package com.fenbeitong.openapi.plugin.seeyon.controller;

import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResponseUtils;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountParam;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonAccountService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonDepartmentService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonFbOrgEmpService;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.SeeyonAccessTokenService;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.SeeyonClientService;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.SeeyonMiddlewareService;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/seeyon/org")
public class SeeyonOrgController {
    @Autowired
    SeeyonDepartmentService seeyonDepartmentService;
    @Autowired
    SeeyonClientService seeyonClientService;
    @Autowired
    SeeyonAccessTokenService seeyonAccessTokenService;
    @Autowired
    SeeyonAccountService seeyonAccountService;
    @Autowired
    SeeyonFbOrgEmpService seeyonFbOrgEmpService;
    @Autowired
    SeeyonMiddlewareService seeyonMiddlewareService;

    /**
     * 根据公司名称获取公司组织架构全量数据
     *
     * @param orgName
     * @return
     */
    @RequestMapping("/getAll")
    @ResponseBody
    public Object getSeeyonAccountOrgInfo(String orgName) {
        String accessToken = seeyonAccessTokenService.getAccessToken(orgName);
        SeeyonClient seeyonClientByName = seeyonClientService.getSeeyonClientByName(orgName);
        Map<String, String> headerParamMap = Maps.newHashMap();
        headerParamMap.put(SeeyonConstant.TOKEN_HEADER, accessToken);
        String seeyonAccountId = seeyonClientByName.getSeeyonAccountId();
        SeeyonAccountParam build = SeeyonAccountParam.builder().orgAccountId(seeyonAccountId).build();
        List<SeeyonAccountOrgResp> orgInfo = seeyonDepartmentService.getOrgInfo("",build, seeyonClientByName.getSeeyonSysUri(), headerParamMap);
        return SeeyonResponseUtils.success(orgInfo);
    }


    @RequestMapping("/getInfo")
    @ResponseBody
    public Object getSeeyonOrgInfo(String orgName, @RequestParam(value = "orgIds", required = true) List<String> orgIds) {
        String accessToken = seeyonAccessTokenService.getAccessToken(orgName);
        SeeyonClient seeyonClientByName = seeyonClientService.getSeeyonClientByName(orgName);
        Map<String, String> headerParamMap = Maps.newHashMap();
        headerParamMap.put(SeeyonConstant.TOKEN_HEADER, accessToken);
        List<SeeyonAccountOrgResp> orgInfo = seeyonDepartmentService.getOrgDetail(orgIds, seeyonClientByName.getSeeyonSysUri(), headerParamMap);
        return SeeyonResponseUtils.success(orgInfo);
    }

    /**
     * 手动处理需要操作的部门数据
     * @param orgName
     * @param orgIds
     * @param operateType
     * @return
     */
    @RequestMapping("/operate/hand")
    @ResponseBody
    public Object operateSeeyonFbOrg(String orgName, @RequestParam(value = "orgIds", required = true) List<String> orgIds, int operateType) {
        String accessToken = seeyonAccessTokenService.getAccessToken(orgName);
        SeeyonClient seeyonClientByName = seeyonClientService.getSeeyonClientByName(orgName);
        Map<String, String> headerParamMap = Maps.newHashMap();
        headerParamMap.put(SeeyonConstant.TOKEN_HEADER, accessToken);
        String seeyonAccountId = seeyonClientByName.getSeeyonAccountId();
        SeeyonAccountParam build = SeeyonAccountParam.builder().orgAccountId(seeyonAccountId).build();
        List<SeeyonAccountOrgResp> orgInfo = null;
        if (ObjectUtils.isEmpty(orgIds)) {//为空则拉取全量数据，数据同步到fb_org_emp表，执行任务进行处理
            //查询所有部门数据
            orgInfo = seeyonDepartmentService.getOrgInfo("",build, seeyonClientByName.getSeeyonSysUri(), headerParamMap);
        } else {//指定部门删除
            orgInfo = seeyonDepartmentService.getOrgDetail(orgIds, seeyonClientByName.getSeeyonSysUri(), headerParamMap);
        }
        for (SeeyonAccountOrgResp seeyonAccountOrgResp : orgInfo) {
            if (operateType == 3) {//删除部门
                seeyonFbOrgEmpService.delOrg(seeyonClientByName, seeyonAccountOrgResp);
            } else if (operateType == 4) {//加部门
                seeyonFbOrgEmpService.createOrg(seeyonClientByName, seeyonAccountOrgResp);
            } else if (operateType == 5) {//改部门
                seeyonFbOrgEmpService.updateOrg(seeyonClientByName, seeyonAccountOrgResp);
            }
        }
        return SeeyonResponseUtils.success(new HashMap<>());


    }


    /**
     * 根据公司和日期删除几天前的部门数据,
     * 流水表需要定期清理，否则后期表数据过大
     */
    @RequestMapping("/deleteOperatin")
    @ResponseBody
    public Object deleteOperatin(String orgAccountId, String beginTime, String endTime) {
        boolean flag = false;
        if (StringUtils.isNotBlank(orgAccountId) && StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            flag = seeyonMiddlewareService.deleteSeeyonDepartment(orgAccountId, beginTime, endTime);
            return SeeyonResponseUtils.success(flag);
        }
        return SeeyonResponseUtils.success(flag);
    }


    @RequestMapping("/getErrorOrgInfo")
    @ResponseBody
    public Object getErrorOrgInfo(@RequestParam String orgName) {
        String accessToken = seeyonAccessTokenService.getAccessToken(orgName);
        SeeyonClient seeyonClientByName = seeyonClientService.getSeeyonClientByName(orgName);
        Map<String, String> headerParamMap = Maps.newHashMap();
        headerParamMap.put(SeeyonConstant.TOKEN_HEADER, accessToken);
        String seeyonAccountId = seeyonClientByName.getSeeyonAccountId();
        SeeyonAccountParam build = SeeyonAccountParam.builder().orgAccountId(seeyonAccountId).build();
        List<SeeyonAccountOrgResp> orgInfos = seeyonDepartmentService.getOrgInfo("",build, seeyonClientByName.getSeeyonSysUri(), headerParamMap);
        List<SeeyonAccountOrgResp> errorOrgInfos = orgInfos.stream().filter(orgInfo -> orgInfo.getName().contains("/")
        ).collect(Collectors.toList());

        return SeeyonResponseUtils.success(errorOrgInfos);
    }



}
