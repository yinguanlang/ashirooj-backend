package com.ashiro.ashirooj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论表
 * @TableName question_comment
 */
@TableName(value ="question_comment")
@Data
public class QuestionComment implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 点赞数
     */
    private Integer favourNum;
    /**
     * 评论问题id
     */
    private Long questionId;

    /**
     * 审核状态（0待审核，1,审核通过，2审核不通过）
     */
    private Integer status;

    /**
     * 评论父级id
     */
    private Long parentId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新用户id
     */
    private String updateUserId;

    /**
     * 逻辑删除值
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}