package com.fenbeitong.openapi.plugin.definition.dto.auto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CorpAutoOrgEmpDTO {
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
    private String companyName;
    /**
     * 分贝通部门ID
     */
    private String fbtOrgId;
    /**
     * 分贝通部门名称
     */
    private String fbtOrgName;
    /**
     * 分贝通人员ID
     */
    private String fbtEmpId;
    /**
     * 分贝通人员名称
     */
    private String fbtEmpName;
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
     * 手机号
     */
    private String mobilePhone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 类型，绑定部门时使用类型 类型：1：按照ID绑定 2：按照全路径绑定
     */
    private int type;

}
