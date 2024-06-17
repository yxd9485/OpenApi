package com.fenbeitong.openapi.plugin.func.company.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: FuncBillExtInfoParamDTO</p>
 * <p>Description: 账单扩展字段http参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/4 6:45 PM
 */
@Data
public class FuncBillExtInfoParamDTO {

    private String url;

    @JsonProperty("clazz_name")
    private String clazzName;
}
