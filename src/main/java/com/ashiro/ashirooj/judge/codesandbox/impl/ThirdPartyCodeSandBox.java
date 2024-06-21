package com.ashiro.ashirooj.judge.codesandbox.impl;

import com.ashiro.ashirooj.judge.codesandbox.CodeSandBox;
import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeRequest;
import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * @author ashiro
 *
 * @description 第三方代码沙箱（调用现成的代码沙箱）
 */
public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
