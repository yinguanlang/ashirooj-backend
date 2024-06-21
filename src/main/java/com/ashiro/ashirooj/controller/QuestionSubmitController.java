package com.ashiro.ashirooj.controller;

import com.ashiro.ashirooj.annotation.AuthCheck;
import com.ashiro.ashirooj.common.BaseResponse;
import com.ashiro.ashirooj.common.ErrorCode;
import com.ashiro.ashirooj.common.ResultUtils;
import com.ashiro.ashirooj.constant.UserConstant;
import com.ashiro.ashirooj.exception.BusinessException;
import com.ashiro.ashirooj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.ashiro.ashirooj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.ashiro.ashirooj.model.dto.user.UserQueryRequest;
import com.ashiro.ashirooj.model.entity.QuestionSubmit;
import com.ashiro.ashirooj.model.entity.User;
import com.ashiro.ashirooj.model.vo.QuestionSubmitVO;
import com.ashiro.ashirooj.service.QuestionSubmitService;
import com.ashiro.ashirooj.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 * @author ashiro
 */
@RestController
//@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

//    @Resource
//    private QuestionSubmitService questionSubmitService;
//
//    @Resource
//    private UserService userService;
//
//    /**
//     * 提交题目
//     *
//     * @param questionSubmitAddRequest
//     * @param request
//     * @return result 提交题目的id
//     */
//    @PostMapping("/")
//    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
//            HttpServletRequest request) {
//        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        // 登录才能点赞
//        final User loginUser = userService.getLoginUser(request);
//        long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
//        return ResultUtils.success(result);
//    }
//
//
//    /**
//     * 分页获取题目提交列表（除管理员外，普通用户只能查看其本人提交结果）
//     *
//     * @param questionSubmitQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/list/page")
//    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
//                                                                         HttpServletRequest request) {
//        long current = questionSubmitQueryRequest.getCurrent();
//        long size = questionSubmitQueryRequest.getPageSize();
//        // 从数据库中查询原始的题目提交分页信息
//        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
//                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
//        final User loginUser = userService.getLoginUser(request);
//        // 返回脱敏信息
//        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
//    }

}
