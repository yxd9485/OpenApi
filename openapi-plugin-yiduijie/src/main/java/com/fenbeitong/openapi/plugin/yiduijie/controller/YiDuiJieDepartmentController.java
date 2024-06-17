package com.fenbeitong.openapi.plugin.yiduijie.controller;

import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResponseUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResultEntity;
import com.fenbeitong.openapi.plugin.yiduijie.constant.MappingType;
import com.fenbeitong.openapi.plugin.yiduijie.model.department.Department;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.DepartmentMappingReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.MappingDTO;
import com.fenbeitong.openapi.plugin.yiduijie.service.department.IDepartmentService;
import com.fenbeitong.openapi.plugin.yiduijie.service.mapping.IMappingService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: YiDuiJieDepartmentController</p>
 * <p>Description: 易对接财务部门</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 11:48 AM
 */
@RestController
@RequestMapping("/yiduijie/department")
@Api(tags = "易对接财务部门", description = "易对接财务部门")
public class YiDuiJieDepartmentController {

    @Autowired
    @Qualifier("springDepartmentService")
    private IDepartmentService departmentService;

    @Autowired
    @Qualifier("springMappingService")
    private IMappingService mappingService;

    /**
     * 同步财务部门
     *
     * @param companyId      公司id
     * @param departmentList 财务部门列表
     * @return 操作结果
     */
    @RequestMapping("/upsertDepartment/{company_id}")
    @ApiOperation(value = "1、同步财务部门", notes = "同步财务部门", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 1)
    public Object upsertDepartment(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @RequestBody List<Department> departmentList) {
        departmentService.upsertDepartment(companyId, departmentList);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 财务部门列表
     *
     * @param companyId 公司id
     * @return 财务部门列表
     */
    @RequestMapping("/listDepartment/{company_id}")
    @ApiOperation(value = "2、财务部门列表", notes = "财务部门列表", httpMethod = "GET", response = YiDuiJieResultEntity.class, position = 2)
    @ApiParam(name = "company_id", value = "公司ID", required = true)
    public Object listDepartment(@PathVariable("company_id") String companyId) {
        List<Department> departmentList = departmentService.listDepartment(companyId);
        return YiDuiJieResponseUtils.success(departmentList);
    }

    /**
     * 增加部门映射
     *
     * @param companyId                公司id
     * @param departmentMappingReqList 部门映射信息
     * @return 操作结果
     */
    @RequestMapping("/addMappingDepartment/{company_id}")
    @ApiOperation(value = "3、增加部门映射", notes = "增加部门映射", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 3)
    public Object addMappingDepartment(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @RequestBody List<DepartmentMappingReqDTO> departmentMappingReqList) {
        mappingService.addMappingDepartment(companyId, departmentMappingReqList);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 更新部门映射
     *
     * @param companyId                公司id
     * @param departmentMappingReqList 部门映射信息
     * @return 操作结果
     */
    @RequestMapping("/updateMappingDepartment/{company_id}")
    @ApiOperation(value = "4、更新部门映射", notes = "更新部门映射", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 4)
    public Object updateMappingDepartment(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @RequestBody List<DepartmentMappingReqDTO> departmentMappingReqList) {
        mappingService.updateMappingDepartment(companyId, departmentMappingReqList);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 删除部门映射
     *
     * @param companyId          公司id
     * @param thirdMappingIdList 部门映射id信息
     * @return 操作结果
     */
    @RequestMapping("/deleteMappingDepartment/{company_id}")
    @ApiOperation(value = "5、删除部门映射", notes = "删除部门映射", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 5)
    public Object deleteMappingDepartment(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @RequestBody List<String> thirdMappingIdList) {
        mappingService.deleteMappingDepartment(companyId, thirdMappingIdList);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 部门映射列表
     *
     * @param companyId 公司id
     * @return 部门映射列表
     */
    @RequestMapping("/listMappingDepartment/{company_id}")
    @ApiOperation(value = "6、部门映射列表", notes = "部门映射列表", httpMethod = "GET", response = YiDuiJieResultEntity.class, position = 6)
    public Object listMappingDepartment(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId) {
        List<MappingDTO> mappingList = mappingService.listMapping(companyId, MappingType.department.getValue());
        return YiDuiJieResponseUtils.success(mappingList);
    }

    /**
     * 清空部门映射列表
     *
     * @param companyId 公司id
     * @return 操作结果
     */
    @RequestMapping("/clearMappingDepartment/{company_id}")
    @ApiOperation(value = "7、清空部门映射列表", notes = "清空部门映射列表", httpMethod = "GET", response = YiDuiJieResultEntity.class, position = 7)
    public Object clearMappingDepartment(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId) {
        List<MappingDTO> mappingList = mappingService.listMapping(companyId, MappingType.department.getValue());
        mappingService.deleteMappingDepartment(companyId, mappingList.stream().map(MappingDTO::getId).collect(Collectors.toList()));
        return YiDuiJieResponseUtils.success(mappingList);
    }

}
