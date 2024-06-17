package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountParam;
import com.fenbeitong.openapi.plugin.seeyon.enums.ApiStatusCodeEnum;
import com.fenbeitong.openapi.plugin.seeyon.exceptions.SeeyonApiException;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonDepartmentService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ServiceAspect
@Service
@Slf4j
public class SeeyonDepartmentServiceImpl extends AbstractOrganizationService implements SeeyonDepartmentService {
    @Autowired
    RestHttpUtils restHttpUtils;
    @Value("${seeyon.rest-apis.get-organization}")
    private String seeyonOrgUrl;

    @Value("${seeyon.rest-apis.get-organization-info}")
    private String seeyonOrgDetailUrl;
    @Autowired
    OpenMsgSetupDao openMsgSetupDao;

    /**
     * 根据公司accountId查询组织架构信息
     *
     * @param accountParam
     * @param accountOrgUrl
     * @param tokenHeader
     * @return
     */
    public List<SeeyonAccountOrgResp> getOrgInfo(String companyId,
                                                 SeeyonAccountParam accountParam, String accountOrgUrl, Map<String, String> tokenHeader) {
        boolean end = false;
        Integer counter = 0;
        List<SeeyonAccountOrgResp> seeyonAccountResps = new ArrayList<>();
        while (!end) {
            try {
                // 调用对应的请求地址,返回数据
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(SeeyonConstant.TOKEN_HEADER, tokenHeader.get(SeeyonConstant.TOKEN_HEADER));
                String orgUrl = accountOrgUrl + seeyonOrgUrl + accountParam.getOrgAccountId();
                String result = "";
                Map itemCodeMap = Maps.newHashMap();
                itemCodeMap.put("companyId", companyId);
                itemCodeMap.put("itemCode", "company_seeyon_simulation_test");
                List<OpenMsgSetup> list = openMsgSetupDao.openMsgSetupList(itemCodeMap);
                if (!ObjectUtils.isEmpty(list)) {//可以查询出数据
                    OpenMsgSetup openMsgSetup = list.get(0);
                    //获取是否为配置企业
                    Integer intVal1 = openMsgSetup.getIntVal1();
                    if (1 == intVal1) {//配置了模拟测试，总开关
                        Integer intVal2 = openMsgSetup.getIntVal2();
                        if (1 == intVal2) {
                            result = openMsgSetup.getStrVal1();
                        } else {
                            result = restHttpUtils.get(orgUrl, httpHeaders, Maps.newHashMap());
                        }
                    } else {
                        result = restHttpUtils.get(orgUrl, httpHeaders, Maps.newHashMap());
                    }
                } else {
                    result = restHttpUtils.get(orgUrl, httpHeaders, Maps.newHashMap());
                }
                if (Objects.isNull(result)) {
                    log.info("获取组织架构信息为空 , result : {}",result);
                    return seeyonAccountResps;
                }
                seeyonAccountResps = JsonUtils.toObj(result, new TypeReference<List<SeeyonAccountOrgResp>>() {
                });
                if (Objects.isNull(seeyonAccountResps)) {
                    log.info("获取组织架构信息为空 , seeyonAccountResps : {}", seeyonAccountResps);
                }
                end = true;
            } catch (SeeyonApiException ex) {
                if (counter > SeeyonConstant.RETRY_COUNTER) {
                    ex.printStackTrace();
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
                ++counter;
                try {
                    Thread.sleep(SeeyonConstant.RETRY_SLEEP);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
            }
        }
        log.info("公司拉取部门总数量: {},{}", companyId, seeyonAccountResps.size());
        return seeyonAccountResps;
    }

    @Override
    public List<SeeyonAccountOrgResp> getOrgDetail(List<String> orgIds, String accountOrgUrl, Map<String, String> tokenHeader) {
        List<SeeyonAccountOrgResp> seeyonAccountResps = new ArrayList<>();
        try {
            // 调用对应的请求地址,返回数据
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(SeeyonConstant.TOKEN_HEADER, tokenHeader.get(SeeyonConstant.TOKEN_HEADER));

            for (String orgId : orgIds) {
                String orgUrl = accountOrgUrl + seeyonOrgDetailUrl + orgId;
                String result = restHttpUtils.get(orgUrl, httpHeaders, Maps.newHashMap());
                if (Objects.isNull(result)) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
                SeeyonAccountOrgResp seeyonAccountOrgResp = JsonUtils.toObj(result, new TypeReference<SeeyonAccountOrgResp>() {
                });
                if (Objects.isNull(seeyonAccountOrgResp)) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
                seeyonAccountResps.add(seeyonAccountOrgResp);
            }
        } catch (SeeyonApiException ex) {
            log.info("获取部门详情数据异常 {}", orgIds);
        }
        return seeyonAccountResps;
    }


}
