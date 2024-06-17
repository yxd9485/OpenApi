package com.fenbeitong.openapi.plugin.definition.dto.auto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CorpAutoCheckDTO {

    /**
     * 检查类型,前期只包含钉钉
     */
    private Integer checkType;
    /**
     * 公司ID
     */
    private String companyId;
    /**
     * 第三方公司ID
     */
    private String thirdCompanyId;
    /**
     * 公司名称
     */
    private String fbtCompName;

    /**
     * 第三方部门ID
     */
    private String thirdOrgId;
    /**
     * 第三方部门名称
     */
    private String thirdOrgName;
    /**
     * 第三方人员ID
     */
    private String thirdEmpId;
    /**
     * 第三方人员名称
     */
    private String thirdEmpName;
    /**
     * 分贝通手机号
     */
    private String fbtMobilePhone;
    /**
     * 分贝通账户
     */
    private String fbtAccount;
    /**
     * 分贝通权限
     */
    private String fbtRoleType;

    /**
     * 是否为教育行业
     */
    private String education;
    /**
     * 检查细则s
     */
    private String checkDetail;


}
