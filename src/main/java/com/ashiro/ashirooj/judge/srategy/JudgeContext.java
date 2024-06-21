package com.ashiro.ashirooj.judge.srategy;

import com.ashiro.ashirooj.model.dto.question.JudgeCase;
import com.ashiro.ashirooj.model.dto.questionsubmit.JudgeInfo;
import com.ashiro.ashirooj.model.entity.Question;
import com.ashiro.ashirooj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * @author ashiro
 * @description 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
