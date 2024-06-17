package com.fenbeitong.openapi.plugin.welink.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-04-20 19:21:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeLinkIsvDepartmentsRespDTO {

    private String code;

    private String message;

    private String deptCode;

    private String deptNameCn;

    private String deptNameEn;

    private Integer parentCode;

    private String managerAccount;


}