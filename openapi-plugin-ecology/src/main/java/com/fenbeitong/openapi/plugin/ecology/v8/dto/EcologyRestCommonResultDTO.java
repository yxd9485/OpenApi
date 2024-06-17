package com.fenbeitong.openapi.plugin.ecology.v8.dto;

import lombok.Data;

/**
 * 通用返回结果
 * @Auther zhang.peng
 * @Date 2022/1/5
 */
@Data
public class EcologyRestCommonResultDTO {

    private String code;

    private Object errMsg;
}
