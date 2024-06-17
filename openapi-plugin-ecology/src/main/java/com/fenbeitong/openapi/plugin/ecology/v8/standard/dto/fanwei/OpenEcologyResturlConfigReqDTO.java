package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import javax.validation.constraints.NotNull;

/**
 * Created by zhangpeng on 2021/12/31.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenEcologyResturlConfigReqDTO {
    /**
     * 公司ID
     */
    @JsonProperty("company_id")
    private String companyId;
    /**
     * 泛微颁发的APPID，用于rest接口调用
     */
    @JsonProperty("ecology_app_id")
    private String ecologyAppId;
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
    /**
     * 接口描述
     */
    @JsonProperty("desc")
    private String desc;
}
