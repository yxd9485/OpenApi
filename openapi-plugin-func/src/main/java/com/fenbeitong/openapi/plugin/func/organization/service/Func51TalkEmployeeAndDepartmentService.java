package com.fenbeitong.openapi.plugin.func.organization.service;

import com.fenbeitong.openapi.plugin.support.employee.dto.OrgDto;

/**
 * <p>Title: FuncEmployeeAndDepartmentService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-24 15:04
 */
public interface Func51TalkEmployeeAndDepartmentService {

    /**
     * 组织架构同步
     */
    String EmployeeAndDepartmentSync(OrgDto orgDtoto, String companyId);


    /**
     * 部门负责人更新
     */
    String updateMasterIds(String companyId);
}
