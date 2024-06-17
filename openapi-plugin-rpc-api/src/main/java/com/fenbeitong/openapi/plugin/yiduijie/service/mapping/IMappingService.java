package com.fenbeitong.openapi.plugin.yiduijie.service.mapping;

import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.AccountMappingReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.DepartmentMappingReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.MappingDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.ProjectMappingReqDTO;

import java.util.List;

/**
 * <p>Title: IMappingService</p>
 * <p>Description: 映射服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 3:05 PM
 */
public interface IMappingService {

    /**
     * 科目映射
     *
     * @param companyId             公司id
     * @param accountMappingReqList 科目映射信息
     */
    void mappingAccount(String companyId, List<AccountMappingReqDTO> accountMappingReqList);

    /**
     * 增加科目映射
     *
     * @param companyId             公司id
     * @param accountMappingReqList 科目映射信息
     * @return 第三方id列表
     */
    List<String> addMappingAccount(String companyId, List<AccountMappingReqDTO> accountMappingReqList);

    /**
     * 更新科目映射
     *
     * @param companyId             公司id
     * @param accountMappingReqList 科目映射信息
     */
    void updateMappingAccount(String companyId, List<AccountMappingReqDTO> accountMappingReqList);

    /**
     * 删除科目映射
     *
     * @param companyId          公司id
     * @param thirdMappingIdList 三方科目映射id信息
     */
    void deleteMappingAccount(String companyId, List<String> thirdMappingIdList);

    /**
     * 部门映射
     *
     * @param companyId                公司id
     * @param departmentMappingReqList 部门映射信息
     */
    void mappingDepartment(String companyId, List<DepartmentMappingReqDTO> departmentMappingReqList);

    /**
     * 增加部门映射
     *
     * @param companyId                公司id
     * @param departmentMappingReqList 部门映射信息
     * @return 第三方id列表
     */
    List<String> addMappingDepartment(String companyId, List<DepartmentMappingReqDTO> departmentMappingReqList);

    /**
     * 更新部门映射
     *
     * @param companyId                公司id
     * @param departmentMappingReqList 部门映射信息
     */
    void updateMappingDepartment(String companyId, List<DepartmentMappingReqDTO> departmentMappingReqList);

    /**
     * 删除部门映射
     *
     * @param companyId          公司id
     * @param thirdMappingIdList 三方科目映射id信息
     */
    void deleteMappingDepartment(String companyId, List<String> thirdMappingIdList);


    /**
     * 增加项目映射
     *
     * @param companyId                公司id
     * @param projectMappingReqList 项目映射信息
     * @return 第三方id列表
     */
    List<String> addMappingProject(String companyId, List<ProjectMappingReqDTO> projectMappingReqList);

    /**
     * 更新项目映射
     *
     * @param companyId                公司id
     * @param projectMappingReqList 项目映射信息
     */
    void updateMappingProject(String companyId, List<ProjectMappingReqDTO> projectMappingReqList);

    /**
     * 删除项目映射
     *
     * @param companyId          公司id
     * @param thirdMappingIdList 三方科目映射id信息
     */
    void deleteMappingProject(String companyId, List<String> thirdMappingIdList);

    /**
     * 查询映射列表
     *
     * @param companyId   公司id
     * @param mappingType 见枚举 MappingType
     * @return 映射列表
     */
    List<MappingDTO> listMapping(String companyId, String mappingType);
}
