package com.ashiro.ashirooj.judge.codesandbox;

import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeRequest;
import com.ashiro.ashirooj.judge.codesandbox.model.ExecuteCodeResponse;
import com.ashiro.ashirooj.model.enums.QuestionSubmitLanguageEnum;
import com.github.mustachejava.Code;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author ashiro
 * @description
 */
@SpringBootTest
class CodeSandboxTest {

    @Value("${codesandbox.type:example}")
    private String type;
    @Test
    void executed(){
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        String code = "{int main {}}";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2","3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
    }

    @Test
    void executedProxy(){
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        String code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(\"结果：\" + (a + b));\n" +
                "    }\n" +
                "}\n";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2","3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }
}
