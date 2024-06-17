package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.QueryParamEnum;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyDepartmentInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyPageBean;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologySubCompanyInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyUserInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyHrmService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import com.weaver.v8.hrm.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Title: EcologyHrmServiceImpl</p>
 * <p>Description: 泛微hrm服务实现</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/1 8:22 PM
 */
@Slf4j
@ServiceAspect
@Service
public class EcologyHrmServiceImpl implements IEcologyHrmService {

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
            log.warn("get user from fanwei error : {}", e.getMessage());
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
            log.warn("get user from fanwei error : {}", e.getMessage());
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
            log.warn("get department from fanwei error : {}", e.getMessage());
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
            log.warn("get user from fanwei error : {}", e.getMessage());
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
            log.warn("get subcompany from fanwei error : {}", e.getMessage());
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
                log.warn("泛微获取人员失败", e);
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
                log.warn("泛微获取人员失败", e);
                return Lists.newArrayList();
            }
        }
        return userBeanList;
    }

    @Override
    public List<EcologyUserInfo> getUserInfoListPage(OpenEcologyWorkflowConfig workflowConfig) {
        if (ObjectUtils.isEmpty(workflowConfig)) {
            throw new OpenApiArgumentException("泛微配置不存在");
        }
        List<EcologyUserInfo> allUserInfoList = new ArrayList<>();

        int curPage = 1;
        int pageSize = 100;

        Map<String, Object> queryParam = initPageParam(workflowConfig, curPage, pageSize, QueryParamEnum.USER_PARAM);

        HrmServiceLocator locator = new HrmServiceLocator(workflowConfig.getWsUrl());
        HrmServicePortType httpPort = null;
        try {
            httpPort = locator.getHrmServiceHttpPort();
            String hrmUserInfoWithPage = httpPort.getHrmUserInfoWithPage(JsonUtils.toJson(queryParam));
            log.info("【泛微】遍历获取到的人员信息:{}", hrmUserInfoWithPage);
            EcologyPageBean<EcologyUserInfo> userPageBean = JsonUtils.toObj(hrmUserInfoWithPage, new TypeReference<EcologyPageBean<EcologyUserInfo>>() {
            });
            List<EcologyUserInfo> userInfoList = userPageBean == null ? new ArrayList<>() : userPageBean.getDataList();
            while (!ObjectUtils.isEmpty(userInfoList)) {
                allUserInfoList.addAll(userInfoList);
                queryParam.put("curpage", ++curPage);
                hrmUserInfoWithPage = httpPort.getHrmUserInfoWithPage(JsonUtils.toJson(queryParam));
                log.info("【泛微】遍历获取到的人员信息:{}", hrmUserInfoWithPage);
                userPageBean = JsonUtils.toObj(hrmUserInfoWithPage, new TypeReference<EcologyPageBean<EcologyUserInfo>>() {
                });
                userInfoList = userPageBean == null ? new ArrayList<>() : userPageBean.getDataList();
            }
        } catch (RemoteException | ServiceException e) {
            log.warn("泛微获取人员失败", e);
        }
        log.info("【泛微】共获取人员数量:{}", allUserInfoList.size());

        return allUserInfoList;
    }

    @Override
    public List<EcologySubCompanyInfo> getSubCompanyInfoListPage(OpenEcologyWorkflowConfig workflowConfig) {
        if (ObjectUtils.isEmpty(workflowConfig)) {
            throw new OpenApiArgumentException("泛微配置不存在");
        }
        List<EcologySubCompanyInfo> allSubCompanyList = new ArrayList<>();

        int curPage = 1;
        int pageSize = 100;

        Map<String, Object> queryParam = initPageParam(workflowConfig, curPage, pageSize, QueryParamEnum.SUBCOMPANY_PARAM);

        HrmServiceLocator locator = new HrmServiceLocator(workflowConfig.getWsUrl());
        HrmServicePortType httpPort = null;
        try {
            httpPort = locator.getHrmServiceHttpPort();
            String hrmSubcompanyInfoWithPage = httpPort.getHrmSubcompanyInfoWithPage(JsonUtils.toJson(queryParam));
            log.info("【泛微】遍历获取分部信息:{}", hrmSubcompanyInfoWithPage);
            EcologyPageBean<EcologySubCompanyInfo> subCompanyInfoEcologyPageBean = JsonUtils.toObj(hrmSubcompanyInfoWithPage, new TypeReference<EcologyPageBean<EcologySubCompanyInfo>>() {
            });
            List<EcologySubCompanyInfo> subCompanyInfos = subCompanyInfoEcologyPageBean == null ? new ArrayList<>() : subCompanyInfoEcologyPageBean.getDataList();
            while (!ObjectUtils.isEmpty(subCompanyInfos)) {
                allSubCompanyList.addAll(subCompanyInfos);
                queryParam.put("curpage", ++curPage);
                hrmSubcompanyInfoWithPage = httpPort.getHrmSubcompanyInfoWithPage(JsonUtils.toJson(queryParam));
                log.info("【泛微】遍历获取分部信息:{}", hrmSubcompanyInfoWithPage);
                subCompanyInfoEcologyPageBean = JsonUtils.toObj(hrmSubcompanyInfoWithPage, new TypeReference<EcologyPageBean<EcologySubCompanyInfo>>() {
                });
                subCompanyInfos = subCompanyInfoEcologyPageBean == null ? new ArrayList<>() : subCompanyInfoEcologyPageBean.getDataList();
            }
        } catch (ServiceException | RemoteException e) {
            log.warn("获取泛微分部失败", e);
        }

        log.info("共获取分部数量:{}", allSubCompanyList.size());
        return allSubCompanyList;
    }

    /**
     * 初始化分页查询参数
     *
     * @param workflowConfig
     * @param curPage
     * @param pageSize
     * @return
     */
    private Map<String, Object> initPageParam(OpenEcologyWorkflowConfig workflowConfig, int curPage, int pageSize, QueryParamEnum queryParamEnum) {
        Map<String, Object> queryParam = initQueryParam(workflowConfig, queryParamEnum);

        // 是否需要传token
        if (!StringUtils.isBlank(workflowConfig.getTokenKey())) {
            Map<String, String> token = getToken(workflowConfig.getTokenKey());
            queryParam.put("token", token);
        }

        queryParam.put("curpage", curPage);
        queryParam.put("pagesize", pageSize);

        return queryParam;
    }

    @Override
    public List<EcologyDepartmentInfo> getDepartmentListPage(OpenEcologyWorkflowConfig workflowConfig) {
        if (ObjectUtils.isEmpty(workflowConfig)) {
            throw new OpenApiArgumentException("泛微配置不存在");
        }

        List<EcologyDepartmentInfo> allDepartmentList = new ArrayList<>();

        int curPage = 1;
        int pageSize = 100;

        Map<String, Object> queryParam = initPageParam(workflowConfig, curPage, pageSize, QueryParamEnum.DEPARTMENT_PARAM);

        HrmServiceLocator locator = new HrmServiceLocator(workflowConfig.getWsUrl());
        HrmServicePortType httpPort = null;

        try {
            httpPort = locator.getHrmServiceHttpPort();
            String departmentInfoWithPage = httpPort.getDepartmentInfoWithPage(JsonUtils.toJson(queryParam));
            log.info("【泛微】遍历获取部门信息:{}", departmentInfoWithPage);
            EcologyPageBean<EcologyDepartmentInfo> departmentInfoEcologyPageBean = JsonUtils.toObj(departmentInfoWithPage, new TypeReference<EcologyPageBean<EcologyDepartmentInfo>>() {
            });
            List<EcologyDepartmentInfo> departmentInfoList = departmentInfoEcologyPageBean == null ? new ArrayList<>() : departmentInfoEcologyPageBean.getDataList();
            while (!ObjectUtils.isEmpty(departmentInfoList)) {
                allDepartmentList.addAll(departmentInfoList);
                queryParam.put("curpage", ++curPage);
                departmentInfoWithPage = httpPort.getDepartmentInfoWithPage(JsonUtils.toJson(queryParam));
                log.info("【泛微】遍历获取部门信息:{}", departmentInfoWithPage);
                departmentInfoEcologyPageBean = JsonUtils.toObj(departmentInfoWithPage, new TypeReference<EcologyPageBean<EcologyDepartmentInfo>>() {
                });
                departmentInfoList = departmentInfoEcologyPageBean == null ? new ArrayList<>() : departmentInfoEcologyPageBean.getDataList();
            }
        } catch (ServiceException | RemoteException e) {
            log.warn("获取泛微部门失败", e);
        }
        log.info("【泛微】共获取部门数量:{}", allDepartmentList.size());
        return allDepartmentList;
    }

    /**
     * 初始化查询参数
     *
     * @param workflowConfig
     * @return
     */
    private Map<String, Object> initQueryParam(OpenEcologyWorkflowConfig workflowConfig, QueryParamEnum queryParamEnum) {
        Map<String, Object> map = new HashMap<>();
        String paramJson = workflowConfig.getParamJson();
        if (!StringUtils.isBlank(paramJson)) {
            Map<String, Object> param = JsonUtils.toObj(paramJson, new TypeReference<Map<String, Object>>() {
            });
            if (!ObjectUtils.isEmpty(param)) {
                Object val = param.get(queryParamEnum.getType());
                if (!ObjectUtils.isEmpty(val)) {
                    Map<String, Object> valMap = JsonUtils.toObj(JsonUtils.toJson(val), new TypeReference<Map<String, Object>>() {
                    });
                    if (!ObjectUtils.isEmpty(valMap)) {
                        map.putAll(valMap);
                    }
                }
            }
        }
        return map;
    }

    /**
     * 获取token
     *
     * @param key
     * @return
     */
    private Map<String, String> getToken(String key) {
        Map<String, String> map = new HashMap<>();
        long l = System.currentTimeMillis();
        String code = key.concat(Long.toString(l));
        String md5key = DigestUtils.md5DigestAsHex(code.getBytes()).toUpperCase();
        map.put("key", md5key);
        map.put("ts", Long.toString(l));
        return map;
    }
}
