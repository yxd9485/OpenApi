package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: FxiaokeDepartmentSimpleListRespDTO</p>
 * <p>Description: 部门</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-01 16:52
 */
@Data
public class FxiaokeDepartmentRespDTO {

    // 返回码
    @JsonProperty("errorCode")
    public Integer errorCode;


    @JsonProperty("errorDescription")
    public String errorDescription;

    // 对返回码的文本描述内容
    @JsonProperty("errorMessage")
    public String errorMessage;

    @JsonProperty("traceId")
    public String traceId;

    // 二级对象
    @JsonProperty("departments")
    public List<DepartmentInfo> departments;

    @Data
    public static class DepartmentInfo {

        // 部门ID
        @JsonProperty("id")
        public String id;

        // 部门名称
        @JsonProperty("name")
        public String name;

        // 父部门ID，根部门ID为0，其它部门Id为非负整数
        @JsonProperty("parentId")
        public String parentId;

        // 是否停用（true表示停用，false表示正常
        @JsonProperty("isStop")
        public Boolean isStop;

        // 部门排序，序号越小，排序越靠前。最小值为1
        @JsonProperty("order")
        public String order;

    }

}


