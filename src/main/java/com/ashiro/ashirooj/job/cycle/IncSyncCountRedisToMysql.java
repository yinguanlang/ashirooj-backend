package com.ashiro.ashirooj.job.cycle;

import cn.hutool.core.collection.CollUtil;
import com.ashiro.ashirooj.constant.RedisConstant;
import com.ashiro.ashirooj.mapper.PostMapper;
import com.ashiro.ashirooj.mapper.QuestionMapper;
import com.ashiro.ashirooj.mapper.QuestionSubmitMapper;
import com.ashiro.ashirooj.model.dto.post.PostEsDTO;
import com.ashiro.ashirooj.model.entity.Post;
import com.ashiro.ashirooj.model.entity.Question;
import com.ashiro.ashirooj.model.entity.QuestionSubmit;
import com.ashiro.ashirooj.service.QuestionService;
import com.ashiro.ashirooj.service.QuestionSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ashiro
 * @description 同步有提交记录的题目进数据库
 */
//@Component
@Slf4j
public class IncSyncCountRedisToMysql {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Resource
    private RedisTemplate<Object,Object> redisTemplate;
    /**
     * 30 分钟执行一次
     */
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void run() {
        // 查询近 30 分钟内的数据
        Date minutesAgoDate = new Date(new Date().getTime() - 30 * 60 * 1000L);
        List<QuestionSubmit> questionSubmitList = questionSubmitService.listQuestionSubmitWithDelete(minutesAgoDate);
        //有新的提交记录
        if (!CollUtil.isEmpty(questionSubmitList)) {
            //根据questionId查redis数据
            List<Long> questionIdList = questionSubmitList.stream().map(QuestionSubmit::getQuestionId).distinct().collect(Collectors.toList());
            for (int i = 0; i < questionIdList.size(); i++) {
                Integer submitCount = null;
                Integer submitPassCount = null;
                try {
                    Map<Object, Object> entries = redisTemplate.boundHashOps(RedisConstant.QUESTION_ID_KEY + questionIdList.get(i)).entries();
                    submitCount = (Integer) entries.get(RedisConstant.SUBMIT_COUNT_KEY);
                    submitPassCount = (Integer) entries.get(RedisConstant.SUBMIT_PASS_COUNT_KEY);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                //更新数据到数据库
                Question question = new Question();
                question.setId(questionIdList.get(i));
                question.setSubmitNum(submitCount);
                question.setAcceptedNum(submitPassCount);
                questionService.updateById(question);
            }
            return;
        }
        log.info("inSy success");
    }

}
