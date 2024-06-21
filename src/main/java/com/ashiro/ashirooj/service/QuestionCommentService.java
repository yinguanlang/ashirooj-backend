package com.ashiro.ashirooj.service;

import com.ashiro.ashirooj.model.dto.questioncomment.QuestionCommentQueryRequest;
import com.ashiro.ashirooj.model.entity.QuestionComment;
import com.ashiro.ashirooj.model.vo.QuestionCommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author ashiro
* @description 针对表【question_comment(评论表)】的数据库操作Service
*/
public interface QuestionCommentService extends IService<QuestionComment> {

    /**
     * 校验
     *
     * @param questionComment
     * @param add
     */
    void validQuestionComment(QuestionComment questionComment, boolean add);


    /**
     * 分页获取一级分页评论封装
     *
     * @param questionCommentPage
     * @param request
     * @return
     */
    Page<QuestionCommentVO> getQuestionCommentVOPage(Page<QuestionComment> questionCommentPage, HttpServletRequest request);


    /**
     * 查询二级评论
     * @param questionCommentQueryRequest
     * @return
     */
    Page<QuestionCommentVO> findSecondComment(QuestionCommentQueryRequest questionCommentQueryRequest);

    /**
     * 删除评论
     * @param id
     * @return
     */
    Boolean deleteComment(Long id);

    /**
     * 获取所有评论数目
     *
     * @param questionCommentQueryRequest
     * @return
     */
    Integer getQuestionCommentSum(QuestionCommentQueryRequest questionCommentQueryRequest);
}
