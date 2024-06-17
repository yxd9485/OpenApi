package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 泛微部门详情
 * 字段描述见 < a https://e-cloudstore.com/doc.html?appId=c373a4b01fb74d098b62e2b969081d2d />
 *
 * @author ctl
 * @date 2021/11/15
 */
@Data
public class EcologyDepartmentInfo implements Serializable {
    @JsonProperty("canceled")
    private String canceled;
    @JsonProperty("supdepid")
    private String supdepid;
    @JsonProperty("departmentmark")
    private String departmentmark;
    @JsonProperty("departmentname")
    private String departmentname;
    @JsonProperty("created")
    private String created;
    @JsonProperty("departmentcode")
    private String departmentcode;
    @JsonProperty("modified")
    private String modified;
    @JsonProperty("id")
    private String id;
    @JsonProperty("subcompanyid1")
    private String subcompanyid1;
    @JsonProperty("custom_data")
    private Map<String, Object> customData;
    @JsonProperty("showorder")
    private String showorder;
}
