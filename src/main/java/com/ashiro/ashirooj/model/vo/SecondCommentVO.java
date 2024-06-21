package com.ashiro.ashirooj.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author ashiro
 * @description
 */
@Data
public class SecondCommentVO {

    private Long id;

    private String content;

    private Long parentId;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 评论用户
     */
    private Long userId;

    private List<SecondCommentVO> children;
}
