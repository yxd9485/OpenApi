package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaOrgRespDTO {

    /**
     * 返回信息,是否成功标识
     */
    @JsonProperty("success")
    private boolean success;
    /**
     * 返回code标识
     */
    @JsonProperty("errorCode")
    private Integer errorCode;

    /**
     * 错误信息,success=false时携带此信息
     */
    @JsonProperty("error")
    private boolean error;

    @JsonProperty("data")
    private List<YunzhijiaOrgDTO> data;

    @Data
    public static class YunzhijiaOrgDTO{
        //部门全名称,以\符号进行分割 如：分贝通\研发部
        private String department;
        //部门ID 如：02008582-08dc-40a0-8d5b-c693f73d2798
        private String id;
        //部门名称 如：研发部
        private String name;
        //父部门ID 如：1112e731-99f8-4ae2-8416-8fdee4fe067e
        private String parentId;
        //权重排序 如：101000
        private int weights;
        //部门ID，在查询公司部门列表时使用
        private String orgId;
        //部门负责人信息
        private  List<YunzhijiaEmployeeDTO> inChargers;
    }

}
