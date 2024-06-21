package com.ashiro.ashirooj.model.dto.questioncomment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ashiro
 * @description
 */
@Data
public class QuestionCommentAddRequest implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论问题id
     */
    private Long questionId;

    /**
     * 评论父级id
     */
    private Long parentId;

    /**
     * 备注
     */
    private String remark;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
