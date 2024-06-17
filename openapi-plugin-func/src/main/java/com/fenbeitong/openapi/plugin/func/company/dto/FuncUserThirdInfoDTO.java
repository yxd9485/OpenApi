package com.fenbeitong.openapi.plugin.func.company.dto;

import lombok.Data;

/**
 * <p>Title: FuncUserThirdInfoDTO</p>
 * <p>Description: 人员三方信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/20 11:14 AM
 */
@Data
public class FuncUserThirdInfoDTO {

    private String thirdEmployeeId;

    private String thirdDeptId;

    private String email;

    private String expand;
}
