package com.ashiro.ashirooj.controller;

import cn.hutool.json.JSONUtil;
import com.ashiro.ashirooj.annotation.AuthCheck;
import com.ashiro.ashirooj.common.BaseResponse;
import com.ashiro.ashirooj.common.DeleteRequest;
import com.ashiro.ashirooj.common.ErrorCode;
import com.ashiro.ashirooj.common.ResultUtils;
import com.ashiro.ashirooj.constant.UserConstant;
import com.ashiro.ashirooj.exception.BusinessException;
import com.ashiro.ashirooj.exception.ThrowUtils;
import com.ashiro.ashirooj.model.dto.questioncomment.QuestionCommentAddRequest;
import com.ashiro.ashirooj.model.dto.questioncomment.QuestionCommentQueryRequest;
import com.ashiro.ashirooj.model.entity.QuestionComment;
import com.ashiro.ashirooj.model.entity.User;
import com.ashiro.ashirooj.model.vo.QuestionCommentVO;
import com.ashiro.ashirooj.service.QuestionCommentService;
import com.ashiro.ashirooj.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 评论接口
 *
 * @author ashiro
 */
@RestController
@RequestMapping("/question_comment")
@Slf4j
public class QuestionCommentController {

    @Resource
    private QuestionCommentService questionCommentService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param questionCommentAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionComment(@RequestBody QuestionCommentAddRequest questionCommentAddRequest, HttpServletRequest request) {
        if (questionCommentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionComment questionComment = new QuestionComment();
        BeanUtils.copyProperties(questionCommentAddRequest, questionComment);
        User loginUser = userService.getLoginUser(request);
        questionComment.setUserId(loginUser.getId());
        questionComment.setCreateTime(new Date());
        questionComment.setUpdateTime(new Date());
        questionCommentService.validQuestionComment(questionComment, true);
        questionComment.setUserId(loginUser.getId());
        questionComment.setFavourNum(0);
        questionComment.setStatus(1);
        boolean result = questionCommentService.save(questionComment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionCommentId = questionComment.getId();
        return ResultUtils.success(newQuestionCommentId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionComment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //需要删除其子级评论，设置isDelete
        // 判断是否存在
        Boolean isDelete = questionCommentService.deleteComment(deleteRequest.getId());
        return ResultUtils.success(isDelete);
    }

//    /**
//     * 更新（仅管理员）
//     *
//     * @param questionCommentUpdateRequest
//     * @return
//     */
//    @PostMapping("/update")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public BaseResponse<Boolean> updateQuestionComment(@RequestBody QuestionCommentUpdateRequest questionCommentUpdateRequest) {
//        if (questionCommentUpdateRequest == null || questionCommentUpdateRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        QuestionComment questionComment = new QuestionComment();
//        BeanUtils.copyProperties(questionCommentUpdateRequest, questionComment);
//        List<String> tags = questionCommentUpdateRequest.getTags();
//        if (tags != null) {
//            questionComment.setTags(JSONUtil.toJsonStr(tags));
//        }
//        // 参数校验
//        questionCommentService.validQuestionComment(questionComment, false);
//        long id = questionCommentUpdateRequest.getId();
//        // 判断是否存在
//        QuestionComment oldQuestionComment = questionCommentService.getById(id);
//        ThrowUtils.throwIf(oldQuestionComment == null, ErrorCode.NOT_FOUND_ERROR);
//        boolean result = questionCommentService.updateById(questionComment);
//        return ResultUtils.success(result);
//    }

//    /**
//     * 根据 id 获取
//     *
//     * @param id
//     * @return
//     */
//    @GetMapping("/get/vo")
//    public BaseResponse<QuestionCommentVO> getQuestionCommentVOById(long id, HttpServletRequest request) {
//        if (id <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        QuestionComment questionComment = questionCommentService.getById(id);
//        if (questionComment == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        return ResultUtils.success(questionCommentService.getQuestionCommentVO(questionComment, request));
//    }

//    /**
//     * 分页获取列表
//     *
//     * @param questionCommentQueryRequest
//     * @return
//     */
//    @PostMapping("/list/page")
//    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
//    public BaseResponse<Page<QuestionComment>> listQuestionCommentByPage(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest) {
//        long current = questionCommentQueryRequest.getCurrent();
//        long size = questionCommentQueryRequest.getPageSize();
//        Page<QuestionComment> questionCommentPage = questionCommentService.page(new Page<>(current, size),
//                questionCommentService.getQueryWrapper(questionCommentQueryRequest));
//        return ResultUtils.success(questionCommentPage);
//    }

    /**
     * 分页获取一级评论列表（封装类）
     *
     * @param questionCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionCommentVO>> listQuestionCommentVOByPage(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest,
            HttpServletRequest request) {
        long current = questionCommentQueryRequest.getCurrent();
        long size = questionCommentQueryRequest.getPageSize();
        // 限制爬虫
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        LambdaQueryWrapper<QuestionComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(questionCommentQueryRequest.getQuestionId() != null,QuestionComment::getQuestionId, questionCommentQueryRequest.getQuestionId());
        queryWrapper.isNull(QuestionComment::getParentId);
        //hottest :最热， uptodate: 最新， earliest：最早
        if (questionCommentQueryRequest.getSortField().equals("hottest")){
            queryWrapper.orderByDesc(QuestionComment::getFavourNum);
        }else if (questionCommentQueryRequest.getSortField().equals("uptodate")){
            queryWrapper.orderByDesc(QuestionComment::getCreateTime);
        }else {
            queryWrapper.orderByAsc(QuestionComment::getCreateTime);
        }

        Page<QuestionComment> questionCommentPage = questionCommentService.page(new Page<>(current, size),
                queryWrapper);

        return ResultUtils.success(questionCommentService.getQuestionCommentVOPage(questionCommentPage, request));
    }

    /**
     * 查询问题下所有评论（封装类）
     *
     * @param questionCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/comment/sum")
    public BaseResponse<Integer> getQuestionCommentSum(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest,
                                                  HttpServletRequest request) {
        // 限制爬虫
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        return ResultUtils.success(questionCommentService.getQuestionCommentSum(questionCommentQueryRequest));
    }

    /**
     * 根据一级评论获取二级及子评论（封装类）
     *
     * @param questionCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/reply/page/vo")
    public BaseResponse<Page<QuestionCommentVO>> listReplyCommentVOByPage(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest,
                                                                             HttpServletRequest request) {
        //根据评论id,查询parentId为id的，并递归查询各自子评论
        Page<QuestionCommentVO> list = questionCommentService.findSecondComment(questionCommentQueryRequest);
        return ResultUtils.success(list);
    }


//    /**
//     * 分页获取当前用户创建的资源列表
//     *
//     * @param questionCommentQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/my/list/page/vo")
//    public BaseResponse<Page<QuestionCommentVO>> listMyQuestionCommentVOByPage(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest,
//            HttpServletRequest request) {
//        if (questionCommentQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getLoginUser(request);
//        questionCommentQueryRequest.setUserId(loginUser.getId());
//        long current = questionCommentQueryRequest.getCurrent();
//        long size = questionCommentQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<QuestionComment> questionCommentPage = questionCommentService.page(new Page<>(current, size),
//                questionCommentService.getQueryWrapper(questionCommentQueryRequest));
//        return ResultUtils.success(questionCommentService.getQuestionCommentVOPage(questionCommentPage, request));
//    }

    // endregion
//
//    /**
//     * 分页搜索（从 ES 查询，封装类）
//     *
//     * @param questionCommentQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/search/page/vo")
//    public BaseResponse<Page<QuestionCommentVO>> searchQuestionCommentVOByPage(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest,
//            HttpServletRequest request) {
//        long size = questionCommentQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<QuestionComment> questionCommentPage = questionCommentService.searchFromEs(questionCommentQueryRequest);
//        return ResultUtils.success(questionCommentService.getQuestionCommentVOPage(questionCommentPage, request));
//    }

//    /**
//     * 编辑（用户）
//     *
//     * @param questionCommentEditRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/edit")
//    public BaseResponse<Boolean> editQuestionComment(@RequestBody QuestionCommentEditRequest questionCommentEditRequest, HttpServletRequest request) {
//        if (questionCommentEditRequest == null || questionCommentEditRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        QuestionComment questionComment = new QuestionComment();
//        BeanUtils.copyProperties(questionCommentEditRequest, questionComment);
//        List<String> tags = questionCommentEditRequest.getTags();
//        if (tags != null) {
//            questionComment.setTags(JSONUtil.toJsonStr(tags));
//        }
//        // 参数校验
//        questionCommentService.validQuestionComment(questionComment, false);
//        User loginUser = userService.getLoginUser(request);
//        long id = questionCommentEditRequest.getId();
//        // 判断是否存在
//        QuestionComment oldQuestionComment = questionCommentService.getById(id);
//        ThrowUtils.throwIf(oldQuestionComment == null, ErrorCode.NOT_FOUND_ERROR);
//        // 仅本人或管理员可编辑
//        if (!oldQuestionComment.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
//        boolean result = questionCommentService.updateById(questionComment);
//        return ResultUtils.success(result);
//    }

}
