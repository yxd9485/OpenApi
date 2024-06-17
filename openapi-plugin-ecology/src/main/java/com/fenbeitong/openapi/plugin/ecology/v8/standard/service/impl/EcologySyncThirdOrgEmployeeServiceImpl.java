package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.ecology.v8.common.EcologyConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowConfigDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyDepartmentInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologySubCompanyInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyUserInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyHrmService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologySyncThirdOrgEmployeeService;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.weaver.v8.hrm.DepartmentBean;
import com.weaver.v8.hrm.SubCompanyBean;
import com.weaver.v8.hrm.UserBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.exception.ArgumentException;

import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

/**
 * 三方人员组织机构同步
 *
 * @author lizhen
 * @date 2020/12/8
 */
@ServiceAspect
@Service
@Slf4j
public class EcologySyncThirdOrgEmployeeServiceImpl implements IEcologySyncThirdOrgEmployeeService {

    @Autowired
    private IEcologyHrmService ecologyHrmService;

    @Autowired
    private OpenEcologyWorkflowConfigDao workflowConfigDao;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    @Autowired
    private DepartmentUtilService departmentUtilService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    @Async
    public void syncThirdOrgEmployee(String companyId) {
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.ECOLOGY_EMPLOYEE_SYNC);
                OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.ECOLOGY_DEPARTMENT_SYNC);
                boolean employeeNeedFilter = employeeConfig != null;
                boolean departmentNeedFilter = departmentConfig != null;
                OpenEcologyWorkflowConfig workflowConfig = workflowConfigDao.findByCompanyId(companyId);
                // 获取组织机构人员
                List<DepartmentBean> departmentInfoList = getSubCompanyAndDepartmentInfoList(workflowConfig, companyId);
                List<UserBean> userInfoList = ecologyHrmService.getUserInfoList(workflowConfig);
                // 转换部门
                List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
                if (!ObjectUtils.isEmpty(departmentInfoList)) {
                    initRootNode(departmentInfoList, companyId);
                    for (DepartmentBean departmentBean : departmentInfoList) {
                        if (EcologyConstant.ORGANIZATION_CANCELED.equals(departmentBean.get_canceled())) {
                            continue;
                        }
                        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
                        openThirdOrgUnitDTO.setCompanyId(companyId);
                        openThirdOrgUnitDTO.setThirdOrgUnitId(departmentBean.get_departmentid());
                        openThirdOrgUnitDTO.setThirdOrgUnitName(departmentBean.get_fullname());
                        openThirdOrgUnitDTO.setThirdOrgUnitParentId(departmentBean.get_supdepartmentid());
                        departmentList.add(openThirdOrgUnitDTO);
                        // 脚本
                        OpenThirdOrgUnitDTO targetDTO = null;
                        if (departmentNeedFilter) {
                            EcologyDepartmentInfo departmentInfo = new EcologyDepartmentInfo();
                            BeanUtils.copyProperties(departmentBean,departmentInfo);
                            targetDTO = departmentBeforeSyncFilter(departmentConfig, departmentInfo, openThirdOrgUnitDTO);
                        }
                        departmentList.add(targetDTO == null ? openThirdOrgUnitDTO : targetDTO);
                    }
                    departmentList = departmentUtilService.deparmentSort(departmentList, companyId);
                    //根部门清除掉父级部门
                    departmentList.get(0).setThirdOrgUnitParentId(null);
                }
                // 转换人员
                List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
                Set<String> set = new HashSet<>();
                if (!ObjectUtils.isEmpty(userInfoList)) {
                    for (UserBean userBean : userInfoList) {
                        String status = userBean.getStatus();
                        // 有效人员 并且为主账号
                        if (EcologyConstant.USER_VALID_STATUS.contains(status) && !userBean.getAccounttype().equals(EcologyConstant.ACCOUNTTYPE)) {
                            OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                            openThirdEmployeeDTO.setCompanyId(companyId);
                            openThirdEmployeeDTO.setThirdEmployeeId(StringUtils.obj2str(userBean.getUserid()));
                            openThirdEmployeeDTO.setThirdDepartmentId(userBean.getDepartmentid());
                            openThirdEmployeeDTO.setThirdEmployeeName(userBean.getLastname());
                            openThirdEmployeeDTO.setThirdEmployeePhone(userBean.getMobile());
                            openThirdEmployeeDTO.setThirdEmployeeEmail(userBean.getEmail());
                            String gender = userBean.getSex();
                            if (!StringUtils.isBlank(gender)) {
                                if ("男".equals(gender)) {
                                    openThirdEmployeeDTO.setThirdEmployeeGender(1);
                                } else {
                                    openThirdEmployeeDTO.setThirdEmployeeGender(2);
                                }
                            }
                            openThirdEmployeeDTO.setThirdEmployeeIdCard(userBean.getCertificatenum());
                            if (!ObjectUtils.isEmpty(userBean.getSeclevel())) {
                                openThirdEmployeeDTO.setThirdEmployeeRoleTye(userBean.getSeclevel());
                            }
                            // 脚本
                            OpenThirdEmployeeDTO targetDTO = null;
                            if (employeeNeedFilter) {
                                EcologyUserInfo tempEcologyUserInfo = new EcologyUserInfo();
                                BeanUtils.copyProperties(userBean,tempEcologyUserInfo);
                                targetDTO = employeeBeforeSyncFilter(employeeConfig, tempEcologyUserInfo, openThirdEmployeeDTO);
                            }
                            employeeList.add(targetDTO == null ? openThirdEmployeeDTO : targetDTO);
                            set.add(openThirdEmployeeDTO.getThirdEmployeeId());
                        }
                    }
                }
                openSyncThirdOrgService.syncThird(OpenType.FANWEI.getType(), companyId, departmentList, employeeList);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("未获取到锁，companyId={}", companyId);
            throw new ArgumentException("未获取到锁");
        }
    }

    @Override
    @Async
    public void syncThirdOrgEmployeePage(String companyId) {
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.ECOLOGY_EMPLOYEE_SYNC);
                OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.ECOLOGY_DEPARTMENT_SYNC);
                boolean employeeNeedFilter = employeeConfig != null;
                boolean departmentNeedFilter = departmentConfig != null;

                OpenEcologyWorkflowConfig workflowConfig = workflowConfigDao.findByCompanyId(companyId);
                // 获取组织机构人员
                List<EcologyDepartmentInfo> departmentInfoList = getSubCompanyAndDepartmentInfoListPage(workflowConfig, companyId);
                List<EcologyUserInfo> userInfoList = ecologyHrmService.getUserInfoListPage(workflowConfig);
                // 转换部门
                List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
                if (!ObjectUtils.isEmpty(departmentInfoList)) {
                    initRootNodeV2(departmentInfoList, companyId);
                    for (EcologyDepartmentInfo departmentBean : departmentInfoList) {
                        if (EcologyConstant.ORGANIZATION_CANCELED.equals(departmentBean.getCanceled())) {
                            continue;
                        }
                        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
                        openThirdOrgUnitDTO.setCompanyId(companyId);
                        openThirdOrgUnitDTO.setThirdOrgUnitId(departmentBean.getId());
                        openThirdOrgUnitDTO.setThirdOrgUnitName(departmentBean.getDepartmentname());
                        openThirdOrgUnitDTO.setThirdOrgUnitParentId(departmentBean.getSupdepid());

                        // 脚本
                        OpenThirdOrgUnitDTO targetDTO = null;
                        if (departmentNeedFilter) {
                            targetDTO = departmentBeforeSyncFilter(departmentConfig, departmentBean, openThirdOrgUnitDTO);
                        }
                        departmentList.add(targetDTO == null ? openThirdOrgUnitDTO : targetDTO);
                    }
                    departmentList = departmentUtilService.deparmentSort(departmentList, companyId);
                    //根部门清除掉父级部门
                    departmentList.get(0).setThirdOrgUnitParentId(null);
                }
                // 转换人员
                List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
                Set<String> set = new HashSet<>();
                if (!ObjectUtils.isEmpty(userInfoList)) {
                    for (EcologyUserInfo userBean : userInfoList) {
                        String status = userBean.getStatus();
                        // 有效人员 并且为主账号
                        if (EcologyConstant.USER_VALID_STATUS.contains(status) && !String.valueOf(EcologyConstant.ACCOUNTTYPE).equals(userBean.getAccounttype())) {
                            OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                            openThirdEmployeeDTO.setCompanyId(companyId);
                            openThirdEmployeeDTO.setThirdEmployeeId(StringUtils.obj2str(userBean.getId()));
                            openThirdEmployeeDTO.setThirdDepartmentId(userBean.getDepartmentid());
                            openThirdEmployeeDTO.setThirdEmployeeName(userBean.getLastname());
                            openThirdEmployeeDTO.setThirdEmployeePhone(userBean.getMobile());
                            openThirdEmployeeDTO.setThirdEmployeeEmail(userBean.getEmail());
                            String gender = userBean.getSex();
                            if (!StringUtils.isBlank(gender)) {
                                if ("男".equals(gender)) {
                                    openThirdEmployeeDTO.setThirdEmployeeGender(1);
                                } else {
                                    openThirdEmployeeDTO.setThirdEmployeeGender(2);
                                }
                            }
                            openThirdEmployeeDTO.setThirdEmployeeIdCard(userBean.getCertificatenum());
                            if (!ObjectUtils.isEmpty(userBean.getSeclevel())) {
                                openThirdEmployeeDTO.setThirdEmployeeRoleTye(userBean.getSeclevel());
                            }
                            // 脚本
                            OpenThirdEmployeeDTO targetDTO = null;
                            if (employeeNeedFilter) {
                                targetDTO = employeeBeforeSyncFilter(employeeConfig, userBean, openThirdEmployeeDTO);
                            }
                            employeeList.add(targetDTO == null ? openThirdEmployeeDTO : targetDTO);
                            set.add(openThirdEmployeeDTO.getThirdEmployeeId());
                        }
                    }
                }
                openSyncThirdOrgService.syncThird(OpenType.FANWEI.getType(), companyId, departmentList, employeeList);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("未获取到锁，companyId={}", companyId);
            throw new ArgumentException("未获取到锁");
        }
    }

    /**
     * 部门同步前按需过滤
     *
     * @param departmentConfig
     * @param departmentInfo
     * @param openThirdOrgUnitDTO
     * @return
     */
    private OpenThirdOrgUnitDTO departmentBeforeSyncFilter(OpenThirdScriptConfig departmentConfig, EcologyDepartmentInfo departmentInfo, OpenThirdOrgUnitDTO openThirdOrgUnitDTO) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("departmentInfo", departmentInfo);
            put("openThirdOrgUnitDTO", openThirdOrgUnitDTO);
        }};
        addParam(departmentConfig, params);
        return (OpenThirdOrgUnitDTO) EtlUtils.execute(departmentConfig.getScript(), params);
    }

    private void addParam(OpenThirdScriptConfig config, Map<String, Object> params) {
        Map<String, Object> param = JsonUtils.toObj(config.getParamJson(), new TypeReference<Map<String, Object>>() {
        });
        if (org.apache.commons.lang3.StringUtils.isNotBlank(config.getParamJson()) && !ObjectUtils.isEmpty(param)) {
            params.putAll(param);
        }
    }

    /**
     * 人员同步前按需过滤
     *
     * @param employeeConfig
     * @param userInfo
     * @param openThirdEmployeeDTO
     * @return
     */
    private OpenThirdEmployeeDTO employeeBeforeSyncFilter(OpenThirdScriptConfig employeeConfig, EcologyUserInfo userInfo, OpenThirdEmployeeDTO openThirdEmployeeDTO) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userInfo", userInfo);
            put("openThirdEmployeeDTO", openThirdEmployeeDTO);
        }};
        addParam(employeeConfig, params);
        return (OpenThirdEmployeeDTO) EtlUtils.execute(employeeConfig.getScript(), params);
    }

    /**
     * 分页获取分部和部门，将分部接入部门，组成部门树
     *
     * @param workflowConfig
     * @param companyId
     * @return
     */
    private List<EcologyDepartmentInfo> getSubCompanyAndDepartmentInfoListPage(OpenEcologyWorkflowConfig workflowConfig, String companyId) {
        // 取分部
        List<EcologySubCompanyInfo> hrmSubcompanyInfo = ecologyHrmService.getSubCompanyInfoListPage(workflowConfig);
        List<EcologyDepartmentInfo> subCompanyDepartment = new ArrayList<>();
        if (!ObjectUtils.isEmpty(hrmSubcompanyInfo)) {
            for (EcologySubCompanyInfo subCompanyBean : hrmSubcompanyInfo) {
                if (EcologyConstant.ORGANIZATION_CANCELED.equals(subCompanyBean.getCanceled())) {
                    continue;
                }
                String fullname = subCompanyBean.getSubcompanydesc();
                String subcompanyid = subCompanyBean.getId();
                String supsubcompanyid = subCompanyBean.getSupsubcomid();
                //非根分部的ID加前缀
                if (!EcologyConstant.ECOLOGY_ROOT_DEPARTMENT_ID.equals(subcompanyid)) {
                    subcompanyid = "sc_" + subcompanyid;
                }
                //非一级分部的父ID加前缀
                if (!EcologyConstant.ECOLOGY_ROOT_DEPARTMENT_ID.equals(supsubcompanyid)) {
                    supsubcompanyid = "sc_" + supsubcompanyid;
                }
                EcologyDepartmentInfo departmentBean = new EcologyDepartmentInfo();
                departmentBean.setSubcompanyid1(subcompanyid);
                departmentBean.setId(subcompanyid);
                departmentBean.setSupdepid(supsubcompanyid);
                departmentBean.setDepartmentname(fullname);
                subCompanyDepartment.add(departmentBean);
            }
        }
        List<EcologyDepartmentInfo> departmentInfoList = ecologyHrmService.getDepartmentListPage(workflowConfig);
        //将分部的根部门的父部门ID换成分部ID
        if (!ObjectUtils.isEmpty(departmentInfoList)) {
            for (EcologyDepartmentInfo departmentBean : departmentInfoList) {
                if (EcologyConstant.ECOLOGY_ROOT_DEPARTMENT_ID.equals(departmentBean.getSupdepid()) && !StringUtils.isBlank(departmentBean.getSubcompanyid1())) {
                    departmentBean.setSupdepid("sc_" + departmentBean.getSubcompanyid1());
                }
            }
            departmentInfoList.addAll(subCompanyDepartment);
        }
        return departmentInfoList;
    }


    /**
     * 获取分部和部门，将分部接入部门，组成部门树
     *
     * @param workflowConfig
     * @param companyId
     * @return
     */
    public List<DepartmentBean> getSubCompanyAndDepartmentInfoList(OpenEcologyWorkflowConfig workflowConfig, String companyId) {
        //取分部
        List<SubCompanyBean> hrmSubcompanyInfo = ecologyHrmService.getHrmSubcompanyInfo(workflowConfig);
        List<DepartmentBean> subCompanyDepartment = new ArrayList<>();
        if (!ObjectUtils.isEmpty(hrmSubcompanyInfo)) {
            for (SubCompanyBean subCompanyBean : hrmSubcompanyInfo) {
                if (EcologyConstant.ORGANIZATION_CANCELED.equals(subCompanyBean.get_canceled())) {
                    continue;
                }
                String fullname = subCompanyBean.get_fullname();
                String subcompanyid =  subCompanyBean.get_subcompanyid();
                String supsubcompanyid = subCompanyBean.get_supsubcompanyid();
                //非根分部的ID加前缀
                if (!EcologyConstant.ECOLOGY_ROOT_DEPARTMENT_ID.equals(subcompanyid)) {
                    subcompanyid = "sc_" + subcompanyid;
                }
                //非一级分部的父ID加前缀
                if (!EcologyConstant.ECOLOGY_ROOT_DEPARTMENT_ID.equals(supsubcompanyid)) {
                    supsubcompanyid = "sc_" + supsubcompanyid;
                }
                DepartmentBean departmentBean = new DepartmentBean();
                departmentBean.set_subcompanyid(subcompanyid);
                departmentBean.set_departmentid(subcompanyid);
                //根分部转成根部门
//                if (EcologyConstant.ECOLOGY_ROOT_DEPARTMENT_ID.equals(supsubcompanyid)) {
//                    AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
//                    departmentBean.set_supdepartmentid(companyId);
//                    departmentBean.set_fullname(authDefinition.getAppName());
//                } else {
//                    departmentBean.set_supdepartmentid(supsubcompanyid);
//                    departmentBean.set_fullname(fullname);
//                }
                departmentBean.set_supdepartmentid(supsubcompanyid);
                departmentBean.set_fullname(fullname);
                subCompanyDepartment.add(departmentBean);
            }
        }
        List<DepartmentBean> departmentInfoList = ecologyHrmService.getDepartmentInfoList(workflowConfig);
        //将分部的根部门的父部门ID换成分部ID
        if (!ObjectUtils.isEmpty(departmentInfoList)) {
            for (DepartmentBean departmentBean : departmentInfoList) {
                if (EcologyConstant.ECOLOGY_ROOT_DEPARTMENT_ID.equals(departmentBean.get_supdepartmentid()) && !StringUtils.isBlank(departmentBean.get_subcompanyid())) {
                    departmentBean.set_supdepartmentid("sc_" + departmentBean.get_subcompanyid());
                }
            }
            departmentInfoList.addAll(subCompanyDepartment);
        }
        return departmentInfoList;
    }


    public void initRootNode(List<DepartmentBean> departmentInfoList, String companyId) {
        //添加根结点
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
        DepartmentBean departmentBean = new DepartmentBean();
        departmentBean.set_shortname(authDefinition.getAppName());
        departmentBean.set_fullname(authDefinition.getAppName());
        departmentBean.set_departmentid(EcologyConstant.ECOLOGY_ROOT_DEPARTMENT_ID);
        departmentBean.set_supdepartmentid(companyId);
        departmentInfoList.add(departmentBean);
    }

    public void initRootNodeV2(List<EcologyDepartmentInfo> departmentInfoList, String companyId) {
        //添加根结点
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
        EcologyDepartmentInfo departmentBean = new EcologyDepartmentInfo();
        departmentBean.setDepartmentmark(authDefinition.getAppName());
        departmentBean.setDepartmentname(authDefinition.getAppName());
        departmentBean.setId(EcologyConstant.ECOLOGY_ROOT_DEPARTMENT_ID);
        departmentBean.setSupdepid(companyId);
        departmentInfoList.add(departmentBean);
    }
}
