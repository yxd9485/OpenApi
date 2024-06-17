package com.fenbeitong.openapi.plugin.customize.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ctl
 * @date 2021/10/29
 */
@Data
public class CustomExcelDTO implements Serializable {

    private String companyId;

    private String employeeId;

    private String expand;
}
