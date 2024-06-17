package com.fenbeitong.openapi.plugin.ecology.v8.standard.service;

/**
 * 泛微组织机构人员同步
 *
 * @author lizhen
 */
public interface IEcologyRestSyncThirdOrgEmployeeService {

    /**
     * restful 风格
     * 组织架构同步
     * 全量获取部门
     * @param companyId 公司id
     */
    void restSyncThirdOrgEmployee(String companyId);

    /**
     * 同步部门主管
     * @param companyId 公司id
     */
    void syncDepartmentManagers(String companyId);

}
