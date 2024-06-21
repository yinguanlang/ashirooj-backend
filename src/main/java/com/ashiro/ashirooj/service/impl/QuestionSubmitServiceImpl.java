package com.ashiro.ashirooj.service.impl;

import com.alibaba.fastjson.JSON;
import com.ashiro.ashirooj.common.BaseResponse;
import com.ashiro.ashirooj.common.ErrorCode;
import com.ashiro.ashirooj.common.ResultUtils;
import com.ashiro.ashirooj.constant.CommonConstant;
import com.ashiro.ashirooj.exception.BusinessException;
import com.ashiro.ashirooj.judge.JudgeService;
import com.ashiro.ashirooj.mapper.QuestionSubmitMapper;
import com.ashiro.ashirooj.model.dto.questionsubmit.JudgeInfo;
import com.ashiro.ashirooj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.ashiro.ashirooj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.ashiro.ashirooj.model.entity.*;
import com.ashiro.ashirooj.model.entity.QuestionSubmit;
import com.ashiro.ashirooj.model.entity.QuestionSubmit;
import com.ashiro.ashirooj.model.enums.QuestionSubmitLanguageEnum;
import com.ashiro.ashirooj.model.enums.QuestionSubmitStatusEnum;
import com.ashiro.ashirooj.model.vo.QuestionSubmitResultVO;
import com.ashiro.ashirooj.model.vo.QuestionSubmitVO;
import com.ashiro.ashirooj.service.*;
import com.ashiro.ashirooj.service.QuestionSubmitService;
import com.ashiro.ashirooj.service.QuestionSubmitService;
import com.ashiro.ashirooj.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author ashiro
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-03-31 16:35:56
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {
    @Resource
    private QuestionService questionService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    @Resource
    private UserService userService;


    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    /**
     * 提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public BaseResponse<QuestionSubmitResultVO> doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        //校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionSubmitAddRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (questionSubmitAddRequest.getCode().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码不能为空");
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        long userId = loginUser.getId();
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setQuestionId(questionSubmitAddRequest.getQuestionId());
        questionSubmit.setUserId(userId);
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }

        Long questionSubmitId = questionSubmit.getId();
        CompletableFuture<QuestionSubmitVO> future = null;
        future = CompletableFuture.supplyAsync(() -> {
            QuestionSubmitVO questionSubmit1 = judgeService.doJudge(questionSubmitId);
            return questionSubmit1;
        });

        QuestionSubmitVO questionSubmit1 = null;

        try {
            questionSubmit1 = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        QuestionSubmitResultVO questionSubmitResultVO = new QuestionSubmitResultVO();
        questionSubmitResultVO.setJudgeInfo(questionSubmit1.getJudgeInfo());
        questionSubmitResultVO.setStatus(questionSubmit1.getStatus());
        questionSubmitResultVO.setOutputList(questionSubmit1.getOutputList());
        //需要返回判题状态，以及判题结果
        return ResultUtils.success(questionSubmitResultVO);

    }


    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
//        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
//            questionSubmitVO.setCode(null);
//        }
        return questionSubmitVO;
    }


    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit) {
        //根据用户id查用户
        Long userId = questionSubmit.getUserId();
        User user = userService.getById(userId);
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        questionSubmitVO.setUsername(user.getUserName());
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }


    @Override
    public List<QuestionSubmit> listQuestionSubmitWithDelete(Date minUpdateTime) {
        List<QuestionSubmit> questionSubmits = questionSubmitMapper.selectList(new LambdaQueryWrapper<QuestionSubmit>().ge(minUpdateTime != null, QuestionSubmit::getUpdateTime, minUpdateTime));
        return questionSubmits;
    }


}




