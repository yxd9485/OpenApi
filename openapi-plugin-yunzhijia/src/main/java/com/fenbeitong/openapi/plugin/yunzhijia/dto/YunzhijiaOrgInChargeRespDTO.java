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
public class YunzhijiaOrgInChargeRespDTO {

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
    private YunzhijiaOrgInChargeDTO data;

    @Data
    public static class YunzhijiaOrgInChargeDTO {
        //部门名称
        private String name;
        //部门ID
        private String orgId;
        //父部门ID
        private String parentId;
        //部门状态
        private String status;
        //部门负责人信息
        private List<YunzhijiaEmployeeDTO> inChargers;


    }
}
