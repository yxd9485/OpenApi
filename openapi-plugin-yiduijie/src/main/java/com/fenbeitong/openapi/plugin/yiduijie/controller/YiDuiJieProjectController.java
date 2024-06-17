package com.fenbeitong.openapi.plugin.yiduijie.controller;

import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResponseUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResultEntity;
import com.fenbeitong.openapi.plugin.yiduijie.constant.MappingType;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.MappingDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.ProjectMappingReqDTO;
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
 * <p>Title: YiDuiJieProjectController</p>
 * <p>Description: 易对接项目</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/15 11:48 AM
 */
@RestController
@RequestMapping("/yiduijie/project")
@Api(tags = "易对接项目", description = "易对接项目")
public class YiDuiJieProjectController {

    @Autowired
    @Qualifier("springMappingService")
    private IMappingService mappingService;

    /**
     * 增加项目映射
     *
     * @param companyId             公司id
     * @param projectMappingReqList 项目映射信息
     * @return 操作结果
     */
    @RequestMapping("/addMappingProject/{company_id}")
    @ApiOperation(value = "1、增加项目映射", notes = "增加项目映射", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 1)
    public Object addMappingAccount(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @RequestBody List<ProjectMappingReqDTO> projectMappingReqList) {
        mappingService.addMappingProject(companyId, projectMappingReqList);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 更新项目映射
     *
     * @param companyId             公司id
     * @param projectMappingReqList 项目映射信息
     * @return 操作结果
     */
    @RequestMapping("/updateMappingProject/{company_id}")
    @ApiOperation(value = "2、更新项目映射", notes = "更新项目映射", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 2)
    public Object updateMappingProject(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @RequestBody List<ProjectMappingReqDTO> projectMappingReqList) {
        mappingService.updateMappingProject(companyId, projectMappingReqList);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 删除项目映射
     *
     * @param companyId          公司id
     * @param thirdMappingIdList 科目映射id信息
     * @return 操作结果
     */
    @RequestMapping("/deleteMappingProject/{company_id}")
    @ApiOperation(value = "3、删除项目映射", notes = "删除项目映射", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 3)
    public Object deleteMappingProject(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @RequestBody List<String> thirdMappingIdList) {
        mappingService.deleteMappingProject(companyId, thirdMappingIdList);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 项目映射列表
     *
     * @param companyId 公司id
     * @return 项目映射列表
     */
    @RequestMapping("/listProjectMapping/{company_id}")
    @ApiOperation(value = "4、项目映射列表", notes = "项目映射列表", httpMethod = "GET", response = YiDuiJieResultEntity.class, position = 4)
    public Object listProjectMapping(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId) {
        List<MappingDTO> mappingList = mappingService.listMapping(companyId, MappingType.project.getValue());
        return YiDuiJieResponseUtils.success(mappingList);
    }

    /**
     * 清空项目映射列表
     *
     * @param companyId 公司id
     * @return 操作结果
     */
    @RequestMapping("/clearProjectMapping/{company_id}")
    @ApiOperation(value = "5、清空项目映射列表", notes = "清空项目映射列表", httpMethod = "GET", response = YiDuiJieResultEntity.class, position = 5)
    public Object clearProjectMapping(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId) {
        List<MappingDTO> mappingList = mappingService.listMapping(companyId, MappingType.project.getValue());
        mappingService.deleteMappingProject(companyId, mappingList.stream().map(MappingDTO::getId).collect(Collectors.toList()));
        return YiDuiJieResponseUtils.success(mappingList);
    }

}
