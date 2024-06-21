package com.ashiro.ashirooj.model.vo;

import cn.hutool.json.JSONUtil;
import com.ashiro.ashirooj.model.dto.question.CodeTemplate;
import com.ashiro.ashirooj.model.dto.question.JudgeCase;
import com.ashiro.ashirooj.model.dto.question.JudgeConfig;
import com.ashiro.ashirooj.model.dto.question.MetaData;
import com.ashiro.ashirooj.model.entity.Question;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目
 * @TableName question
 */
@Data
public class QuestionVO implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 题解
     */
    private String answer;

    /**
     * 说明
     */
    private String description;

    /**
     * 代码元数据
     */
    private MetaData metaData;

    /**
     * 代码模板
     */
    private List<CodeTemplate> codeTemplate;/**

//     * 代码主函数
//     */
//    private List<CodeTemplate> mainCode;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

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
     * 判题用例
     */
    private List<JudgeCase> judgeCasesList;


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

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
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
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setTags(JSONUtil.toList(question.getTags(), String.class));
        String judgeConfig1 = question.getJudgeConfig();
        String codeTemplate1 = question.getCodeTemplate();
        questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfig1,JudgeConfig.class));
        return questionVO;
    }
}