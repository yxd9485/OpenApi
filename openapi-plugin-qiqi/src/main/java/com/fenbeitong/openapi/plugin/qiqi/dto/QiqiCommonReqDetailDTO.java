package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QiqiCommonReqDetail
 * @Description 企企请求公共报文（树形结构）
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/6/14
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiCommonReqDetailDTO {
    @JsonProperty("fieldName")
    private String  fieldName;
    @JsonProperty("fields")
    private Object[] fields;

}
