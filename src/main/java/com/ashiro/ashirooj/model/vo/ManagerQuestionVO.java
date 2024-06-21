package com.ashiro.ashirooj.model.vo;

import cn.hutool.json.JSONUtil;
import com.ashiro.ashirooj.model.dto.question.CodeTemplate;
import com.ashiro.ashirooj.model.dto.question.JudgeCase;
import com.ashiro.ashirooj.model.dto.question.JudgeConfig;
import com.ashiro.ashirooj.model.dto.question.MetaData;
import com.ashiro.ashirooj.model.entity.Question;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

/**
 * @author ashiro
 * @description
 */
@Data
public class ManagerQuestionVO {
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 代码原数据
     */
    private MetaData metaData;

    /**
     * 代码模板
     */
    private List<CodeTemplate> codeTemplate;

    /**
     * 主函数
     */
    private List<CodeTemplate> mainCode;

    /**
     * 判题用例（json 数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private UserVO userVO;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(ManagerQuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        if (tagList != null){
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig judgeConfig = questionVO.getJudgeConfig();
        if (judgeConfig != null){
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        List<JudgeCase> judgeCase1 = questionVO.getJudgeCase();
        question.setJudgeCase(JSONUtil.toJsonStr(judgeCase1));
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static ManagerQuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        ManagerQuestionVO questionVO = new ManagerQuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setTags(JSONUtil.toList(question.getTags(), String.class));
        String judgeConfig1 = question.getJudgeConfig();
        String judgeCase1 = question.getJudgeCase();
        questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfig1,JudgeConfig.class));
        questionVO.setJudgeCase(JSONUtil.toList(judgeCase1,JudgeCase.class));
        return questionVO;
    }
}
