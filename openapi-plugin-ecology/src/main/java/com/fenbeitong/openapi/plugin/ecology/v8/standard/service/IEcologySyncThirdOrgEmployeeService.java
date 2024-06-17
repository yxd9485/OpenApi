package com.fenbeitong.openapi.plugin.ecology.v8.standard.service;

/**
 * 泛微组织机构人员同步
 *
 * @author lizhen
 */
public interface IEcologySyncThirdOrgEmployeeService {

    /**
     * 组织架构同步
     * 全量获取部门人员 无自定义字段
     *
     * @param companyId
     */
    void syncThirdOrgEmployee(String companyId);

    /**
     * 组织架构同步
     * 全量获取部门
     * 分页获取人员 包含自定义字段
     *
     * @param companyId
     */
    void syncThirdOrgEmployeePage(String companyId);

}
