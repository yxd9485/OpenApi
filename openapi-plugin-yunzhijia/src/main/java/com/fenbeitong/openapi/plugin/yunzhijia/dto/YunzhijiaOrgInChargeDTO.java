package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaOrgInChargeDTO {
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
