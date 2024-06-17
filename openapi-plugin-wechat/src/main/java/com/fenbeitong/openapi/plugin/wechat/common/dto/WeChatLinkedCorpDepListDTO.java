package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: WeChatLinkedCorpDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/1/14 4:04 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatLinkedCorpDepListDTO {

    @JsonProperty("errcode")
    public Integer errcode;

    @JsonProperty("errmsg")
    public String errmsg;

    @JsonProperty("department_list")
    public List<Department> departmentList;

    @Data
    public static class Department {

        @JsonProperty("department_id")
        public String departmentId;

        @JsonProperty("department_name")
        public String departmentName;

        @JsonProperty("parentid")
        public String parentid;

        @JsonProperty("order")
        public Integer order;
    }
}
