package com.fenbeitong.openapi.plugin.qiqi.service.department.impl;

import com.fenbeitong.common.utils.basis.StringUtil;
import com.fenbeitong.common.utils.date.DateUtils;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.qiqi.constant.*;
import com.fenbeitong.openapi.plugin.qiqi.dto.*;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.service.AbstractQiqiCommonService;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.qiqi.service.department.IQiqiDepartmentService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.employee.dto.CertDTO;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdExpandFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdExpandFieldConfig;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.util.PhoneCheckUtil;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.finhub.framework.exception.ArgumentException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName QiqiDepartmentServiceImpl
 * @Description 企企同步部门数据
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/5/13 下午2:11
 **/
@Service
@Slf4j
public class QiqiDepartmentServiceImpl extends AbstractQiqiCommonService implements IQiqiDepartmentService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;
    @Autowired
    private QiqiCommonReqServiceImpl qiqiCommonReqService;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private OpenSysConfigDao openSysConfigDao;
    @Autowired
    private OpenThirdExpandFieldConfigDao expandFieldConfigDao;
    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;
    @Autowired
    private DepartmentUtilService departmentUtilService;


    @Override
    @Async
    public void syncQiqiOrgEmployee(String companyId) throws Exception {
        log.info("【qiqi】 syncQiqiOrgEmployee, 开始同步组织机构人员,companyId={}", companyId);
        QiqiCorpInfo corpInfo = getCorpInfo(companyId);
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                String corpId = corpInfo.getOpenId();
                syncOrgEmployee(OpenType.QIQI.getType(), corpId, companyId, corpInfo.getCompanyName());
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【qiqi eia】 syncFeiShuEiaOrgEmployee, 未获取到锁，companyId={}", companyId);
            throw new ArgumentException("未获取到锁");
        }
    }


    /**
     * 全量同步部门人员
     *
     * @param corpId
     */
    public void syncOrgEmployee(int openType, String corpId, String companyId, String companyName) throws Exception {
        //获取当前时间戳
        String currentStr = StringUtil.obj2str(System.currentTimeMillis());
        Long currentTime = Long.valueOf(currentStr.substring(0, currentStr.length() - 3));
        //全量拉取部门数据
        List<QiqiDepartmentReqDTO> departmentInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.DEPARTMENT.getCode(), QiqiDepartmentReqDTO.class, "id is not null  and (disabledTime is null or disabledTime>to_timestamp(" + currentTime + "))", null);
        //全量拉取人员数据
        List<QiqiEmployeeReqDTO> userInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.USER.getCode(), QiqiEmployeeReqDTO.class, "id is not null and (disabledTime is null or disabledTime>to_timestamp(" + currentTime + ")) and (systemDisabledTime is  null or systemDisabledTime>to_timestamp(" + currentTime + ")) and statusId='UserStatus.incumbent' and name !='小企'", getTreeParam());
        if (CollectionUtils.isBlank(departmentInfos)) {
            log.info("【qiqi】 syncOrgEmployee, 查询三方部门数据为空");
            return;
        }
        //转换部门
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOS = departmentConvert(departmentInfos, companyId, companyName);
        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = employeeConvert(userInfos, corpId, companyId);
        //同步
        openSyncThirdOrgService.syncThird(openType, companyId, openThirdOrgUnitDTOS, employeeList);
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        // 判断设置部门主管配置时否为空
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            openSyncThirdOrgService.setAllDepManagePackV2(openThirdOrgUnitDTOS, companyId);
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
    public OpenThirdOrgUnitDTO departmentBeforeSyncFilter(OpenThirdScriptConfig departmentConfig, QiqiDepartmentReqDTO departmentInfo, OpenThirdOrgUnitDTO openThirdOrgUnitDTO) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("departmentInfo", departmentInfo);
            put("openThirdOrgUnitDTO", openThirdOrgUnitDTO);
        }};
        if (StringUtils.isNotBlank(departmentConfig.getParamJson()) && JsonUtils.toObj(departmentConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(departmentConfig.getParamJson(), Map.class));
        }
        return (OpenThirdOrgUnitDTO) EtlUtils.execute(departmentConfig.getScript(), params);
    }

    /**
     * 人员同步前按需过滤
     *
     * @param employeeConfig
     * @param userInfo
     * @param openThirdEmployeeDTO
     * @return
     */
    public OpenThirdEmployeeDTO employeeBeforeSyncFilter(OpenThirdScriptConfig employeeConfig, QiqiEmployeeReqDTO userInfo, OpenThirdEmployeeDTO openThirdEmployeeDTO) {

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userInfo", userInfo);
            put("openThirdEmployeeDTO", openThirdEmployeeDTO);
        }};
        if (StringUtils.isNotBlank(employeeConfig.getParamJson()) && JsonUtils.toObj(employeeConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(employeeConfig.getParamJson(), Map.class));
        }
        return (OpenThirdEmployeeDTO) EtlUtils.execute(employeeConfig.getScript(), params);
    }

    @Override
    public List<OpenThirdOrgUnitDTO> departmentConvert(List<QiqiDepartmentReqDTO> departmentInfos, String companyId, String companyName) {

        // 先查一下配置表 看是否需要过滤
        OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getDepartmentConfig(companyId);
        boolean departmentNeedFilter = departmentConfig != null;
        String rootId = null;
        //转换部门
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        //多个一级部门
        List<OpenThirdOrgUnitDTO> firstDept = new ArrayList<>();
        for (QiqiDepartmentReqDTO departmentInfo : departmentInfos) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            openThirdOrgUnitDTO.setCompanyId(companyId);
            openThirdOrgUnitDTO.setThirdOrgUnitName(departmentInfo.getName());
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(departmentInfo.getParentId());
            openThirdOrgUnitDTO.setThirdOrgUnitId(departmentInfo.getId());
            // 增加部门主管ID
            openThirdOrgUnitDTO.setOrgUnitMasterIds(departmentInfo.getManagerId());

            OpenThirdOrgUnitDTO targetDTO = null;
            if (departmentNeedFilter) {
                targetDTO = departmentBeforeSyncFilter(departmentConfig, departmentInfo, openThirdOrgUnitDTO);
            }
            if (StringUtils.isEmpty(departmentInfo.getParentId())) {
                //可能存在多个根部门，根部门变为一级部门
                firstDept.add(openThirdOrgUnitDTO);
            } else {
                departmentList.add(targetDTO != null ? targetDTO : openThirdOrgUnitDTO);
            }
        }
        //判断是否是多个根部门，如果是多个则变为一级部门，如果是单个则为根部门
        if (CollectionUtils.isNotBlank(firstDept)) {
            if (firstDept.size() > 1) {
                firstDept.stream().forEach(f -> {
                    f.setThirdOrgUnitParentId(QiqiSyncConstant.ROOT_ID);
                });
                rootId = QiqiSyncConstant.ROOT_ID;
            } else {
                rootId = firstDept.get(0).getThirdOrgUnitId();
            }
            departmentList.addAll(firstDept);
        }

        //部门排序
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOS = Lists.newArrayList();
        if (StringUtils.isNotBlank(rootId)) {
            log.info("【qiqi】 departmentConvert,排序前部门集合：{}", JsonUtils.toJson(departmentList));
            openThirdOrgUnitDTOS = departmentUtilService.deparmentSortAuto(departmentList, companyName, 0, rootId, false);
            log.info("【qiqi】 departmentConvert,排序后部门集合：{}", JsonUtils.toJson(openThirdOrgUnitDTOS));
        }
        return CollectionUtils.isNotBlank(openThirdOrgUnitDTOS) ? openThirdOrgUnitDTOS : departmentList;
    }

    @Override
    public List<OpenThirdEmployeeDTO> employeeConvert(List<QiqiEmployeeReqDTO> userInfos, String corpId, String companyId) {
        //获取企企配置的分贝通权限自定义字段,数据初始化之前需配置表数据
        List<OpenMsgSetup> feishuFbtPriv = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("company_qiqi_fbt_priv"));
        String fbtPrivIdKey = "";
        if (!ObjectUtils.isEmpty(feishuFbtPriv)) {
            OpenMsgSetup openMsgSetup = feishuFbtPriv.get(0);
            Integer intVal1 = openMsgSetup.getIntVal1();
            if (intVal1 == 1) {
                fbtPrivIdKey = openMsgSetup.getStrVal1();
            }
        }
        // 先查一下配置表 看是否需要过滤
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
        boolean employeeNeedFilter = employeeConfig != null;

        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        for (QiqiEmployeeReqDTO userInfo : userInfos) {
            OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
            openThirdEmployeeDTO.setCompanyId(companyId);
            openThirdEmployeeDTO.setThirdDepartmentId(userInfo.getDepartmentId());
            openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
            openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
            openThirdEmployeeDTO.setThirdEmployeePhone(PhoneCheckUtil.getMobileWithoutCountryCode(userInfo.getMobile()));
            if (!org.apache.commons.lang3.StringUtils.isEmpty(userInfo.getGenderId())) {
                GenderEnum enumByType = GenderEnum.getEnumByType(userInfo.getGenderId());
                openThirdEmployeeDTO.setThirdEmployeeGender(enumByType.getType());
            }
            //更新标识
            openThirdEmployeeDTO.setUpdateFlag(true);
            //身份证号，不是身份证和护照的则不同步
            if (!StringUtils.isEmpty(userInfo.getIdNumber()) && !StringUtils.isEmpty(userInfo.getIdTypeId()) && (userInfo.getIdTypeId().equals(CertTypeEnum.IDCARD.getDesc()) || userInfo.getIdTypeId().equals(CertTypeEnum.PASSPORT.getDesc()))) {
                List<CertDTO> certDTOList = new ArrayList<>();
                CertDTO certDTO = new CertDTO();
                certDTO.setCertType(CertTypeEnum.getEnumByType(userInfo.getIdTypeId()).getType());
                certDTO.setCertNo(userInfo.getIdNumber());
                certDTOList.add(certDTO);
                openThirdEmployeeDTO.setCerts(certDTOList);
            }
            //生日
            if (!ObjectUtils.isEmpty(userInfo.getBirthdate()) && userInfo.getBirthdate() != 0) {
                openThirdEmployeeDTO.setThirdEmployeeBirthday(DateUtils.parseLongToInteger(userInfo.getBirthdate()));
            }

            //工号
            openThirdEmployeeDTO.setEmployeeNumber(userInfo.getCode());
            openThirdEmployeeDTO.setThirdEmployeeRankId(userInfo.getRankId());
            if ("0".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
                openThirdEmployeeDTO.setThirdDepartmentId(corpId);
            }
            openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getId());
            //不为空，进行分贝通权限字段设置
            if (org.apache.commons.lang3.StringUtils.isNotBlank(fbtPrivIdKey)) {
                Map<String, Object> customAttrs = userInfo.getCustomAttrs();
                //如果人员没有配置分贝通权限，则拉取的详情数据中不会包含自定义字段内容
                if (!ObjectUtils.isEmpty(customAttrs)) {
                    Map fbtPrivMap = (Map) customAttrs.get(fbtPrivIdKey);
                    if (!ObjectUtils.isEmpty(fbtPrivMap)) {
                        String fbtPriv = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(fbtPrivMap.get("value"));
                        //如果没有填写分贝通权限字段，则默认不进行权限同步，全部关闭
                        if (org.apache.commons.lang3.StringUtils.isBlank(fbtPriv)) {
                            int roleType = NumericUtils.obj2int(fbtPriv, -1);
                            if (roleType != -1) {
                                openThirdEmployeeDTO.setThirdEmployeeRoleTye(com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(roleType));
                            }
                        }
                    }
                }
            }
            //人员扩展字段
            Map<String, Object> customAttrs = userInfo.getCustomAttrs();
            if (!ObjectUtils.isEmpty(customAttrs)) {
                //扩展字段配置
                OpenThirdExpandFieldConfig expandFieldConfig = expandFieldConfigDao.getByCompanyId(companyId);
                String userExpandFields = expandFieldConfig == null ? null : expandFieldConfig.getUserExpandFields();
                Map map = JsonUtils.toObj(userExpandFields, Map.class);
                if (!ObjectUtils.isEmpty(map)) {
                    Map expandJson = Maps.newHashMap();
                    map.forEach((k, v) -> {
                        // 身份证号不加到自定义字段里
                        // 自定义字段里有身份证号,则设置到分贝通
                        if (QiqiSyncConstant.ID_NO.equals(v) && (customAttrs.get(k) != null)) {
                            Map valueInfo = customAttrs.get(k) == null ? null : (Map) customAttrs.get(k);
                            String value = valueInfo == null ? "" : (String) valueInfo.get("value");
                            openThirdEmployeeDTO.setThirdEmployeeIdCard(value);
                        } else {
                            String key = k + ":value";
                            String value = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(MapUtils.getValueByExpress(customAttrs, key));
                            if (!com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(value)) {
                                expandJson.put(v, value);
                            }
                        }
                    });
                    openThirdEmployeeDTO.setExtAttr(expandJson);
                }
            }

            //人员状态 如果人员状态为离职或者业务停用时间、系统停用时间小于等于当前日期的零点，状态为停用
            Integer status = EmployeeStatusEnum.INCUMBENT.getCode();
            if ("UserStatus.resignation".equals(userInfo.getStatusId())) {
                status = 2;
            } else {
                //计算当日零点时间戳
                Long currentTimestamps = System.currentTimeMillis();
                Long oneDayTimestamps = Long.valueOf(60 * 60 * 24 * 1000);
                long todayTimestamps = currentTimestamps - (currentTimestamps + 60 * 60 * 8 * 1000) % oneDayTimestamps;

                if (userInfo.getDisabledTime() != null) {
                    long l = userInfo.getDisabledTime() - todayTimestamps;
                    if (l <= 0) {
                        status = EmployeeStatusEnum.RESIGNATION.getCode();
                    }
                }
                if (userInfo.getSystemDisabledTime() != null) {
                    long l = userInfo.getSystemDisabledTime() - todayTimestamps;
                    if (l <= 0) {
                        status = EmployeeStatusEnum.RESIGNATION.getCode();
                    }
                }
            }
            openThirdEmployeeDTO.setStatus(status);

            OpenThirdEmployeeDTO targetDTO = null;
            if (employeeNeedFilter) {
                targetDTO = employeeBeforeSyncFilter(employeeConfig, userInfo, openThirdEmployeeDTO);
            }
            employeeList.add(targetDTO == null ? openThirdEmployeeDTO : targetDTO);

        }
        log.info("【qiqi】 employeeConvert, 参数employeeList:{}", JsonUtils.toJson(employeeList));
        return employeeList;
    }

    /**
     * 树形参数封装
     * @return
     */
    @Override
    public List<QiqiCommonReqDetailDTO> getTreeParam() {
        List<QiqiCommonReqDetailDTO> qiqiCommonReqDetailList = Lists.newArrayList();
        //树形参数封装
        QiqiCommonReqDetailDTO commonReqDetail = new QiqiCommonReqDetailDTO();
        Field[] declaredFields = QiqiJobRelationshipDTO.class.getDeclaredFields();
        String[] fieldArray = Arrays.stream(declaredFields).map(f -> f.getName()).collect(Collectors.toList()).toArray(new String[]{});
        commonReqDetail.setFieldName("jobRelationshipsObject");
        commonReqDetail.setFields(fieldArray);
        qiqiCommonReqDetailList.add(commonReqDetail);
        return qiqiCommonReqDetailList;
    }

}
