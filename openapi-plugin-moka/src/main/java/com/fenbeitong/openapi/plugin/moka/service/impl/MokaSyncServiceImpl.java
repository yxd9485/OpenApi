package com.fenbeitong.openapi.plugin.moka.service.impl;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.moka.dto.DepartmentRespDto;
import com.fenbeitong.openapi.plugin.moka.dto.EmployeeRespDto;
import com.fenbeitong.openapi.plugin.moka.dto.JobConfigDto;
import com.fenbeitong.openapi.plugin.moka.dto.MokaSysConfigDto;
import com.fenbeitong.openapi.plugin.moka.service.MokaSyncService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.util.PhoneCheckUtil;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.stream.Collectors;

/**
 * <p>Title: MokaSyncServiceImpl</p>
 * <p>Description: Moka组织架构数据同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-08-31 15:48
 */

@Slf4j
@ServiceAspect
@Service
public class MokaSyncServiceImpl extends MokaServiceApi implements MokaSyncService {

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    DepartmentUtilService departmentUtilService;

    @Autowired
    AuthDefinitionDao authDefinitionDao;

    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;


    /**
     * 同步
     */
    @Override
    @Async
    public String syncOrganization(JobConfigDto jobConfigDto) {

        log.info("[moka] syncOrganization, 开始同步组织机构人员,companyId={}", jobConfigDto.getCompanyId());
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, jobConfigDto.getCompanyId());
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                syncOrgEmployee(OpenType.MOKA_EIA.getType(), jobConfigDto.getTopId(), jobConfigDto.getCompanyId(), jobConfigDto.isSyncDepManager(), jobConfigDto.getType());
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("未获取到锁，companyId={}", jobConfigDto.getCompanyId());
            throw new ArgumentException("未获取到锁");
        }
        return "success";
    }


    /**
     * 组织同步
     */
    public String syncOrgEmployee(int openType, String topId, String companyId, boolean syncDepManager, String type) {
        // 获取企业信息
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
        if (ObjectUtils.isEmpty(authDefinition)) {
            log.info("Moka 组织架构同步失败 open_auth_info 需要配置");
            return "Failed";
        }
        // 获取配置信息
        HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put("code", companyId);
        configMap.put("type", OpenSysConfigType.MOKA_ORGANIZATION_SYS_CONFIG.getType());
        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfig(configMap);
        log.info("syncOrgEmployee 获取配置文件信息openSysConfig:{}", openSysConfig);
        MokaSysConfigDto mokaSysConfigDto;
        if (ObjectUtils.isEmpty(openSysConfig)) {
            log.info("获取配置 openSysConfig 文件信息失败");
            return "Failed";
        } else {
            mokaSysConfigDto = JsonUtils.toObj(openSysConfig.getValue(), MokaSysConfigDto.class);
        }
        // 获取moka全量部门
        List<DepartmentRespDto.DataBean.ListBean> departmentInfos = getAllDepartment(mokaSysConfigDto,companyId).getData().getList();
        // 部门过滤有效部门
        departmentInfos = departmentInfos.stream().filter(t -> "1".equals(t.getHaveUsed())).collect(Collectors.toList());
        log.info("moka全量部门:{}", JsonUtils.toJson(departmentInfos));
        // 获取moka全量人员
        List<EmployeeRespDto.DataBean.ListBean> personnelInfos = getAllEmployee(mokaSysConfigDto,companyId).getData().getList();
        // 人员过滤有效人员
        personnelInfos = personnelInfos.stream().filter(t -> "1".equals(t.getEmployeeStatusId())).collect(Collectors.toList());
        log.info("moka全量人员:{}", JsonUtils.toJson(personnelInfos));
        List<OpenThirdOrgUnitManagers> openThirdOrgUnitManagersList = new ArrayList<>();
        // 转换部门
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        for (DepartmentRespDto.DataBean.ListBean departmentInfo : departmentInfos) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            if (!com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(departmentInfo.getDirector())) {
                openThirdOrgUnitManagersList.add(OpenThirdOrgUnitManagers.builder()
                        .id(RandomUtils.bsonId())
                        .companyId(companyId)
                        .thirdEmployeeIds(com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(departmentInfo.getDirectorId()))
                        .thirdOrgUnitId(departmentInfo.getNodeUid())
                        .status(0)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build());
            }
            openThirdOrgUnitDTO.setCompanyId(companyId);
            // 部门名称
            openThirdOrgUnitDTO.setThirdOrgUnitName(departmentInfo.getDeptSampleName());
            // 上级部门
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(JsonUtils.toObj(departmentInfo.getSuperiorDept(), DepartmentRespDto.SuperiorDept.class).getId());
            // 部门ID
            openThirdOrgUnitDTO.setThirdOrgUnitId(departmentInfo.getNodeUid());
            departmentList.add(openThirdOrgUnitDTO);
        }
        if ("1".equals(type)) {
            // 部门排序
            departmentList = departmentUtilService.deparmentSort(departmentList, topId);
        } else {
            // 绑定根部门名称
            departmentList.forEach(t -> {
                if (topId.equals(t.getThirdOrgUnitParentId())) {
                    t.setThirdOrgUnitName(authDefinition.getAppName());
                }
            });
            // 去除根部门
            departmentList.remove(0);
        }
        // 转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
        if (!ObjectUtils.isEmpty(employeeConfig)) {
            employeeBeforeSyncFilter(employeeConfig, personnelInfos, employeeList, companyId);
        } else {
            for (EmployeeRespDto.DataBean.ListBean personnelInfo : personnelInfos) {
                OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                // 企业ID
                openThirdEmployeeDTO.setCompanyId(companyId);
                // 部门ID
                openThirdEmployeeDTO.setThirdDepartmentId(personnelInfo.getDepartmentId());
                // 人员姓名
                openThirdEmployeeDTO.setThirdEmployeeName(personnelInfo.getRealname());
                // 手机号
                if (StringUtils.isNotBlank(personnelInfo.getTelephone())) {
                    openThirdEmployeeDTO.setThirdEmployeePhone(PhoneCheckUtil.getMobileWithoutCountryCode(personnelInfo.getTelephone()));
                }
                // 唯一ID
                openThirdEmployeeDTO.setThirdEmployeeId(personnelInfo.getUuid());
                // 1 代表身份证
                if ("1".equals(personnelInfo.getIdType())) {
                    openThirdEmployeeDTO.setThirdEmployeeIdCard(personnelInfo.getIdNo());
                }
                employeeList.add(openThirdEmployeeDTO);
            }
        }
        // 同步
        openSyncThirdOrgService.syncThird(openType, companyId, departmentList, employeeList);
        // 查询配置判断该企业是否要同步部门主管
        if (syncDepManager) {
            // 同步部门主管
            openSyncThirdOrgService.setAllDepManageV2(openThirdOrgUnitManagersList, companyId);
        }
        return "success";
    }


    private List<OpenThirdEmployeeDTO> employeeBeforeSyncFilter(OpenThirdScriptConfig employeeConfig, List<EmployeeRespDto.DataBean.ListBean> personnelInfos, List<OpenThirdEmployeeDTO> employeeList, String companyId) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("personnelInfos", personnelInfos);
            put("employeeList", employeeList);
            put("companyId", companyId);
        }};
        if (org.apache.commons.lang3.StringUtils.isNotBlank(employeeConfig.getParamJson()) && JsonUtils.toObj(employeeConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(employeeConfig.getParamJson(), Map.class));
        }
        return (List<OpenThirdEmployeeDTO>) EtlUtils.execute(employeeConfig.getScript(), params);
    }


}
