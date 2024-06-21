package com.ashiro.ashirooj.model.vo;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.ashiro.ashirooj.model.dto.question.JudgeConfig;
import com.ashiro.ashirooj.model.entity.Question;
import com.ashiro.ashirooj.model.entity.QuestionComment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author ashiro
 * @description
 */
@Data
public class QuestionCommentVO implements Serializable {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
     * 被回复者的信息
     */
    private UserVO parentUser;

    /**
     * 创建该评论的用户
     */
    private UserVO createUser;



    private List<QuestionCommentVO> children;

    /**
     * 二级评论数目
     */
    private Integer hasChildrenNum;
    /**
     * 包装类转对象
     *
     * @param questionCommentVO
     * @return
     */
    public static QuestionComment voToObj(QuestionCommentVO questionCommentVO) {
        if (questionCommentVO == null) {
            return null;
        }
        QuestionComment questionComment = new QuestionComment();
        BeanUtils.copyProperties(questionCommentVO, questionComment);
        return questionComment;
    }

    /**
     * 对象转包装类
     *
     * @param questionComment
     * @return
     */
    public static QuestionCommentVO objToVo(QuestionComment questionComment) {
        if (questionComment == null) {
            return null;
        }
        QuestionCommentVO questionCommentVO = new QuestionCommentVO();
        BeanUtils.copyProperties(questionComment, questionCommentVO);
        return questionCommentVO;
    }
}
