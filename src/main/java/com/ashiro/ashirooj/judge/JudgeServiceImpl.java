package com.ashiro.ashirooj.judge;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.ashiro.ashirooj.common.ErrorCode;
import com.ashiro.ashirooj.constant.RedisConstant;
import com.ashiro.ashirooj.exception.BusinessException;
import com.ashiro.ashirooj.judge.codesandbox.CodeSandBox;
import com.ashiro.ashirooj.judge.codesandbox.CodeSandBoxFactory;
import com.ashiro.ashirooj.judge.codesandbox.CodeSandBoxProxy;
import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeRequest;
import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeResponse;
import com.ashiro.ashirooj.judge.srategy.JudgeContext;
import com.ashiro.ashirooj.model.dto.question.CodeTemplate;
import com.ashiro.ashirooj.model.dto.question.JudgeCase;
import com.ashiro.ashirooj.model.dto.question.MetaData;
import com.ashiro.ashirooj.model.dto.questionsubmit.JudgeInfo;
import com.ashiro.ashirooj.model.entity.Question;
import com.ashiro.ashirooj.model.entity.QuestionSubmit;
import com.ashiro.ashirooj.model.enums.JudgeInfoMessageEnum;
import com.ashiro.ashirooj.model.enums.QuestionSubmitStatusEnum;
import com.ashiro.ashirooj.model.vo.QuestionSubmitVO;
import com.ashiro.ashirooj.service.QuestionService;
import com.ashiro.ashirooj.service.QuestionSubmitService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ashiro
 * @description
 */
@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Resource
    private JudgeManager judgeManager;

    private final static Gson GSON = new Gson();

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmitVO doJudge(long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        String language = questionSubmit.getLanguage();
        String originString = questionSubmit.getCode();
        String metaData = question.getMetaData();
        ObjectMapper objectMapper = new ObjectMapper();
        MetaData parse = null;
        try {
            parse = objectMapper.readValue(metaData, MetaData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //构造code,添加Main方法
        List<CodeTemplate> mainCodeList = null;
        try {
            mainCodeList = objectMapper.readValue(question.getMainCode(), objectMapper.getTypeFactory().constructCollectionType(List.class, CodeTemplate.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        CodeTemplate maincode = mainCodeList.stream().filter(mainCode ->
            mainCode.getLanguage().equals(language)
        ).findAny().get();
        String code = addMainCode(originString, maincode.getCode());
        //获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .classname(parse.getClassname())
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();


        //5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        //修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        //修改redis的题目提交数，通过数
        //提交通过,则提交数和通过数均加1，首先需要判断是否存在
        String SUBMIT_COUNT = RedisConstant.SUBMIT_COUNT_KEY;
        String SUBMIT_PASS_COUNT = RedisConstant.SUBMIT_PASS_COUNT_KEY;
        String QUESTION_KEY = RedisConstant.QUESTION_ID_KEY + questionId;

        Boolean HAS_SUBMIT_COUNT_KEY = redisTemplate.boundHashOps(QUESTION_KEY).hasKey(SUBMIT_COUNT);
        Boolean HAS_SUBMIT_PASS_COUNT_KEY = redisTemplate.boundHashOps(QUESTION_KEY).hasKey(SUBMIT_COUNT);
        if (HAS_SUBMIT_COUNT_KEY) {
            redisTemplate.boundHashOps(QUESTION_KEY).increment(SUBMIT_COUNT, 1);
        } else {
            //从MySQL数据库查询，更新到redis中
            Integer submitNum = question.getSubmitNum();
            redisTemplate.boundHashOps(QUESTION_KEY).put(SUBMIT_COUNT, submitNum + 1);
        }
        //提交通过
        if (judgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getValue())) {
            if (HAS_SUBMIT_PASS_COUNT_KEY) {
                redisTemplate.boundHashOps(QUESTION_KEY).increment(SUBMIT_PASS_COUNT, 1);
            } else {
                Integer acceptedNum = question.getAcceptedNum();
                redisTemplate.boundHashOps(QUESTION_KEY).put(SUBMIT_PASS_COUNT, acceptedNum + 1);
            }
        }

        QuestionSubmit questionSubmit1 = questionSubmitService.getById(questionSubmitId);
        String judgeInfo1 = questionSubmit1.getJudgeInfo();
        Integer status = questionSubmit1.getStatus();

        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        questionSubmitVO.setJudgeInfo(JSONUtil.toBean(judgeInfo1, JudgeInfo.class));
        questionSubmitVO.setStatus(status);
        questionSubmitVO.setOutputList(outputList);
        return questionSubmitVO;
    }


    /**
     * 最后一个}前添加代码
     *
     * @param originString
     * @param mainCode
     * @return
     */
    public String addMainCode(String originString, String mainCode) {
        int lastIndex = originString.lastIndexOf('}');
        if (lastIndex != -1) {
            // 在}前插入代码
            return originString.substring(0, lastIndex) + mainCode + originString.substring(lastIndex);
        }
        return originString; // $0 represents the part of the string matched by the whole regex
    }
}
