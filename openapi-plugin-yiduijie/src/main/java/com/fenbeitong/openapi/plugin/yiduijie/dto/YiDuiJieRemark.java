package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: YiDuiJieRemark</p>
 * <p>Description: 易对接配置备注</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/14 7:59 PM
 */
@Data
public class YiDuiJieRemark {

    private String username;

    private String password;

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("app_title")
    private String appTitle;

    @JsonProperty("company_name")
    private String companyName;

    private String email;
}
