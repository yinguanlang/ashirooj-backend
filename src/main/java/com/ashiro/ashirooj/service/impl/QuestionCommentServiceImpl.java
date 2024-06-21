package com.ashiro.ashirooj.service.impl;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.ashiro.ashirooj.common.ErrorCode;
import com.ashiro.ashirooj.exception.BusinessException;
import com.ashiro.ashirooj.exception.ThrowUtils;
import com.ashiro.ashirooj.mapper.QuestionCommentMapper;
import com.ashiro.ashirooj.mapper.QuestionMapper;
import com.ashiro.ashirooj.model.dto.questioncomment.QuestionCommentQueryRequest;
import com.ashiro.ashirooj.model.entity.Question;
import com.ashiro.ashirooj.model.entity.QuestionComment;
import com.ashiro.ashirooj.model.vo.QuestionCommentVO;
import com.ashiro.ashirooj.model.vo.UserVO;
import com.ashiro.ashirooj.service.QuestionCommentService;
import com.ashiro.ashirooj.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ashiro
 * @description 针对表【question_comment(评论表)】的数据库操作Service实现
 */
@Service
public class QuestionCommentServiceImpl extends ServiceImpl<QuestionCommentMapper, QuestionComment>
        implements QuestionCommentService {

    @Autowired
    private QuestionCommentMapper questionCommentMapper;

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public void validQuestionComment(QuestionComment questionComment, boolean add) {
        if (questionComment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = questionComment.getUserId();
        String content = questionComment.getContent();
        Long questionId = questionComment.getQuestionId();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content), ErrorCode.PARAMS_ERROR);
        }
        if (add) {
            ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR);
        }
        if (add) {
            ThrowUtils.throwIf(questionId == null, ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 1000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容过长");
        }
    }

    /**
     * 查一级评论
     * @param questionCommentPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionCommentVO> getQuestionCommentVOPage(Page<QuestionComment> questionCommentPage, HttpServletRequest request) {
        List<QuestionComment> questionCommentsList = questionCommentPage.getRecords();
        Page<QuestionCommentVO> questionCommentVOPage = new Page<>(questionCommentPage.getCurrent(), questionCommentPage.getSize(), questionCommentPage.getTotal());
        if (CollUtil.isEmpty(questionCommentsList)) {
            return questionCommentVOPage;
        }
        // 填充信息
        AtomicReference<Long> total = new AtomicReference<>(questionCommentVOPage.getTotal());
        List<QuestionCommentVO> questionCommentVOList = questionCommentsList.stream().map(questionComment -> {
            Long userId = questionComment.getUserId();
            //获取创建者信息
            UserVO userVO = userService.getUserVO(userId);
            QuestionCommentVO questionCommentVO = QuestionCommentVO.objToVo(questionComment);
            questionCommentVO.setCreateUser(userVO);
            //查询有多少条子回复评论
            List<QuestionCommentVO> questionCommentVOS = new ArrayList<>();
            setChildren(questionComment.getId(),questionCommentVOS);
            questionCommentVO.setHasChildrenNum(questionCommentVOS.size());
            total.updateAndGet(v -> v + questionCommentVOS.size());
            return questionCommentVO;
        }).collect(Collectors.toList());
        questionCommentVOPage.setRecords(questionCommentVOList);
        return questionCommentVOPage;
    }


    /**
     * 根据id查询二级
     * @return
     */
    @Override
    public Page<QuestionCommentVO> findSecondComment(QuestionCommentQueryRequest questionCommentQueryRequest) {
        Long id = questionCommentQueryRequest.getId();
        LambdaQueryWrapper<QuestionComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionComment::getParentId, id);
        queryWrapper.eq(QuestionComment::getIsDelete, 0);
        queryWrapper.orderByDesc(QuestionComment::getFavourNum);
        //查询该问题下评论，该一级评论下的二级评论
        List<QuestionComment> questionComments = questionCommentMapper.selectList(queryWrapper);
        //该一级评论没有二级评论
        if (questionComments == null || questionComments.size() == 0) {
            long current = questionCommentQueryRequest.getCurrent();
            long size = questionCommentQueryRequest.getPageSize();
            Page<QuestionCommentVO> questionCommentPage = new Page<>(current, size);
            questionCommentPage.setTotal(0);
            return questionCommentPage;
        }
        //存在二级评论
        List<QuestionCommentVO> questionCommentVOS = new ArrayList<>();
        //直接根据评论id递归返回二级评论及以下评论集合
        setChildren(id,questionCommentVOS);
        //数据处理
        int current = questionCommentQueryRequest.getCurrent();
        int size = questionCommentQueryRequest.getPageSize();
        Page<QuestionCommentVO> questionCommentPage = new Page<>(current, size);
        Collections.sort(questionCommentVOS,Comparator.comparing(QuestionCommentVO::getCreateTime, (t1, t2) -> t2.compareTo(t1)));
        questionCommentPage.setTotal(questionCommentVOS.size());

        int fromIndex = (current - 1) * size;
        int toIndex = fromIndex + size;
        if (toIndex > questionCommentVOS.size()) {
            toIndex = questionCommentVOS.size();
        }
        List<QuestionCommentVO> collect1 = questionCommentVOS.stream().sorted(Comparator.comparing(QuestionCommentVO::getCreateTime)).collect(Collectors.toList());
        List<QuestionCommentVO> page = collect1.subList(fromIndex, toIndex);
        questionCommentPage.setRecords(page);

        return questionCommentPage;
    }

    /**
     * 删除评论
     * @param id
     * @return
     */
    @Override
    public Boolean deleteComment(Long id) {
        QuestionComment questionComment1 = new QuestionComment();
        questionComment1.setId(id);
        questionComment1.setIsDelete(1);
        questionCommentMapper.updateById(questionComment1);
        List<QuestionComment> questionComments = questionCommentMapper.selectList(new LambdaQueryWrapper<QuestionComment>().eq(QuestionComment::getParentId, id));
        if (questionComments.isEmpty()) {
            return true;
        }
        //存在子评论
        for (QuestionComment questionComment : questionComments) {
            deleteCommentChildren(questionComment);
        }
        return true;
    }

    /**
     * 获取评论总数
     *
     * @param questionCommentQueryRequest
     * @return
     */
    @Override
    public Integer getQuestionCommentSum(QuestionCommentQueryRequest questionCommentQueryRequest) {
        Long questionId = questionCommentQueryRequest.getQuestionId();
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目不存在");
        }
        LambdaQueryWrapper<QuestionComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(questionCommentQueryRequest.getQuestionId() != null,QuestionComment::getQuestionId, questionId);
        queryWrapper.isNull(QuestionComment::getParentId);
        List<QuestionComment> questionCommentList = questionCommentMapper.selectList(queryWrapper);
        // 填充信息
        AtomicReference<Integer> total = new AtomicReference<>(questionCommentList.size());
        List<QuestionCommentVO> questionCommentVOList = questionCommentList.stream().map(questionComment -> {
            Long userId = questionComment.getUserId();
            //获取创建者信息
            UserVO userVO = userService.getUserVO(userId);
            QuestionCommentVO questionCommentVO = QuestionCommentVO.objToVo(questionComment);
            questionCommentVO.setCreateUser(userVO);
            //查询有多少条子回复评论
            List<QuestionCommentVO> questionCommentVOS = new ArrayList<>();
            setChildren(questionComment.getId(),questionCommentVOS);
            questionCommentVO.setHasChildrenNum(questionCommentVOS.size());
            total.updateAndGet(v -> v + questionCommentVOS.size());
            return questionCommentVO;
        }).collect(Collectors.toList());
        return total.get();
    }

    private void deleteCommentChildren(QuestionComment questionComment) {
        //根据父id查询子评论
        LambdaQueryWrapper<QuestionComment> rootCommentsWrapper = new LambdaQueryWrapper<>();
        rootCommentsWrapper.eq(QuestionComment::getParentId,questionComment.getId());
        List<QuestionComment> children = this.list(rootCommentsWrapper);
        //遍历子结点
        if (!children.isEmpty()) {
            for (QuestionComment child : children) {
                child.setIsDelete(1);
                questionCommentMapper.updateById(child);
                deleteCommentChildren(child);
            }
        }

    }


    public void setChildren(Long id,List<QuestionCommentVO> questionCommentVOS) {
        //根据二级评论id查询子评论
        LambdaQueryWrapper<QuestionComment> rootCommentsWrapper = new LambdaQueryWrapper<>();
        rootCommentsWrapper.eq(QuestionComment::getParentId,id);
        List<QuestionComment> comments = this.list(rootCommentsWrapper);
        for (QuestionComment comment : comments) {
            QuestionCommentVO questionCommentVO1 = new QuestionCommentVO().objToVo(comment);
            //需要添加ParentUser,CreateUser
            //添加评论者以及被评论者信息
            UserVO userVO = userService.getUserVO(questionCommentVO1.getUserId());
            QuestionComment questionComment1 = questionCommentMapper.selectById(questionCommentVO1.getParentId());
            UserVO parentUserVO = userService.getUserVO(questionComment1.getUserId());
            questionCommentVO1.setCreateUser(userVO);
            questionCommentVO1.setParentUser(parentUserVO);
            questionCommentVOS.add(questionCommentVO1);
        }
        //遍历子结点
        if (!comments.isEmpty()) {
            for (QuestionComment child : comments) {
                setChildren(child.getId(),questionCommentVOS);
            }
        }
    }
}




