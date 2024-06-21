package com.ashiro.ashirooj.judge.codesandbox;

import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeRequest;
import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ashiro
 * @description 代码沙箱接口
 */

public interface CodeSandBox {

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
