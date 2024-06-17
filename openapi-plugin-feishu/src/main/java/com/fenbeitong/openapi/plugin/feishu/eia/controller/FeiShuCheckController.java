package com.fenbeitong.openapi.plugin.feishu.eia.controller;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseUtils;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuDepartmentSimpleListRespDTO;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaEmployeeService;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaOrganizationService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/feishu/syncThird")
public class FeiShuCheckController {
    @Autowired
    FeiShuEiaOrganizationService feiShuEiaOrganizationService;
    @Autowired
    FeiShuEiaEmployeeService feiShuEiaEmployeeService;


    @RequestMapping("/checkFeiShuDepartment/{companyId}/{corpId}/{companyName}")
    @ResponseBody
    public Object checkFeiShuDepartment(@PathVariable("companyId") String companyId,@PathVariable("companyName") String companyName,@PathVariable("corpId") String corpId){
        List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> unSynDeptList = feiShuEiaOrganizationService.checkFeiShuDepartment(companyId, corpId,companyName, OpenType.FEISHU_EIA.getType());
        return FeiShuResponseUtils.success(unSynDeptList);
    }


    @RequestMapping("/checkFeiShuEmployee/{companyId}/{corpId}/{companyName}")
    @ResponseBody
    public Object checkFeiShuEmployee(@PathVariable("companyId") String companyId,@PathVariable("companyName") String companyName,@PathVariable("corpId") String corpId){
        List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> allDepartments = feiShuEiaOrganizationService.getAllDepartments(corpId, companyName);
        Map<String, Object> stringObjectMap = feiShuEiaEmployeeService.checkFeiShuEmployee(companyId, corpId, allDepartments,OpenType.FEISHU_EIA.getType());
        return FeiShuResponseUtils.success(stringObjectMap);
    }


}
