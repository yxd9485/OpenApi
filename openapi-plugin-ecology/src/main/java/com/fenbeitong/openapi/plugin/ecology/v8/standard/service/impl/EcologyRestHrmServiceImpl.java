package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.EcologyRestApi;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.QueryParamEnum;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyDepartmentInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyPageBean;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologySubCompanyInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyUserInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyResturlConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyHrmService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyRestHrmService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.weaver.v8.hrm.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 泛微 restful 组织架构实现
 * @Auther zhang.peng
 * @Date 2022/03/04
 */
@Slf4j
@ServiceAspect
@Service
public class EcologyRestHrmServiceImpl implements IEcologyRestHrmService {

    private static final int CUR_PAGE = 1;
    private static final int PAGE_SIZE = 100;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Override
    public UserBean getUserByUserCode(OpenEcologyWorkflowConfig workflowConfig, String userCode) {
        HrmServiceLocator locator = new HrmServiceLocator(workflowConfig.getWsUrl());
        HrmServicePortType httpPort = null;
        try {
            httpPort = locator.getHrmServiceHttpPort();
            log.info("get user from fanwei ip:{},usercode:{}", workflowConfig.getWsIp(), userCode);
            UserBean[] hrmUserInfo = httpPort.getHrmUserInfo(workflowConfig.getWsIp(), userCode, null, null, null, null);
            log.info("get user from fanwei ip:{},usercode:{},result:{}", workflowConfig.getWsIp(), userCode, JsonUtils.toJson(hrmUserInfo));
            return ObjectUtils.isEmpty(hrmUserInfo) ? null : hrmUserInfo[0];
        } catch (ServiceException | RemoteException e) {
            log.error("get user from fanwei error : {}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<UserBean> getUserByUserId(OpenEcologyWorkflowConfig workflowConfig, List<String> userIdList) {
        HrmServiceLocator locator = new HrmServiceLocator(workflowConfig.getWsUrl());
        HrmServicePortType httpPort = null;
        try {
            httpPort = locator.getHrmServiceHttpPort();
            log.info("get user from fanwei ip:{},userIdList:{}", workflowConfig.getWsIp(), CollectionUtils.isBlank(userIdList) ? "null" : userIdList.toArray());
            UserBean[] hrmUserInfo = httpPort.getHrmUserInfo(workflowConfig.getWsIp(), null, null, null, null, null);
            if (hrmUserInfo != null && hrmUserInfo.length > 0) {
                return Lists.newArrayList(hrmUserInfo).stream().filter(userIdList::contains).collect(Collectors.toList());
            }
        } catch (ServiceException | RemoteException e) {
            log.error("get user from fanwei error : {}", e.getMessage());
        }
        return Lists.newArrayList();
    }

    @Override
    public List<DepartmentBean> getDepartmentInfoList(OpenEcologyWorkflowConfig workflowConfig) {
        //取部门
        HrmServiceLocator locator = new HrmServiceLocator(workflowConfig.getWsUrl());
        HrmServicePortType httpPort = null;
        try {
            httpPort = locator.getHrmServiceHttpPort();
            DepartmentBean[] hrmDepartmentInfo = httpPort.getHrmDepartmentInfo(workflowConfig.getWsIp(), null);
            log.info("get department from fanwei，companyId={}, res={}", workflowConfig.getCompanyId(), JsonUtils.toJson(hrmDepartmentInfo));
            if (hrmDepartmentInfo != null && hrmDepartmentInfo.length > 0) {
                return Lists.newArrayList(hrmDepartmentInfo);
            }
        } catch (ServiceException | RemoteException e) {
            log.error("get department from fanwei error : {}", e.getMessage());
        }
        return Lists.newArrayList();
    }

    @Override
    public List<UserBean> getUserInfoList(OpenEcologyWorkflowConfig workflowConfig) {
        HrmServiceLocator locator = new HrmServiceLocator(workflowConfig.getWsUrl());
        HrmServicePortType httpPort = null;
        try {
            httpPort = locator.getHrmServiceHttpPort();
            UserBean[] hrmUserInfo = httpPort.getHrmUserInfo(workflowConfig.getWsIp(), null, null, null, null, null);
            log.info("get user from fanwei，companyId={}, res={}", workflowConfig.getCompanyId(), JsonUtils.toJson(hrmUserInfo));
            if (hrmUserInfo != null && hrmUserInfo.length > 0) {
                return Lists.newArrayList(hrmUserInfo);
            }
            log.info("get user from fanwei，companyId={}, res={}", workflowConfig.getCompanyId(), JsonUtils.toJson(hrmUserInfo));
        } catch (ServiceException | RemoteException e) {
            log.error("get user from fanwei error : {}", e.getMessage());
        }
        return Lists.newArrayList();
    }


    @Override
    public List<SubCompanyBean> getHrmSubcompanyInfo(OpenEcologyWorkflowConfig workflowConfig) {
        HrmServiceLocator locator = new HrmServiceLocator(workflowConfig.getWsUrl());
        HrmServicePortType httpPort = null;
        try {
            httpPort = locator.getHrmServiceHttpPort();
            log.info("get subcompany from fanwei start，companyId={}, url={}", workflowConfig.getCompanyId(), workflowConfig.getWsUrl());
            SubCompanyBean[] subCompanyBean = httpPort.getHrmSubcompanyInfo(workflowConfig.getWsIp());
            log.info("get subcompany from fanwei end，companyId={}, res={}", workflowConfig.getCompanyId(), JsonUtils.toJson(subCompanyBean));
            if (subCompanyBean != null && subCompanyBean.length > 0) {
                return Lists.newArrayList(subCompanyBean);
            }
        } catch (ServiceException | RemoteException e) {
            log.error("get subcompany from fanwei error : {}", e.getMessage());
        }
        return Lists.newArrayList();
    }


    @Override
    public List<UserBean> getUserInfoListWithSubCompany(OpenEcologyWorkflowConfig workflowConfig) {
        List<UserBean> userBeanList = Lists.newArrayList();
        List<SubCompanyBean> hrmSubcompanyInfo = getHrmSubcompanyInfo(workflowConfig);
        if (!ObjectUtils.isEmpty(hrmSubcompanyInfo)) {
            HrmServiceLocator locator = new HrmServiceLocator(workflowConfig.getWsUrl());
            HrmServicePortType httpPort = null;
            try {
                httpPort = locator.getHrmServiceHttpPort();
                for (SubCompanyBean subCompanyBean : hrmSubcompanyInfo) {
                    String subcompanyid = subCompanyBean.get_subcompanyid();
                    UserBean[] hrmUserInfo = httpPort.getHrmUserInfo(workflowConfig.getWsIp(), subcompanyid, null, null, null, null);
                    log.info("get user with subcompany from fanwei，companyId={}, res={}", workflowConfig.getCompanyId(), JsonUtils.toJson(subCompanyBean));
                    if (hrmUserInfo != null && hrmUserInfo.length > 0) {
                        userBeanList.addAll(Lists.newArrayList(hrmUserInfo));
                    }
                }

            } catch (ServiceException | RemoteException e) {
                log.error("泛微获取人员失败", e);
                return Lists.newArrayList();
            }
        }
        return userBeanList;
    }


    @Override
    public List<UserBean> getUserInfoListWithDepartment(OpenEcologyWorkflowConfig workflowConfig) {
        List<UserBean> userBeanList = Lists.newArrayList();
        List<DepartmentBean> departmentInfoList = getDepartmentInfoList(workflowConfig);
        if (!ObjectUtils.isEmpty(departmentInfoList)) {
            HrmServiceLocator locator = new HrmServiceLocator(workflowConfig.getWsUrl());
            HrmServicePortType httpPort = null;
            try {
                httpPort = locator.getHrmServiceHttpPort();
                for (DepartmentBean departmentBean : departmentInfoList) {
                    String departmentid = departmentBean.get_departmentid();
                    UserBean[] hrmUserInfo = httpPort.getHrmUserInfo(workflowConfig.getWsIp(), null, departmentid, null, null, null);
                    log.info("get user with department from fanwei，companyId={}, departmentId={}, res={}", workflowConfig.getCompanyId(), departmentid, JsonUtils.toJson(hrmUserInfo));
                    if (hrmUserInfo != null && hrmUserInfo.length > 0) {
                        userBeanList.addAll(Lists.newArrayList(hrmUserInfo));
                    }
                }

            } catch (ServiceException | RemoteException e) {
                log.error("泛微获取人员失败", e);
                return Lists.newArrayList();
            }
        }
        return userBeanList;
    }

    @Override
    public List<EcologyUserInfo> getUserInfoListPage(OpenEcologyResturlConfig resturlConfig) {
        if (ObjectUtils.isEmpty(resturlConfig)) {
            throw new OpenApiArgumentException("泛微配置不存在");
        }
        List<EcologyUserInfo> allUserInfoList = new ArrayList<>();

        int curPage = 1;
        String domain = resturlConfig.getDomainName();
        String url = domain + EcologyRestApi.GET_USERS;
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("curpage", curPage);
        queryParam.put("pagesize", PAGE_SIZE);
        if (!StringUtils.isBlank(resturlConfig.getUserParams())){
            Map<String,String> parmas = JsonUtils.toObj(resturlConfig.getUserParams(),Map.class);
            queryParam.putAll(parmas);
        }
        Map<String, Object> query = new HashMap<>();
        query.put("params",queryParam);

        try {
            String hrmUserInfoWithPage = RestHttpUtils.postJson(url,JsonUtils.toJson(query));
            Map result = JsonUtils.toObj(hrmUserInfoWithPage,Map.class);
            EcologyPageBean<EcologyUserInfo> userPageBean = JsonUtils.toObj(JsonUtils.toJson(result.get("data")), new TypeReference<EcologyPageBean<EcologyUserInfo>>() {
            });
            List<EcologyUserInfo> userInfoList = userPageBean == null ? new ArrayList<>() : userPageBean.getDataList();
            while (!ObjectUtils.isEmpty(userInfoList)) {
                allUserInfoList.addAll(userInfoList);
                queryParam.put("curpage", ++curPage);
                hrmUserInfoWithPage = RestHttpUtils.postJson(url,JsonUtils.toJson(query));
                Map addResult = JsonUtils.toObj(hrmUserInfoWithPage,Map.class);
                userPageBean = JsonUtils.toObj(JsonUtils.toJson(addResult.get("data")), new TypeReference<EcologyPageBean<EcologyUserInfo>>() {
                });
                addResult = null;
                userInfoList = userPageBean == null ? new ArrayList<>() : userPageBean.getDataList();
            }
        } catch (Exception e) {
            log.error("泛微获取人员失败", e);
        }

        log.info("共获取人员数量:{}", allUserInfoList.size());
        return allUserInfoList;
    }

    @Override
    public List<EcologySubCompanyInfo> getSubCompanyInfoListPage(OpenEcologyResturlConfig resturlConfig) {
        if (ObjectUtils.isEmpty(resturlConfig)) {
            throw new OpenApiArgumentException("泛微配置不存在");
        }
        List<EcologySubCompanyInfo> allSubCompanyList = new ArrayList<>();

        String domain = resturlConfig.getDomainName();
        String url = domain + EcologyRestApi.GET_SUB_COMPANY;
        int curPage = 1;

        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("curpage", curPage);
        queryParam.put("pagesize", PAGE_SIZE);
        Map<String, Object> query = new HashMap<>();
        query.put("params",queryParam);

        try {
            // 返回结果
            String hrmSubcompanyInfoWithPage = RestHttpUtils.postJson(url,JsonUtils.toJson(query));
            Map result = JsonUtils.toObj(hrmSubcompanyInfoWithPage,Map.class);
            EcologyPageBean<EcologySubCompanyInfo> subCompanyInfoEcologyPageBean = JsonUtils.toObj(JsonUtils.toJson(result.get("data")), new TypeReference<EcologyPageBean<EcologySubCompanyInfo>>() {
            });
            List<EcologySubCompanyInfo> subCompanyInfos = subCompanyInfoEcologyPageBean == null ? new ArrayList<>() : subCompanyInfoEcologyPageBean.getDataList();
            while (!ObjectUtils.isEmpty(subCompanyInfos)) {
                allSubCompanyList.addAll(subCompanyInfos);
                queryParam.put("curpage", ++curPage);
                hrmSubcompanyInfoWithPage = RestHttpUtils.postJson(url,JsonUtils.toJson(query));
                Map addResult = JsonUtils.toObj(hrmSubcompanyInfoWithPage,Map.class);
                subCompanyInfoEcologyPageBean = JsonUtils.toObj(JsonUtils.toJson(addResult.get("data")), new TypeReference<EcologyPageBean<EcologySubCompanyInfo>>() {
                });
                addResult = null;
                subCompanyInfos = subCompanyInfoEcologyPageBean == null ? new ArrayList<>() : subCompanyInfoEcologyPageBean.getDataList();
            }
        } catch ( Exception e) {
            log.error("获取泛微分部失败", e);
        }

        log.info("共获取分部数量:{}", allSubCompanyList.size());
        return allSubCompanyList;
    }

    @Override
    public List<EcologyDepartmentInfo> getDepartmentListPage(OpenEcologyResturlConfig resturlConfig) {
        if (ObjectUtils.isEmpty(resturlConfig)) {
            throw new OpenApiArgumentException("泛微配置不存在");
        }

        List<EcologyDepartmentInfo> allDepartmentList = new ArrayList<>();

        String domain = resturlConfig.getDomainName();
        String url = domain + EcologyRestApi.GET_DEPARTMENT;

        int curPage = 1;

        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("curpage", curPage);
        queryParam.put("pagesize", PAGE_SIZE);
        if (!StringUtils.isBlank(resturlConfig.getDepartmentParams())){
            Map<String,String> parmas = JsonUtils.toObj(resturlConfig.getDepartmentParams(),Map.class);
            queryParam.putAll(parmas);
        }
        Map<String, Object> query = new HashMap<>();
        query.put("params",queryParam);

        try {
            String departmentInfoWithPage = RestHttpUtils.postJson(url,JsonUtils.toJson(query));
            Map result = JsonUtils.toObj(departmentInfoWithPage,Map.class);
            EcologyPageBean<EcologyDepartmentInfo> departmentInfoEcologyPageBean = JsonUtils.toObj(JsonUtils.toJson(result.get("data")), new TypeReference<EcologyPageBean<EcologyDepartmentInfo>>() {
            });
            List<EcologyDepartmentInfo> departmentInfoList = departmentInfoEcologyPageBean == null ? new ArrayList<>() : departmentInfoEcologyPageBean.getDataList();
            while (!ObjectUtils.isEmpty(departmentInfoList)) {
                allDepartmentList.addAll(departmentInfoList);
                queryParam.put("curpage", ++curPage);
                departmentInfoWithPage = RestHttpUtils.postJson(url,JsonUtils.toJson(query));
                Map addResult = JsonUtils.toObj(departmentInfoWithPage,Map.class);
                departmentInfoEcologyPageBean = JsonUtils.toObj(JsonUtils.toJson(addResult.get("data")), new TypeReference<EcologyPageBean<EcologyDepartmentInfo>>() {
                });
                addResult = null;
                departmentInfoList = departmentInfoEcologyPageBean == null ? new ArrayList<>() : departmentInfoEcologyPageBean.getDataList();
            }
        } catch (Exception e) {
            log.error("获取泛微部门失败", e);
        }

        log.info("共获取部门数量:{}", allDepartmentList.size());
        return allDepartmentList;
    }

}
