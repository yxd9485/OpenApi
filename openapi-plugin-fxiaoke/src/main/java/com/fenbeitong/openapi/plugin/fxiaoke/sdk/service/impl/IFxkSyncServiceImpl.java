package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseCode;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.constant.FxkConstant;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.exception.OpenApiFxkException;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.util.FxkHttpUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeCorpAppDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.*;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeCorpApp;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkAccessTokenService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkSyncService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.util.PhoneCheckUtil;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>Title: IFxkSyncServiceImpl</p>
 * <p>Description: 数据同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-08-31 15:48
 */

@Slf4j
@ServiceAspect
@Service
public class IFxkSyncServiceImpl implements IFxkSyncService {

    @Autowired
    IFxkAccessTokenService iFxkAccessTokenService;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    FxiaokeCorpAppDao fxiaokeCorpAppDao;

    @Autowired
    OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    DepartmentUtilService departmentUtilService;

    @Value("${fenxiaoke.host}")
    private String fxiaokeHost;

    @Autowired
    private FxkHttpUtils fxkHttpUtils;

    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    FxkCommonApiService fxkCommonApiService;

    /**
     * 组织架构同步
     */
    @Override
    public String syncOrganization(FxiaokeOrgConfigDTO fxiaokeOrgConfigDTO) {
        if (ObjectUtils.isEmpty(fxiaokeOrgConfigDTO) || StringUtils.isBlank(fxiaokeOrgConfigDTO.getCompanyId()) || StringUtils.isBlank(fxiaokeOrgConfigDTO.getTopId())) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_JOB_CONFIG_ERROR);
        }
        log.info("[fxiaoke] syncOrganization, 开始同步组织机构人员,companyId={}", fxiaokeOrgConfigDTO.getCompanyId());
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, fxiaokeOrgConfigDTO.getCompanyId());
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getByCompanyId(fxiaokeOrgConfigDTO.getCompanyId());
                syncOrgEmployee(OpenType.FXIAOKE_EIA.getType(), pluginCorpDefinition.getThirdCorpId(), fxiaokeOrgConfigDTO);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【分销客】 syncOrganization, 未获取到锁，companyId={}", fxiaokeOrgConfigDTO.getCompanyId());
        }
        return "success";
    }

    /**
     * 组织同步
     */
    public void syncOrgEmployee(int openType, String corpId, FxiaokeOrgConfigDTO fxiaokeOrgConfigDTO) {
        String companyId = fxiaokeOrgConfigDTO.getCompanyId();
        // 获取配置信息
        Map<String, Object> map = Maps.newHashMap();
        map.put("corpId", corpId);
        FxiaokeCorpApp fxiaokeCorpApp = fxiaokeCorpAppDao.getFxiaokeCorpApp(map);
        // 获取token
        FxkGetCorpAccessTokenReqDTO fxkGetCorpAccessTokenReqDTO = new FxkGetCorpAccessTokenReqDTO();
        fxkGetCorpAccessTokenReqDTO.setAppId(fxiaokeCorpApp.getAppId());
        fxkGetCorpAccessTokenReqDTO.setAppSecret(fxiaokeCorpApp.getAppSecret());
        fxkGetCorpAccessTokenReqDTO.setPermanentCode(fxiaokeCorpApp.getPermanent());
        FxkGetCorpAccessTokenRespDTO fxkGetCorpAccessTokenRespDTO = iFxkAccessTokenService.getCorpAccessToken(fxkGetCorpAccessTokenReqDTO);
        OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.DEPARTMENT_SYNC);
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(departmentConfig)) {
            if (!StringUtils.isBlank(departmentConfig.getParamJson())) {
                List<Map<String, Object>> departmentListMap = new ArrayList<>();
                FxkGetCustomDataListReqDTO req = JsonUtils.toObj(String.format(departmentConfig.getParamJson(), fxkGetCorpAccessTokenRespDTO.getCorpAccessToken()), FxkGetCustomDataListReqDTO.class);
                fxkCommonApiService.getAllCustomData(req, departmentListMap);
                departmentBeforeSyncFilter(departmentConfig, departmentListMap, departmentList, companyId);
                departmentList = departmentUtilService.deparmentSort(departmentList, fxiaokeOrgConfigDTO.getTopId());
            }
        } else {
            // 获取纷享销客全量部门
            List<FxiaokeDepartmentRespDTO.DepartmentInfo> departmentInfos = getAllDepartments(fxkGetCorpAccessTokenRespDTO);
            // 转换部门
            for (FxiaokeDepartmentRespDTO.DepartmentInfo departmentInfo : departmentInfos) {
                OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
                openThirdOrgUnitDTO.setCompanyId(companyId);
                openThirdOrgUnitDTO.setThirdOrgUnitName(departmentInfo.getName());
                openThirdOrgUnitDTO.setThirdOrgUnitParentId(departmentInfo.getParentId());
                openThirdOrgUnitDTO.setThirdOrgUnitId(departmentInfo.getId());
                departmentList.add(openThirdOrgUnitDTO);
            }
            // 部门排序
            departmentList = departmentUtilService.deparmentSort(departmentList, FxkConstant.organization.ORGANIZATION_TOP_ID);
        }
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(employeeConfig)) {
            List<Map<String, Object>> employeListMap = new ArrayList<>();
            FxkGetCustomDataListReqDTO req = JsonUtils.toObj(String.format(employeeConfig.getParamJson(), fxkGetCorpAccessTokenRespDTO.getCorpAccessToken()), FxkGetCustomDataListReqDTO.class);
            fxkCommonApiService.getAllCustomData(req, employeListMap);
            employeeBeforeSyncFilter(employeeConfig, employeListMap, employeeList, companyId);
        } else {
            // 获取纷享销客全量人员
            List<FxiaokePersonnelRespDTO.PersonnelInfo> personnelInfos = getAllPersonnel(fxkGetCorpAccessTokenRespDTO);
            // 转换人员
            for (FxiaokePersonnelRespDTO.PersonnelInfo personnelInfo : personnelInfos) {
                OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                openThirdEmployeeDTO.setCompanyId(companyId);
                openThirdEmployeeDTO.setThirdDepartmentId(personnelInfo.getMainDepartmentId());
                if (StringUtils.isNotBlank(personnelInfo.getName())) {
                    openThirdEmployeeDTO.setThirdEmployeeName(personnelInfo.getName());
                } else {
                    openThirdEmployeeDTO.setThirdEmployeeName(personnelInfo.getNickName());
                }
                openThirdEmployeeDTO.setThirdEmployeeEmail(personnelInfo.getEmail());
                if (StringUtils.isNotBlank(personnelInfo.getMobile())) {
                    openThirdEmployeeDTO.setThirdEmployeePhone(PhoneCheckUtil.getMobileWithoutCountryCode(personnelInfo.getMobile()));
                }
                openThirdEmployeeDTO.setThirdEmployeeGender("M".equals(personnelInfo.getGender()) ? 1 : 2);
                openThirdEmployeeDTO.setThirdEmployeeId(personnelInfo.getOpenUserId());
                employeeList.add(openThirdEmployeeDTO);

            }
        }
        // 组织架同步同步
        openSyncThirdOrgService.syncThird(openType, companyId, departmentList, employeeList);
        // 部门主管同步
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            openSyncThirdOrgService.setAllDepManagePackV2(departmentList, companyId);
        }
    }


    /**
     * 获取全量部门信息
     */
    @Override
    public List<FxiaokeDepartmentRespDTO.DepartmentInfo> getAllDepartments(FxkGetCorpAccessTokenRespDTO fxkGetCorpAccessTokenRespDTO) {
        String data = RestHttpUtils.postJson(fxiaokeHost.concat("/cgi/department/list"), JsonUtils.toJson(fxkGetCorpAccessTokenRespDTO));
        FxiaokeDepartmentRespDTO fxiaokeDepartmentRespDTO = JsonUtils.toObj(data, FxiaokeDepartmentRespDTO.class);
        // 去除停用的部门并返回（true表示停用，false表示正常）
        return fxiaokeDepartmentRespDTO.getDepartments().stream().filter(t -> t.getIsStop() == false).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
    }

    /**
     * 获取全量人员信息
     */
    @Override
    public List<FxiaokePersonnelRespDTO.PersonnelInfo> getAllPersonnel(FxkGetCorpAccessTokenRespDTO fxkGetCorpAccessTokenRespDTO) {
        // 封装请求参数
        FxiaokePersonnelReqDTO fxiaokePersonnelReqDTO = new FxiaokePersonnelReqDTO();
        fxiaokePersonnelReqDTO.setPageNumber(1);
        fxiaokePersonnelReqDTO.setPageSize(1000);
        fxiaokePersonnelReqDTO.setCorpAccessToken(fxkGetCorpAccessTokenRespDTO.getCorpAccessToken());
        fxiaokePersonnelReqDTO.setCorpId(fxkGetCorpAccessTokenRespDTO.getCorpId());
        fxiaokePersonnelReqDTO.setShowDepartmentIdsDetail(true);
        List<FxiaokePersonnelRespDTO.PersonnelInfo> personnelInfoList = new ArrayList<>();
        getListThirdProjectRespDTO(fxiaokePersonnelReqDTO, personnelInfoList);
        // 去除离职的员工（true表示停用，false表示正常）
        return personnelInfoList.stream().filter(t -> t.getIsStop() == false).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
    }

    /**
     * 递归查询纷享销客的全量人员信息
     */
    public void getListThirdProjectRespDTO(FxiaokePersonnelReqDTO req, List<FxiaokePersonnelRespDTO.PersonnelInfo> data) {
        String respData = RestHttpUtils.postJson(fxiaokeHost.concat("/cgi/user/get/batchByUpdTime"), JsonUtils.toJson(req));
        FxiaokePersonnelRespDTO fxiaokePersonnelRespDTO = JsonUtils.toObj(respData, FxiaokePersonnelRespDTO.class);
        data.addAll(fxiaokePersonnelRespDTO.getEmployees());
        while ((fxiaokePersonnelRespDTO.getTotalCount() > req.getPageNumber() * req.getPageSize())) {
            req.setPageNumber(req.getPageNumber() + 1);
            getListThirdProjectRespDTO(req, data);
        }
    }

    @Override
    public FxiaokeGetUserInfoRespDTO getUserByOpenUserId(String openUserId, String corpId) {
        String url = fxiaokeHost.concat("/cgi/user/get");
        Map<String, Object> data = new HashMap<>(2);
        data.put("corpId", corpId);
        data.put("openUserId", openUserId);
        String res = fxkHttpUtils.postJsonWithAccessToken(url, data, corpId);
        FxiaokeGetUserInfoRespDTO fxiaokeGetUserInfoRespDTO = JsonUtils.toObj(res, FxiaokeGetUserInfoRespDTO.class);
        return fxiaokeGetUserInfoRespDTO;
    }


    @Override
    public FxiaokeGetByNickNameRespDTO getUserByNickName(String nickName, String corpId) {
        String url = fxiaokeHost.concat("/cgi/user/getByNickName");
        Map<String, Object> data = new HashMap<>(2);
        data.put("corpId", corpId);
        data.put("nickName", nickName);
        String res = fxkHttpUtils.postJsonWithAccessToken(url, data, corpId);
        FxiaokeGetByNickNameRespDTO fxiaokeGetByNickNameRespDTO = JsonUtils.toObj(res, FxiaokeGetByNickNameRespDTO.class);
        if (fxiaokeGetByNickNameRespDTO == null || fxiaokeGetByNickNameRespDTO.getErrorCode() != 0) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_EMPLOYEE_NOT_EXISTS);
        }
        return fxiaokeGetByNickNameRespDTO;
    }

    private void employeeBeforeSyncFilter(OpenThirdScriptConfig employeeConfig, List<Map<String, Object>> listMap, List<OpenThirdEmployeeDTO> employeeList, String companyId) {
        Map<String, Object> params = new HashMap<String, Object>(3) {{
            put("listMap", listMap);
            put("employeeList", employeeList);
            put("companyId", companyId);
        }};
        if (org.apache.commons.lang3.StringUtils.isNotBlank(employeeConfig.getParamJson()) && JsonUtils.toObj(employeeConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(employeeConfig.getParamJson(), Map.class));
        }
        EtlUtils.execute(employeeConfig.getScript(), params);
    }

    private void departmentBeforeSyncFilter(OpenThirdScriptConfig departmentConfig, List<Map<String, Object>> listMap, List<OpenThirdOrgUnitDTO> thirdOrgUnitDTOList, String companyId) {
        Map<String, Object> params = new HashMap<String, Object>(3) {{
            put("listMap", listMap);
            put("thirdOrgUnitDTOList", thirdOrgUnitDTOList);
            put("companyId", companyId);
        }};
        if (org.apache.commons.lang3.StringUtils.isNotBlank(departmentConfig.getParamJson()) && JsonUtils.toObj(departmentConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(departmentConfig.getParamJson(), Map.class));
        }
        EtlUtils.execute(departmentConfig.getScript(), params);
    }
}
