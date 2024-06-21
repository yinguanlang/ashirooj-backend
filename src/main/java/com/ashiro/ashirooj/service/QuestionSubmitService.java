package com.ashiro.ashirooj.service;

import com.ashiro.ashirooj.common.BaseResponse;
import com.ashiro.ashirooj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.ashiro.ashirooj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.ashiro.ashirooj.model.entity.QuestionSubmit;
import com.ashiro.ashirooj.model.entity.User;
import com.ashiro.ashirooj.model.vo.QuestionSubmitResultVO;
import com.ashiro.ashirooj.model.vo.QuestionSubmitVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;


/**
* @author ashiro
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-03-31 16:35:56
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionId
     * @param loginUser
     * @return
     */
    BaseResponse<QuestionSubmitResultVO> doQuestionSubmit(QuestionSubmitAddRequest questionId, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage);


    /**
     * 查询提交列表（包括已被删除的数据）
     */
    List<QuestionSubmit> listQuestionSubmitWithDelete(Date minUpdateTime);
}
