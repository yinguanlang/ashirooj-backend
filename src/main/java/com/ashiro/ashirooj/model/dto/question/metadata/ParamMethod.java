package com.ashiro.ashirooj.model.dto.question.metadata;

import lombok.Data;

import java.util.List;

/**
 * @author ashiro
 * @description
 */
@Data
public class ParamMethod {
    private String name;

    private List<ParamData> params;

    private MethodReturn methodReturn;
}
