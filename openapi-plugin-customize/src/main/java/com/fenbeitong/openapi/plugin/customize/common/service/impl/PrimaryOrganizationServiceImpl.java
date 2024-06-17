package com.fenbeitong.openapi.plugin.customize.common.service.impl;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCustonmConstant;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenCustomizeConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenCustomizePageConfigDao;
import com.fenbeitong.openapi.plugin.customize.common.service.OrgListener;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenCustomizeConfig;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title: TalkOrganizationServiceImpl</p>
 * <p>Description: 组织架构同步 可配置化主类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:26
 */
@Slf4j
@ServiceAspect
@Service
public class PrimaryOrganizationServiceImpl extends PrimaryCommonImpl {

    @Autowired
    OpenCustomizeConfigDao openOrgConfigDao;

    @Autowired
    OpenCustomizePageConfigDao openOrgPageConfigDao;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    IEtlService etlService;

    @Autowired
    OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    DepartmentUtilService departmentUtilService;

    @Autowired
    AuthDefinitionDao authDefinitionDao;


    /**
     * 组织架构同步
     */
    public String allSync(String companyId, String topId) {
        log.info("[AbstractPrimaryOrganizationServiceImpl], 开始同步组织机构人员,companyId={}", companyId);
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                // 获取企业信息
                AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
                String companyName = authDefinition.getAppName();
                if (ObjectUtils.isEmpty(authDefinition)) {
                    log.info("可配置化 组织架构同步失败 open_auth_info 需要配置");
                    return "Failed";
                }
                syncOrgEmployee(OpenType.OPEN_API.getType(), companyId, companyName, topId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("syncOrganization, 未获取到锁，companyId={}", companyId);
        }
        return "success";
    }

    /**
     * 全量同步组织同步
     */
    public void syncOrgEmployee(int openType, String companyId, String companyName, String topId) {
        log.info("开始同步部门--------------start-----------------");
        // 同步部门 0全量 1增量
        syncDep(openType, companyId, companyName, topId, OpenCustonmConstant.open_customize_config_type.DEP_ALL, OpenCustonmConstant.open_customize_config_isAll.NOT_PAGE);
        log.info("同步部门结束-----------end-----------------");
        log.info("开始同步人员--------------start-----------------");
        // 同步人员 0全量 1增量
        syncEmp(openType, companyId, companyName, topId, OpenCustonmConstant.open_customize_config_type.EMP_ALL, OpenCustonmConstant.open_customize_config_isAll.NOT_PAGE);
        log.info("同步人员结束--------------end-----------------");
    }


    /**
     * 全量同步部门
     */
    public void syncDep(int openType, String companyId, String companyName, String topId, Integer type, Integer isAll) {
        // 获取部门配置
        OpenCustomizeConfig openOrgDepartmentConfig = getOpenCustomizeConfig(companyId, type, isAll);
        if (!ObjectUtils.isEmpty(openOrgDepartmentConfig)) {
            OrgListener orgListenerDep = getOrgLister(openOrgDepartmentConfig);
            // 获取全量部门
            List<OpenThirdOrgUnitDTO> getAllDepartments = getAllDepartments(companyId, openOrgDepartmentConfig, orgListenerDep);
            getAllDepartments.forEach(t -> t.setCompanyId(companyId));
            // 监听处理前置
            getAllDepartments = orgListenerDep.filterOpenThirdOrgUnitDtoBefore(getAllDepartments, companyId, topId, companyName);
            // 部门排序
            getAllDepartments = departmentUtilService.deparmentSort(getAllDepartments, companyId);
            // 监听处理后置
            getAllDepartments = orgListenerDep.filterOpenThirdOrgUnitDtoAfter(getAllDepartments);
            log.info("部门数据{}", JsonUtils.toJson(getAllDepartments));
            // 同步部门
            openSyncThirdOrgService.syncThird(openType, companyId, getAllDepartments, new ArrayList<>());
            // 更新中间表的数据
            orgListenerDep.updateDepManage(getAllDepartments, companyId, openType);

        }
    }


    /**
     * 全量同步人员
     */
    public void syncEmp(int openType, String companyId, String companyName, String topId, Integer type, Integer isAll) {
        // 获取人员配置
        OpenCustomizeConfig openOrgEmployeeConfig = getOpenCustomizeConfig(companyId, type, isAll);
        if (!ObjectUtils.isEmpty(openOrgEmployeeConfig)) {
            OrgListener orgListenerEmployee = getOrgLister(openOrgEmployeeConfig);
            // 获取全量人员
            List<OpenThirdEmployeeDTO> getAllPersonnel = getAllPersonnel(companyId, openOrgEmployeeConfig, orgListenerEmployee);
            // 监听处理数据
            getAllPersonnel = orgListenerEmployee.fileOpenThirdEmployeeDto(getAllPersonnel);
            // 给公司
            getAllPersonnel.forEach(t -> t.setCompanyId(companyId));
            log.info("人员数据{}", JsonUtils.toJson(getAllPersonnel));
            // 同步人员
            openSyncThirdOrgService.syncThird(openType, companyId, new ArrayList<>(), getAllPersonnel);
            // 设置部门负责人监听
            orgListenerEmployee.setDepManage(companyId, openType);
        }
    }


    /**
     * 增量同步部门
     */
    public List<OpenThirdOrgUnitDTO> syncDepPortion(String companyId) {
        // 获取部门配置
        OpenCustomizeConfig openOrgDepartmentConfig = getOpenCustomizeConfig(companyId, OpenCustonmConstant.open_customize_config_type.DEP_PROTION, OpenCustonmConstant.open_customize_config_isAll.NOT_PAGE);
        if (!ObjectUtils.isEmpty(openOrgDepartmentConfig)) {
            OrgListener orgListenerDep = getOrgLister(openOrgDepartmentConfig);
            // 获取增量部门
            List<OpenThirdOrgUnitDTO> getAllDepartments = getAllDepartments(companyId, openOrgDepartmentConfig, orgListenerDep);
            getAllDepartments.forEach(t -> t.setCompanyId(companyId));
            return getAllDepartments;
        }
        return null;
    }


    /**
     * 增量同步人员
     */

    public List<OpenThirdEmployeeDTO> syncEmpPortion(String companyId) {
        // 获取人员配置
        OpenCustomizeConfig openOrgEmployeeConfig = getOpenCustomizeConfig(companyId, OpenCustonmConstant.open_customize_config_type.EMP_PARTION, OpenCustonmConstant.open_customize_config_isAll.NOT_PAGE);
        if (!ObjectUtils.isEmpty(openOrgEmployeeConfig)) {
            OrgListener orgListenerEmployee = getOrgLister(openOrgEmployeeConfig);
            // 获取全量人员
            List<OpenThirdEmployeeDTO> getAllPersonnel = getAllPersonnel(companyId, openOrgEmployeeConfig, orgListenerEmployee);
            // 给公司
            getAllPersonnel.forEach(t -> t.setCompanyId(companyId));
            return getAllPersonnel;
        }
        return null;
    }
}
