package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QIqiCommonReqData
 * @Description 企企公共请求报文
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/5/14 下午3:34
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiCommonReqData {
    @JsonProperty("object_type")
    private String  objectType;
    @JsonProperty("criteria_str")
    private String criteriaStr;
    @JsonProperty("fields")
    private Object[] fields;
}
