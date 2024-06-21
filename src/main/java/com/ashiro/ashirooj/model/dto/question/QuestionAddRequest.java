package com.ashiro.ashirooj.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建请求
 *
 * @author ashiro
 */
@Data
public class QuestionAddRequest implements Serializable {

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
     * 题目说明
     */
    private String description;

    /**
     * 代码元数据
     */
    private MetaData metaData;

    /**
     * 代码模板
     */
    private List<CodeTemplate> codeTemplate;

    /**
     * 代码主函数
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




    private static final long serialVersionUID = 1L;
}