package com.fenbeitong.openapi.plugin.seeyon.controller;

import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResponseUtils;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeYonRedisKeyConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.*;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOpenMsgSetup;
import com.fenbeitong.openapi.plugin.seeyon.enums.ApiStatusCodeEnum;
import com.fenbeitong.openapi.plugin.seeyon.enums.HttpStatusCodeEnum;
import com.fenbeitong.openapi.plugin.seeyon.exceptions.SeeyonApiException;
import com.fenbeitong.openapi.plugin.seeyon.service.*;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.*;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Maps;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/seeyon/thirdOrg")
public class SeeyonSyncController {

    @Autowired
    SeeyonClientService seeyonClientService;
    @Autowired
    SeeyonMsgSetupService seeyonMsgSetupService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SeeyonOrgSynService seeyonOrgSynService;

    @RequestMapping("/sync")
    @ResponseBody
    public Object syncSeeyonThirdOrg(@RequestParam("jobConfig") String jobConfig) throws UnsupportedEncodingException {
        log.info("================= seeyon sync third org info start , jobConfig : {} ================= ",jobConfig);
        SeeyonOrgNameReq seeyonOrgNameReq = JsonUtils.toObj(jobConfig, SeeyonOrgNameReq.class);
        String orgName;
        if (!ObjectUtils.isEmpty(seeyonOrgNameReq)) {
            orgName = seeyonOrgNameReq.getOrgName();
        } else {
            throw new SeeyonApiException(HttpStatusCodeEnum.BAD_REQUEST);
        }
        SeeyonClient seeyonClient = seeyonClientService.getSeeyonClientByName(orgName);
        Map<String, Object> companyRedisMap = Maps.newHashMap();
        companyRedisMap.put("companyId", seeyonClient.getOpenapiAppId());
        companyRedisMap.put("itemCode", "company_seeyon_use_tag");
        //一般一天仅同步一次的公司需要配置，其他类型的公司无需配置
        SeeyonOpenMsgSetup seeyonCompanySetup = seeyonMsgSetupService.getSeeyonCompanySetup(companyRedisMap);
        //设置redis key
        String seeyonAccountOrgEmpKey = MessageFormat.format(SeeYonRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(SeeYonRedisKeyConstant.SEEYON_EMPLOYEE_SYNC_TAG, seeyonClient.getSeeyonAccountId()));
        log.info("seeyonAccountOrgEmpKey is : {} ",seeyonAccountOrgEmpKey);
        if (hasRedisInfo(seeyonCompanySetup,seeyonAccountOrgEmpKey)){
            return SeeyonResponseUtils.success(new HashMap<>());
        }
        if ( null == seeyonClient) {
            log.info("未配置致远相关信息 , seeyonClient is null");
            throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ORG_NAME_NOT_FOUND.transform());
        }
        seeyonOrgSynService.doOrgSyn(orgName,seeyonClient,seeyonOrgNameReq,seeyonCompanySetup,seeyonAccountOrgEmpKey);
        log.info("================= seeyon 组织机构同步 success ================= ");
        return SeeyonResponseUtils.success(new HashMap<>());
    }

    public boolean hasRedisInfo(SeeyonOpenMsgSetup seeyonCompanySetup , String seeyonAccountOrgEmpKey){
        if (ObjectUtils.isEmpty(seeyonCompanySetup)){
            return false;
        }
        //不为空，说明使用redis存储拉取部门和人员标识,同步频率一小时几次
        //总开关,0默认关闭，1开启
        Integer intVal1 = seeyonCompanySetup.getIntVal1();
        if (1 == intVal1) {
            Integer intVal2 = seeyonCompanySetup.getIntVal2();
            if (1 == intVal2) {//Redis使用标识
                //根据存储的公司key，查询是否存储标识
                String orgEmpTag = ObjUtils.toString(redisTemplate.opsForValue().get(seeyonAccountOrgEmpKey));
                if (!StringUtils.isBlank(orgEmpTag)) {
                    if (orgEmpTag.equals("successed")) {//成功后直接返回，不进行执行后续操作
                        log.info("缓存已有致远信息 , 直接返回 true");
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
