package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by zhangpeng on 2021/12/14.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenEcologyResturlConfigDTO {
    /**
     * 公司ID
     */
    @JsonProperty("company_id")
    private String companyId;
    /**
     * 域名
     */
    @JsonProperty("domain_name")
    private String domainName;
    /**
     * 接口地址
     */
    @JsonProperty("url")
    private String url;
    /**
     * 接口类型
     */
    @JsonProperty("type")
    private String type;
}
