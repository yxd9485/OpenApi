package com.fenbeitong.openapi.plugin.landray.ekp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.landray.ekp.dao.OpenLandrayEkpConfigDao;
import com.fenbeitong.openapi.plugin.landray.ekp.dto.LandaryEkpDepartmentInfoDTO;
import com.fenbeitong.openapi.plugin.landray.ekp.dto.LandaryEkpEmployeeDTO;
import com.fenbeitong.openapi.plugin.landray.ekp.entity.OpenLandrayEkpConfig;
import com.fenbeitong.openapi.plugin.landray.ekp.service.ILandaryEkpSyncOrgEmployeeService;
import com.fenbeitong.openapi.plugin.landray.ekp.service.ILandrayEkpEmployeeService;
import com.fenbeitong.openapi.plugin.landray.ekp.service.ILandrayEkpOrganizationService;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhen on 2021/1/27.
 */
@ServiceAspect
@Service
@Slf4j
public class LandaryEkpSyncOrgEmployeeServiceImpl implements ILandaryEkpSyncOrgEmployeeService {

    @Autowired
    private ILandrayEkpOrganizationService landrayEkpOrganizationService;

    @Autowired
    private OpenLandrayEkpConfigDao openLandrayEkpConfigDao;

    @Autowired
    private ILandrayEkpEmployeeService landrayEkpEmployeeService;


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    DepartmentUtilService departmentUtilService;

    /**
     * 蓝凌同步部门人员
     * 1.拉取蓝凌部门人员数据并排序有效部门
     * 2.获取分贝通部门数据并排序部门，用于按倒序删除部门
     * 3.将无效人员删除
     * 4.将有效人员及有效部门排序后添加
     * 5.将无效部门倒序删除
     *
     * @param companyId
     */
    @Async
    @Override
    public void syncThirdOrgEmployee(String companyId) {
        doSync(companyId, false, null);
    }

    @Async
    @Override
    public void syncThirdOrgEmployeeV2(String companyId, String rootId) {
        doSync(companyId, true, rootId);
    }

    @Override
    public List<LandaryEkpDepartmentInfoDTO> queryLandaryEkpDepartmentInfo(String companyId) {
        OpenLandrayEkpConfig openLandrayEkpConfig = openLandrayEkpConfigDao.getOpenLandrayEkpConfigByCompanyId(companyId);
        if (openLandrayEkpConfig == null) {
            log.info("企业不存在, companyId={}", companyId);
            return null;
        }
        List<LandaryEkpDepartmentInfoDTO> allDepartment = landrayEkpOrganizationService.getAllDepartment(openLandrayEkpConfig, null);
        //有效部门
        List<LandaryEkpDepartmentInfoDTO> availableDepartment = landrayEkpOrganizationService.getAvailableDepartment(allDepartment, companyId);
        return availableDepartment;
    }

