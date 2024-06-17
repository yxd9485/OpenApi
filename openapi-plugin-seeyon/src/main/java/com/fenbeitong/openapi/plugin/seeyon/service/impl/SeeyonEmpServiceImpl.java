package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountEmpResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountParam;
import com.fenbeitong.openapi.plugin.seeyon.enums.ApiStatusCodeEnum;
import com.fenbeitong.openapi.plugin.seeyon.exceptions.SeeyonApiException;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonDepartmentService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonEmpService;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.template.RestTemplateUtils;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ServiceAspect
@Service
@Slf4j
public class SeeyonEmpServiceImpl extends AbstractEmployeeService implements SeeyonEmpService {

    @Autowired
    RestHttpUtils restHttpUtils;
    @Value("${seeyon.rest-apis.get-employee}")
    private String seeyonEmpUrl;
    @Value("${seeyon.rest-apis.get-employee-info}")
    private String employeeDetail;
    @Value("${seeyon.rest-apis.get-org-employee}")
    private String seeyonOrgEmpInfoUrl;
    @DubboReference(check = false)
    private IThirdEmployeeService iThirdEmployeeService;
    @Autowired
    private SeeyonDepartmentService seeyonDepartmentService;
    @Autowired
    OpenMsgSetupDao openMsgSetupDao;
    @Autowired
    RestTemplateUtils restTemplateUtils;

    @Override
    public List<SeeyonAccountEmpResp> getEmpInfo(String companyId, SeeyonAccountParam accountParam, String accountOrgUrl, Map<String, String> tokenHeader) {
        boolean end = false;
        Integer counter = 0;
        List<SeeyonAccountEmpResp> seeyonAccountEmpResps = new ArrayList<>();
        while (!end) {
            try {
                List<SeeyonAccountOrgResp> orgInfo = seeyonDepartmentService.getOrgInfo(companyId, accountParam, accountOrgUrl, tokenHeader);
                //循环获取部门数据，根据部门ID查询致远OA部门员工数据
                String url = accountOrgUrl + seeyonOrgEmpInfoUrl;
                //2.根据部门ID循环查询人员数据
                for (SeeyonAccountOrgResp orgResp : orgInfo) {
                    List<SeeyonAccountEmpResp> orgEmpInfo = getOrgEmpInfo(companyId, String.valueOf(orgResp.getId()), url, tokenHeader);
                    log.info("{} 该部门下员工个数为 {}",orgResp.getId(), null == orgEmpInfo ? 0 : orgEmpInfo.size());
                    seeyonAccountEmpResps.addAll(orgEmpInfo);
                }
                seeyonAccountEmpResps = seeyonAccountEmpResps.stream().distinct().collect(Collectors.toList());
                if (ObjectUtils.isEmpty(seeyonAccountEmpResps)) {
                    log.info("部门下人员信息数据为空 , companyId : {}",companyId);
                }
                end = true;
            } catch (SeeyonApiException ex) {
                if (counter > SeeyonConstant.RETRY_COUNTER) {
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
        log.info("公司拉取人员总数量: {},{}", companyId, seeyonAccountEmpResps.size());
        return seeyonAccountEmpResps;
    }

    @Override
    public List<SeeyonAccountEmpResp> getEmpDetail(List<String> empIds, String accountEmpUrl, Map<String, String> tokenHeader) {
        List<SeeyonAccountEmpResp> seeyonAccountEmpResps = new ArrayList<>();
        try {
            // 调用对应的请求地址,返回数据
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(SeeyonConstant.TOKEN_HEADER, tokenHeader.get(SeeyonConstant.TOKEN_HEADER));
            for (String empId : empIds) {
                String orgUrl = accountEmpUrl + employeeDetail + empId;
                String result = restHttpUtils.get(orgUrl, httpHeaders, Maps.newHashMap());
                if (Objects.isNull(result)) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
                SeeyonAccountEmpResp seeyonAccountEmpResp = JsonUtils.toObj(result, new TypeReference<SeeyonAccountEmpResp>() {
                });
                if (Objects.isNull(seeyonAccountEmpResp)) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
                seeyonAccountEmpResps.add(seeyonAccountEmpResp);
            }
        } catch (SeeyonApiException ex) {
            log.info("获取人员详情数据异常 {}", empIds);
        }
        return seeyonAccountEmpResps;
    }

    @Override
    public List<SeeyonAccountEmpResp> getOrgEmpInfo(String companyId, String orgId, String accountEmpUrl, Map<String, String> tokenHeader) {
        //获取部门下直属员工集合
        String empUrl = accountEmpUrl + orgId;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SeeyonConstant.TOKEN_HEADER, tokenHeader.get(SeeyonConstant.TOKEN_HEADER));
        String result = "";
        Map itemCodeMap = Maps.newHashMap();
        itemCodeMap.put("companyId", companyId);
        itemCodeMap.put("itemCode", "company_seeyon_simulation_test");
        List<OpenMsgSetup> list = openMsgSetupDao.openMsgSetupList(itemCodeMap);
        if (!ObjectUtils.isEmpty(list)) {//可以查询出数据
            OpenMsgSetup openMsgSetup = list.get(0);
            //获取是否为配置企业
            Integer intVal1 = openMsgSetup.getIntVal1();
            if (1 == intVal1) {//配置了模拟测试
                //人员模拟数据开关
                Integer intVal3 = openMsgSetup.getIntVal3();
                if (1 == intVal3) {//模拟人员数据开
                    result = openMsgSetup.getStrVal2();
                } else {
                    result = restHttpUtils.get(empUrl, httpHeaders, Maps.newHashMap());
                }
            } else {
                result = restHttpUtils.get(empUrl, httpHeaders, Maps.newHashMap());
            }
        } else {
            result = restHttpUtils.get(empUrl, httpHeaders, Maps.newHashMap());
        }
        if (Objects.isNull(result)) {
            throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
        }
        List<SeeyonAccountEmpResp> seeyonAccountEmpResp = JsonUtils.toObj(result, new TypeReference<List<SeeyonAccountEmpResp>>() {
        });
        if (Objects.isNull(seeyonAccountEmpResp)) {
            throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
        }
        return seeyonAccountEmpResp;
    }

    @Override
    public List<SeeyonAccountEmpResp> getAllEmployee(String accountId, String accountEmpUrl, Map<String, String> tokenHeader) {
        List<SeeyonAccountEmpResp> seeyonAccountEmpResps = null;
        try {
            // 调用对应的请求地址,返回数据
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(SeeyonConstant.TOKEN_HEADER, tokenHeader.get(SeeyonConstant.TOKEN_HEADER));
            String orgUrl = accountEmpUrl + seeyonEmpUrl + accountId;
//            String result = restHttpUtils.get(orgUrl, httpHeaders, Maps.newHashMap());
            String result = restTemplateUtils.doGet(
                    orgUrl,
                    tokenHeader,
                    Maps.newHashMap());
            if (Objects.isNull(result)) {
                throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
            }
            seeyonAccountEmpResps = JsonUtils.toObj(result, new TypeReference<List<SeeyonAccountEmpResp>>() {
            });
            if (Objects.isNull(seeyonAccountEmpResps)) {
                throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
            }
        } catch (SeeyonApiException ex) {
            log.info("获取人员数据数据异常 {}", ex);
        }
        log.info("第三方公司拉取人员总数量: {},{}", accountId, seeyonAccountEmpResps.size());
        return seeyonAccountEmpResps;
    }


    @Override
    protected IThirdEmployeeService getThirdEmployeeService() {
        return iThirdEmployeeService;
    }

}
