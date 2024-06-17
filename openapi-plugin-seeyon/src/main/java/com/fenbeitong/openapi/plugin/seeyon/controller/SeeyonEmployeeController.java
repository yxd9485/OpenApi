package com.fenbeitong.openapi.plugin.seeyon.controller;

import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResponseUtils;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountEmpResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountParam;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonAccountService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonDepartmentService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonEmpService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonFbOrgEmpService;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.SeeyonAccessTokenService;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.SeeyonClientService;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.SeeyonFbOrgEmpServiceImpl;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.SeeyonMiddlewareService;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.PhoneCheckUtil;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/seeyon/emp")
public class SeeyonEmployeeController {

    @Autowired
    SeeyonClientService seeyonClientService;
    @Autowired
    SeeyonAccessTokenService seeyonAccessTokenService;
    @Autowired
    SeeyonAccountService seeyonAccountService;
    @Autowired
    SeeyonEmpService seeyonEmpService;
    @Autowired
    SeeyonFbOrgEmpService seeyonFbOrgEmpService;
    @Autowired
    SeeyonMiddlewareService seeyonMiddlewareService;
    @Autowired
    SeeyonFbOrgEmpServiceImpl seeyonFbOrgEmpServiceImpl;
    /**
     * 根据公司名称获取致远OA人员数据
     * @param orgName
     * @return
     */
    @RequestMapping("/getAll")
    @ResponseBody
    public Object getSeeyonAllEmp(String orgName) {
        String accessToken = seeyonAccessTokenService.getAccessToken(orgName);
        SeeyonClient seeyonClientByName = seeyonClientService.getSeeyonClientByName(orgName);
        Map<String, String> headerParamMap = Maps.newHashMap();
        headerParamMap.put(SeeyonConstant.TOKEN_HEADER, accessToken);
        String seeyonAccountId = seeyonClientByName.getSeeyonAccountId();
        SeeyonAccountParam build = SeeyonAccountParam.builder().orgAccountId(seeyonAccountId).build();
        List<SeeyonAccountEmpResp> empInfo = seeyonEmpService.getEmpInfo("",build, seeyonClientByName.getSeeyonSysUri(), headerParamMap);
        return SeeyonResponseUtils.success(empInfo);
    }

    @RequestMapping("/getInfo")
    @ResponseBody
    public Object getSeeyonEmpDetail(String orgName,@RequestParam(value = "empIds",required = true)List<String> empIds) {
        String accessToken = seeyonAccessTokenService.getAccessToken(orgName);
        SeeyonClient seeyonClientByName = seeyonClientService.getSeeyonClientByName(orgName);
        Map<String, String> headerParamMap = Maps.newHashMap();
        headerParamMap.put(SeeyonConstant.TOKEN_HEADER, accessToken);
        List<SeeyonAccountEmpResp> empInfo = seeyonEmpService.getEmpDetail(empIds, seeyonClientByName.getSeeyonSysUri(), headerParamMap);
        return SeeyonResponseUtils.success(empInfo);
    }



    /**
     * 手动处理需要操作的人员数据
     * @param orgName
     * @param empIds
     * @param operateType
     * @return
     */
    @RequestMapping("/operate/hand")
    @ResponseBody
    public Object operateSeeyonFbEmp(String orgName, @RequestParam(value = "empIds", required = true) List<String> empIds, int operateType) {
        String accessToken = seeyonAccessTokenService.getAccessToken(orgName);
        SeeyonClient seeyonClientByName = seeyonClientService.getSeeyonClientByName(orgName);
        Map<String, String> headerParamMap = Maps.newHashMap();
        headerParamMap.put(SeeyonConstant.TOKEN_HEADER, accessToken);
        String seeyonAccountId = seeyonClientByName.getSeeyonAccountId();
        SeeyonAccountParam build = SeeyonAccountParam.builder().orgAccountId(seeyonAccountId).build();
        List<SeeyonAccountEmpResp> empInfo = null;
        if (ObjectUtils.isEmpty(empIds)) {//为空则拉取全量数据，数据同步到fb_org_emp表，执行任务进行处理
            //查询所有部门数据
            empInfo = seeyonEmpService.getEmpInfo("",build, seeyonClientByName.getSeeyonSysUri(), headerParamMap);
        } else {//指定部门删除
            empInfo = seeyonEmpService.getEmpDetail(empIds, seeyonClientByName.getSeeyonSysUri(), headerParamMap);
        }
        for (SeeyonAccountEmpResp seeyonAccountEmpResp : empInfo) {
            if (operateType == 0) {//删除人员
                ThirdEmployeeRes employeeByThirdId = seeyonFbOrgEmpServiceImpl.getEmployeeByThirdId(seeyonClientByName.getOpenapiAppId(), String.valueOf(seeyonAccountEmpResp.getId()));
                if(!ObjectUtils.isEmpty(employeeByThirdId)){
                    seeyonFbOrgEmpService.delEmp(seeyonClientByName, seeyonAccountEmpResp);
                }
            } else if (operateType == 1) {//加人员
                seeyonFbOrgEmpService.createEmp(seeyonClientByName, seeyonAccountEmpResp,null);
            } else if (operateType == 2) {//改人员
                ThirdEmployeeRes employeeByThirdId = seeyonFbOrgEmpServiceImpl.getEmployeeByThirdId(seeyonClientByName.getOpenapiAppId(), String.valueOf(seeyonAccountEmpResp.getId()));
                if(!ObjectUtils.isEmpty(employeeByThirdId)){
                    seeyonFbOrgEmpService.updateEmp(seeyonClientByName, seeyonAccountEmpResp,null);
                }
            }
        }
        return SeeyonResponseUtils.success(new HashMap<>());


    }


    /**
     * 根据公司和日期删除几天前的人员数据,
     * 流水表需要定期清理，否则后期表数据过大
     */
    @RequestMapping("/deleteOperatin")
    @ResponseBody
    public Object deleteOperatin(String orgAccountId, String beginTime, String endTime) {
        boolean flag = false;
        if (StringUtils.isNotBlank(orgAccountId) && StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            flag = seeyonMiddlewareService.deleteSeeyonEmp(orgAccountId, beginTime, endTime);
            return SeeyonResponseUtils.success(flag);
        }
        return SeeyonResponseUtils.success(flag);
    }



    @RequestMapping("/getErrorMobileEmpInfo")
    @ResponseBody
    public Object getErrorMobileEmpInfos(String orgName) {
        String accessToken = seeyonAccessTokenService.getAccessToken(orgName);
        SeeyonClient seeyonClientByName = seeyonClientService.getSeeyonClientByName(orgName);
        Map<String, String> headerParamMap = Maps.newHashMap();
        headerParamMap.put(SeeyonConstant.TOKEN_HEADER, accessToken);
        String seeyonAccountId = seeyonClientByName.getSeeyonAccountId();
        SeeyonAccountParam build = SeeyonAccountParam.builder().orgAccountId(seeyonAccountId).build();
        List<SeeyonAccountEmpResp> empInfos = seeyonEmpService.getEmpInfo("",build, seeyonClientByName.getSeeyonSysUri(), headerParamMap);
        List<SeeyonAccountEmpResp> errorEmpInfos = empInfos.stream().filter(empInfo -> !PhoneCheckUtil.validMomile(empInfo.getTelNumber())).collect(Collectors.toList());
        return SeeyonResponseUtils.success(errorEmpInfos);
    }


}

