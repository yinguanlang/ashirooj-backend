package com.ashiro.ashirooj.judge;

import com.ashiro.ashirooj.judge.srategy.*;
import com.ashiro.ashirooj.model.dto.questionsubmit.JudgeInfo;
import com.ashiro.ashirooj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

import javax.swing.*;

/**
 * @author ashiro
 * @description 判题策略管理（简化调用）
 */
@Service
public class JudgeManager {
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new JavaLanguageJudgeStrategy();
        if ("java".equals(language)){
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        if ("cpp".equals(language)){
            judgeStrategy = new CppLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
