package com.ashiro.ashirooj.judge.codesandbox.impl;

import com.ashiro.ashirooj.judge.codesandbox.CodeSandBox;
import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeRequest;
import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeResponse;
import com.ashiro.ashirooj.model.dto.questionsubmit.JudgeInfo;
import com.ashiro.ashirooj.model.enums.JudgeInfoMessageEnum;
import com.ashiro.ashirooj.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * @author ashiro
 *
 * @description 示例代码沙箱
 */
public class ExampleCodeSandBox implements CodeSandBox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
