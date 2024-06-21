package com.ashiro.ashirooj.model.dto.question;

import com.ashiro.ashirooj.model.dto.question.metadata.Constructor;
import com.ashiro.ashirooj.model.dto.question.metadata.ParamMethod;
import lombok.Data;

import java.util.List;

/**
 * @author ashiro
 * @description
 */
@Data
public class MetaData {

    private String classname;

    private String hasConstructor;

    private Constructor constructor;

    private List<ParamMethod> methods;
}
