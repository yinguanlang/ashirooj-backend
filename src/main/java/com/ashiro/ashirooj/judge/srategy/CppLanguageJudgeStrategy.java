package com.ashiro.ashirooj.judge.srategy;

/**
 * @author ashiro
 * @description
 */

import cn.hutool.json.JSONUtil;
import com.ashiro.ashirooj.model.dto.question.JudgeCase;
import com.ashiro.ashirooj.model.dto.question.JudgeConfig;
import com.ashiro.ashirooj.model.dto.questionsubmit.JudgeInfo;
import com.ashiro.ashirooj.model.entity.Question;
import com.ashiro.ashirooj.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;

/**
 * Java 程序的判题策略
 */
public class CppLanguageJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);
        //编译错误直接返回
        if (judgeInfo.getMessage() != null && judgeInfo.getMessage().equals(JudgeInfoMessageEnum.COMPILE_ERROR.getValue())) {
            judgeInfoResponse.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            return judgeInfoResponse;
        }

        // 先判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if (memory > needMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            judgeInfoResponse.setMemory(memory);
            return judgeInfoResponse;
        }
        // 假设Java 程序本身需要额外执行 10 秒钟，需要减去10秒
//        long JAVA_PROGRAM_TIME_COST = 10000L;
        if (time > needTimeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            judgeInfoResponse.setTime(time);
            return judgeInfoResponse;
        }

        // 先判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (outputList != null && outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // 依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            String result = outputList.get(i);
            String replace = result.replace("\n", "");
            if (!judgeCase.getOutput().equals(replace)) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }

        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}

