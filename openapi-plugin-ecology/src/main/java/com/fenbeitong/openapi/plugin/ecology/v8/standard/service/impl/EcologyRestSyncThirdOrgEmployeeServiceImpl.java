package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.ecology.v8.common.EcologyConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyResturlConfigDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowConfigDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyDepartmentInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologySubCompanyInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyUserInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyResturlConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyRestHrmService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyRestSyncThirdOrgEmployeeService;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeOrgUnitDTO;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.exception.ArgumentException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 泛微 restful 三方人员组织机构同步
 * @author zhang.peng
 * @Date 2022/03/04
 */
@ServiceAspect
@Service
@Slf4j
public class EcologyRestSyncThirdOrgEmployeeServiceImpl implements IEcologyRestSyncThirdOrgEmployeeService {

    @Autowired
    private IEcologyRestHrmService ecologyHrmService;

    @Autowired
    private OpenEcologyResturlConfigDao resturlConfigDao;

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

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private OpenEcologyResturlConfigDao openEcologyResturlConfigDao;

    private static final String DEPARTMENT_MANAGER = "department_manager";

    private static final String DEPARTMENT_MANAGER_URL = "/api/getFBTData/getBmfzrList";

    @Override
    @Async
    public void restSyncThirdOrgEmployee(String companyId) {
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.ECOLOGY_EMPLOYEE_SYNC);
                OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.ECOLOGY_DEPARTMENT_SYNC);
                List<OpenMsgSetup> ecologyEffectiveStatus = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList(ItemCodeEnum.ECOLOGY_EFFECTIVE_STATUS_LIST.getCode()));
                List<String> effectiveStatus = CollectionUtils.isBlank(ecologyEffectiveStatus) || StringUtils.isBlank(ecologyEffectiveStatus.get(0).getStrVal1()) ?
                    Lists.newArrayList("0", "1", "2", "3") : JsonUtils.toObj(ecologyEffectiveStatus.get(0).getStrVal1(), new TypeReference<List<String>>() {});
                boolean employeeNeedFilter = employeeConfig != null;
                boolean departmentNeedFilter = departmentConfig != null;

                OpenEcologyResturlConfig resturlConfig = resturlConfigDao.findListOpenEcologyResturlConfig(companyId);
                // 获取组织机构人员
                List<EcologyDepartmentInfo> departmentInfoList = getSubCompanyAndDepartmentInfoListPage(resturlConfig);
                List<EcologyUserInfo> userInfoList = ecologyHrmService.getUserInfoListPage(resturlConfig);
                List<EcologyUserInfo> invalidUser = userInfoList.stream().filter(u -> CollectionUtils.isNotBlank(effectiveStatus) && !effectiveStatus.contains(u.getStatus())).collect(Collectors.toList());
                log.info("{}泛微过滤掉的无效人员数据{}",companyId,JsonUtils.toJson(invalidUser));
                //只保留有效状态的员工
                userInfoList = userInfoList.stream().filter(u -> CollectionUtils.isNotBlank(effectiveStatus) && effectiveStatus.contains(u.getStatus())).collect(Collectors.toList());
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
                if (!ObjectUtils.isEmpty(userInfoList)) {
                    // 账号状态从脚本判断
                    for (EcologyUserInfo userBean : userInfoList) {
                        buildEmployeeList(companyId,userBean,employeeNeedFilter,employeeConfig,employeeList);
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
    public void syncDepartmentManagers(String companyId){
        OpenEcologyResturlConfig resturlConfig = resturlConfigDao.findListOpenEcologyResturlConfig(companyId);
        List<EcologyDepartmentInfo> departmentInfoList = getSubCompanyAndDepartmentInfoListPage(resturlConfig);
        if (CollectionUtils.isBlank(departmentInfoList)){
            log.info("泛微部门数据为空 , 不执行");
            return;
        }
        OpenEcologyResturlConfig ecologyResturlConfig = openEcologyResturlConfigDao.findListOpenEcologyResturlConfig(companyId);
        if ( null == ecologyResturlConfig ){
            log.info("未配置获取部门主管接口 , 不执行");
            return;
        }
        String url = ecologyResturlConfig.getDomainName() + DEPARTMENT_MANAGER_URL;
        String result = RestHttpUtils.get(url,new HashMap<>());
        Map<String, Object> resultMap = JsonUtils.toObj(result, Map.class);
        Map<String, String> departmentId2ManagerMap = new HashMap<>();
        // 获取部门负责人数据
        buildDepartment2ManagerMap(resultMap, departmentId2ManagerMap);
        List<OpenThirdOrgUnitManagers> openThirdOrgUnitManagersList = new ArrayList<>();
        for (EcologyDepartmentInfo ecologyDepartmentInfo : departmentInfoList) {
            // 用配置的三方字段取自定义字段
            String departmentManagerId = "";
            if ( !departmentId2ManagerMap.isEmpty() ){
                departmentManagerId = departmentId2ManagerMap.get(ecologyDepartmentInfo.getId());
            }
            if ( StringUtils.isBlank(departmentManagerId) ){
                log.info("部门主管id为空 , 继续下一个");
                continue;
            }
            // 部门主管实体构建
            buildDepartmentManagers(openThirdOrgUnitManagersList,companyId,ecologyDepartmentInfo,departmentManagerId);
        }
        // 填充员工三方id
        openSyncThirdOrgService.setAllDepManageV2(openThirdOrgUnitManagersList, companyId);
    }

    private void buildDepartment2ManagerMap(Map<String, Object> resultMap, Map<String, String> departmentId2ManagerMap) {
        if ( null == resultMap ){
            return;
        }
        List<Map<String, String>> dataList = (List) resultMap.get("data");
        if (CollectionUtils.isBlank(dataList)){
            return;
        }
        for (Map<String, String> map : dataList) {
            if (StringUtils.isBlank(map.get("bmfzr"))){
                continue;
            }
            departmentId2ManagerMap.put(map.get("deptid"),map.get("bmfzr"));
        }
    }

    private void buildDepartmentManagers(List<OpenThirdOrgUnitManagers> openThirdOrgUnitManagersList , String companyId , EcologyDepartmentInfo ecologyDepartmentInfo , String departmentManagerId){
        openThirdOrgUnitManagersList.add(OpenThirdOrgUnitManagers.builder()
            .id(RandomUtils.bsonId())
            .companyId(companyId)
            .thirdOrgUnitId(ecologyDepartmentInfo.getId())
            .status(0)
            .createTime(new Date())
            .updateTime(new Date())
            .thirdEmployeeIds(departmentManagerId)
            .build());
        log.info("泛微部门主管数据 : {}",JsonUtils.toJson(openThirdOrgUnitManagersList));
    }

    private void buildEmployeeList(String companyId , EcologyUserInfo userBean , boolean employeeNeedFilter , OpenThirdScriptConfig employeeConfig , List<OpenThirdEmployeeDTO> employeeList){
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdEmployeeId(StringUtils.obj2str(userBean.getId()));
        openThirdEmployeeDTO.setThirdDepartmentId(userBean.getDepartmentid());
        openThirdEmployeeDTO.setThirdEmployeeName(userBean.getLastname());
        openThirdEmployeeDTO.setThirdEmployeePhone(userBean.getMobile());
        openThirdEmployeeDTO.setThirdEmployeeEmail(userBean.getEmail());
        openThirdEmployeeDTO.setStatus(1);
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
        OpenThirdEmployeeDTO targetDTO = null;
        if (employeeNeedFilter) {
            targetDTO = employeeBeforeSyncFilter(employeeConfig, userBean, openThirdEmployeeDTO);
        }
        employeeList.add(targetDTO == null ? openThirdEmployeeDTO : targetDTO);
    }

    /**
     * 分页获取分部和部门，将分部接入部门，组成部门树
     *
     * @param resturlConfig
     * @return 分部信息
     */
    public List<EcologyDepartmentInfo> getSubCompanyAndDepartmentInfoListPage(OpenEcologyResturlConfig resturlConfig) {
        // 取分部
        List<EcologySubCompanyInfo> hrmSubcompanyInfo = ecologyHrmService.getSubCompanyInfoListPage(resturlConfig);
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
        List<EcologyDepartmentInfo> departmentInfoList = ecologyHrmService.getDepartmentListPage(resturlConfig);
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
     * @return 员工信息
     */
    private OpenThirdEmployeeDTO employeeBeforeSyncFilter(OpenThirdScriptConfig employeeConfig, EcologyUserInfo userInfo, OpenThirdEmployeeDTO openThirdEmployeeDTO) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userInfo", userInfo);
            put("openThirdEmployeeDTO", openThirdEmployeeDTO);
        }};
        addParam(employeeConfig, params);
        return (OpenThirdEmployeeDTO) EtlUtils.execute(employeeConfig.getScript(), params);
    }

}
