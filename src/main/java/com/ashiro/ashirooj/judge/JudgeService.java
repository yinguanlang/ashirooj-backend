package com.ashiro.ashirooj.judge;

import com.ashiro.ashirooj.common.BaseResponse;
import com.ashiro.ashirooj.model.entity.QuestionSubmit;
import com.ashiro.ashirooj.model.vo.QuestionSubmitVO;

/**
 * @author ashiro
 * @description 判题服务
 */
public interface JudgeService {

    /**
     *
     * @param questionSubmitId
     * @return
     */
    QuestionSubmitVO doJudge(long questionSubmitId);
}
