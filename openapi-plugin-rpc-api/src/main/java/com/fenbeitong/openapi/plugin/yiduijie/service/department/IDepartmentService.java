package com.fenbeitong.openapi.plugin.yiduijie.service.department;

import com.fenbeitong.openapi.plugin.yiduijie.model.department.Department;

import java.util.List;

/**
 * <p>Title: IAccountService</p>
 * <p>Description: 财务部门服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 3:09 PM
 */
public interface IDepartmentService {

    /**
     * 同步财务部门
     *
     * @param companyId      公司id
     * @param departmentList 财务部门列表
     */
    void upsertDepartment(String companyId, List<Department> departmentList);

    /**
     * 公司财务部门列表
     *
     * @param companyId 公司id
     * @return 财务部门列表
     */
    List<Department> listDepartment(String companyId);
}
