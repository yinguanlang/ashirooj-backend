package com.ashiro.ashirooj.judge.srategy;

import com.ashiro.ashirooj.model.dto.questionsubmit.JudgeInfo;

/**
 * @author ashiro
 * @description 判题策略
 */
public interface JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
