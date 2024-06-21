package com.ashiro.ashirooj.model.dto.questioncomment;

import com.ashiro.ashirooj.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ashiro
 * @description
 */
@Data
public class QuestionCommentQueryRequest extends PageRequest implements Serializable {

    /**
     * 评论id
     */
    private Long id;


    /**
     * 题目id
     */
    private Long questionId;


    /**
     * 用户id
     */
    private Long userId;


    /**
     * 父级评论id
     */
    private Long parentId;


    /**
     * 排序字段 ， hottest :最热， uptodate: 最新， earliest：最早
     */
    private String sortField;

    private static final long serialVersionUID = 1L;

}