    /**
     * @param companyId 企业ID
     * @param isVersion 是否新版本  false老版本 true新版本
     * @param rootId    根部门ID
     * @Description 执行同步
     * @Author duhui
     * @Date 2022/3/29
     **/
    private void doSync(String companyId, Boolean isVersion, String rootId) {
        OpenLandrayEkpConfig openLandrayEkpConfig = openLandrayEkpConfigDao.getOpenLandrayEkpConfigByCompanyId(companyId);
        if (openLandrayEkpConfig == null) {
            log.info("企业不存在, companyId={}", companyId);
            return;
        }
        String beginTime = null;
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                //拉全量人员
                List<LandaryEkpEmployeeDTO> allEmployee = landrayEkpEmployeeService.getAllEmployee(openLandrayEkpConfig, beginTime);
                //有效人员
                List<LandaryEkpEmployeeDTO> availableEmployee = landrayEkpEmployeeService.getAvailableEmployee(allEmployee);
                //拉全量部门
                List<LandaryEkpDepartmentInfoDTO> allDepartment = landrayEkpOrganizationService.getAllDepartment(openLandrayEkpConfig, beginTime);
                //有效部门
                List<LandaryEkpDepartmentInfoDTO> availableDepartment = landrayEkpOrganizationService.getAvailableDepartment(allDepartment, companyId);
                // 获取标准部门DTO
                List<OpenThirdOrgUnitDTO> openThirdOrgUnitTreeList = new ArrayList<>();
                if (isVersion) {
                    openThirdOrgUnitTreeList = packageOrgUnitListV2(companyId, availableDepartment, rootId);
                } else {
                    openThirdOrgUnitTreeList = packageOrgUnitList(companyId, availableDepartment);
                }
                // 获取标准人员DTO
                List<OpenThirdEmployeeDTO> employeeList = packageEmployeeList(companyId, availableEmployee);
                // 过滤有效人员
                employeeList = departmentUtilService.getValidEmployee(employeeList, openThirdOrgUnitTreeList);
                // 同步人员部门
                openSyncThirdOrgService.syncThird(OpenType.OPEN_API.getType(), companyId, openThirdOrgUnitTreeList, employeeList);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("landary ekp syncThirdOrgEmployee, 未获取到锁，companyId={}", companyId);
        }
    }

    private List<OpenThirdOrgUnitDTO> packageOrgUnitList(String companyId, List<LandaryEkpDepartmentInfoDTO> availableDepartment) {
        log.info("蓝凌有效部门{},{}", companyId, JsonUtils.toJson(ObjectUtils.isEmpty(availableDepartment) ? null : JsonUtils.toJson(availableDepartment)));
        AuthDefinition authInfo = authDefinitionDao.getAuthInfoByAppId(companyId);
        OpenThirdOrgUnitDTO rootDept = new OpenThirdOrgUnitDTO();
        rootDept.setThirdOrgUnitId(companyId);
        rootDept.setThirdOrgUnitName(authInfo.getAppName());
        rootDept.setThirdOrgUnitFullName(authInfo.getAppName());
        rootDept.setThirdOrgUnitParentId("-" + companyId);
        //转换有效部门
        List<OpenThirdOrgUnitDTO> orgUnitList = Lists.newArrayList();
        orgUnitList.add(rootDept);
        for (LandaryEkpDepartmentInfoDTO landaryEkpDepartmentInfoDTO : availableDepartment) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            openThirdOrgUnitDTO.setThirdOrgUnitId(landaryEkpDepartmentInfoDTO.getId());
            String departmentName = landaryEkpDepartmentInfoDTO.getName();
            if (!StringUtils.isBlank(departmentName) && departmentName.contains("/")) {
                departmentName = departmentName.replace("/", "-");
            }
            openThirdOrgUnitDTO.setThirdOrgUnitName(departmentName);
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(landaryEkpDepartmentInfoDTO.getParent());
            if (openThirdOrgUnitDTO.getThirdOrgUnitId().equals(companyId)) {
                openThirdOrgUnitDTO.setThirdOrgUnitParentId(null);
            }
            orgUnitList.add(openThirdOrgUnitDTO);
        }
        // 当根部门的父级部门不为空时，脚本处理替换成公司ID
        OpenThirdScriptConfig OrgConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.DEPARTMENT_SYNC);
        if (!ObjectUtils.isEmpty(OrgConfig)) {
            orgUnitList = orgBeforeSyncFilter(OrgConfig, orgUnitList, companyId);
        }
        //有效部门排序
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitTreeList = departmentUtilService.deparmentSort(orgUnitList, rootDept.getThirdOrgUnitParentId());
        rootDept.setThirdOrgUnitParentId(null);
        return openThirdOrgUnitTreeList;
    }


    private List<OpenThirdOrgUnitDTO> packageOrgUnitListV2(String companyId, List<LandaryEkpDepartmentInfoDTO> availableDepartment, String rootId) {
        log.info("蓝凌有效部门{},{}", companyId, JsonUtils.toJson(ObjectUtils.isEmpty(availableDepartment) ? null : JsonUtils.toJson(availableDepartment)));
        AuthDefinition authInfo = authDefinitionDao.getAuthInfoByAppId(companyId);
        //转换有效部门
        List<OpenThirdOrgUnitDTO> orgUnitList = Lists.newArrayList();
        for (LandaryEkpDepartmentInfoDTO landaryEkpDepartmentInfoDTO : availableDepartment) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            openThirdOrgUnitDTO.setThirdOrgUnitId(landaryEkpDepartmentInfoDTO.getId());
            String departmentName = landaryEkpDepartmentInfoDTO.getName();
            if (!StringUtils.isBlank(departmentName) && departmentName.contains("/")) {
                departmentName = departmentName.replace("/", "-");
            }
            openThirdOrgUnitDTO.setThirdOrgUnitName(departmentName);
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(landaryEkpDepartmentInfoDTO.getParent());
            orgUnitList.add(openThirdOrgUnitDTO);
        }
        // 脚本处理
        OpenThirdScriptConfig OrgConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.DEPARTMENT_SYNC);
        if (!ObjectUtils.isEmpty(OrgConfig)) {
            orgUnitList = orgBeforeSyncFilter(OrgConfig, orgUnitList, companyId);
        }
        //有效部门排
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitTreeList = new ArrayList<>();
        if (StringUtils.isBlank(rootId)) {
            openThirdOrgUnitTreeList = departmentUtilService.deparmentSortAuto(orgUnitList, authInfo.getAppName(), 0, "-" + companyId, false);
        } else {
            openThirdOrgUnitTreeList = departmentUtilService.deparmentSort(orgUnitList, rootId, authInfo.getAppName());

        }
        return openThirdOrgUnitTreeList;
    }

    private List<OpenThirdEmployeeDTO> packageEmployeeList(String companyId, List<LandaryEkpEmployeeDTO> availableEmployee) {
        List<OpenThirdEmployeeDTO> employeeList = Lists.newArrayList();
        //有效人员
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
        if (!ObjectUtils.isEmpty(employeeConfig)) {
            employeeBeforeSyncFilter(employeeConfig, availableEmployee, employeeList, companyId);
        } else {
            for (LandaryEkpEmployeeDTO landaryEkpEmployeeDTO : availableEmployee) {
                OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                openThirdEmployeeDTO.setThirdEmployeeId(landaryEkpEmployeeDTO.getId());
                openThirdEmployeeDTO.setThirdEmployeeName(landaryEkpEmployeeDTO.getName());
                openThirdEmployeeDTO.setThirdEmployeeGender("M".equals(landaryEkpEmployeeDTO.getSex()) ? 1 : 2);
                openThirdEmployeeDTO.setThirdEmployeePhone(landaryEkpEmployeeDTO.getMobileNo());
                openThirdEmployeeDTO.setThirdEmployeeEmail(landaryEkpEmployeeDTO.getEmail());
                openThirdEmployeeDTO.setThirdDepartmentId(landaryEkpEmployeeDTO.getParent());
                if (StringUtils.isBlank(landaryEkpEmployeeDTO.getParent())) {
                    openThirdEmployeeDTO.setThirdDepartmentId(companyId);
                }
                employeeList.add(openThirdEmployeeDTO);
            }
        }
        return employeeList;
    }

    private List<OpenThirdEmployeeDTO> employeeBeforeSyncFilter(OpenThirdScriptConfig employeeConfig, List<LandaryEkpEmployeeDTO> availableEmployee, List<OpenThirdEmployeeDTO> employeeList, String companyId) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("availableEmployee", availableEmployee);
            put("employeeList", employeeList);
            put("companyId", companyId);
        }};
        if (org.apache.commons.lang3.StringUtils.isNotBlank(employeeConfig.getParamJson()) && JsonUtils.toObj(employeeConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(employeeConfig.getParamJson(), Map.class));
        }
        return (List<OpenThirdEmployeeDTO>) EtlUtils.execute(employeeConfig.getScript(), params);
    }

    private List<OpenThirdOrgUnitDTO> orgBeforeSyncFilter(OpenThirdScriptConfig orgConfig, List<OpenThirdOrgUnitDTO> orgUnitList, String companyId) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("orgStr", JsonUtils.toJson(orgUnitList));
            put("companyId", companyId);
        }};
        if (org.apache.commons.lang3.StringUtils.isNotBlank(orgConfig.getParamJson()) && JsonUtils.toObj(orgConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(orgConfig.getParamJson(), Map.class));
        }
        String str = (String) EtlUtils.execute(orgConfig.getScript(), params);
        return JsonUtils.toObj(str, new TypeReference<List<OpenThirdOrgUnitDTO>>() {
        });
    }

}
