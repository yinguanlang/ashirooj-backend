package com.ashiro.ashirooj.judge.codesandbox.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ashiro.ashirooj.common.ErrorCode;
import com.ashiro.ashirooj.exception.BusinessException;
import com.ashiro.ashirooj.judge.codesandbox.CodeSandBox;
import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeRequest;
import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * @author ashiro
 *
 * @description 远程代码沙箱（实际调用接口的代码沙箱）
 */
public class RemoteCodeSandBox implements CodeSandBox {
    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String url = "http://192.168.149.141:8090/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String response = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER,AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();

        if (StrUtil.isBlank(response)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"executeCode remoteSandBox error, message = " + response);
        }
        ExecuteCodeResponse bean = JSONUtil.toBean(response, ExecuteCodeResponse.class);
        return bean;
    }
}
