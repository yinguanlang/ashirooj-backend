package com.ashiro.ashirooj.model.dto.question;

import lombok.Data;

/**
 * @author ashiro
 *
 * @description 题目配置
 */
@Data
public class JudgeConfig {

    /**
     * 时间限制(ms)
     */
    private Long timeLimit;

    /**
     * 消耗内存(KB)
     */
    private Long memoryLimit;

    /**
     * 堆栈内存(KB)
     */
    private Long stackLimit;




}
