package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 泛微分部详情
 * 字段描述见 < a https://e-cloudstore.com/doc.html?appId=c373a4b01fb74d098b62e2b969081d2d />
 *
 * @author ctl
 * @date 2021/11/15
 */
@Data
public class EcologySubCompanyInfo implements Serializable {
    @JsonProperty("subcompanydesc")
    private String subcompanydesc;
    @JsonProperty("canceled")
    private String canceled;
    @JsonProperty("subcompanycode")
    private String subcompanycode;
    @JsonProperty("supsubcomid")
    private String supsubcomid;
    @JsonProperty("created")
    private String created;
    @JsonProperty("modified")
    private String modified;
    @JsonProperty("id")
    private String id;
    @JsonProperty("subcompanyname")
    private String subcompanyname;
    @JsonProperty("url")
    private String url;
    @JsonProperty("custom_data")
    private Map<String, Object> customData;
    @JsonProperty("showorder")
    private String showorder;
}
