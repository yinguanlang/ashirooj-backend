package com.ashiro.ashirooj.model.dto.questionsubmit;

import com.ashiro.ashirooj.common.PageRequest;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 提交列表查询对象
 *
 * @author ashiro
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {


    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交状态
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题目 id
     */
    private Long userId;


    private static final long serialVersionUID = 1L;

}