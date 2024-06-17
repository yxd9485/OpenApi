package com.fenbeitong.openapi.plugin.customize.wanyang.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.common.utils.basis.StringUtil;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.customize.wanyang.dto.WanYangEmployeeDTO;
import com.fenbeitong.openapi.plugin.customize.wanyang.dto.WanYangOrgUnitDTO;
import com.fenbeitong.openapi.plugin.customize.wanyang.service.OrgUnitSyncService;
import com.fenbeitong.openapi.plugin.support.apply.dto.BaseDTO;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.employee.dto.CertDTO;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.privilege.dao.OpenEmployeeRuleTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.privilege.entity.OpenEmployeeRuleTemplateConfig;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.ThreadUtils;
import com.finhub.framework.exception.ArgumentException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName OrgUnitSyncServiceImpl
 * @Description 万洋组织架构同步
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/8/2 下午7:57
 **/
@Service
@Slf4j
public class OrgUnitSyncServiceImpl implements OrgUnitSyncService {

    @Autowired
    private AuthDefinitionDao authDefinitionDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OpenSysConfigDao openSysConfigDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Value("${wanyang.host}")
    private String wyHost;

    @Autowired
    private OpenEmployeeRuleTemplateConfigDao openEmployeeRuleTemplateConfigDao;

    @Autowired
    private DepartmentUtilService departmentUtilService;

    private static final String ROOT_ID = "root001";

    @Override
    public void syncOrgUnit(String companyId) {
        //校验企业是否存在
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
        if (ObjectUtils.isEmpty(authDefinition)) {
            log.info("企业信息不存在,companyId:" + companyId);
            throw new OpenApiArgumentException("[companyId]公司id不能为空");
        }
        log.info("【wanyang】 orgUnitSyncServiceImpl, 开始同步万洋组织机构人员,companyId={}", companyId);
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                syncOrgEmployee(OpenType.UNKNOW.getType(), companyId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【wangyang】 同步万洋组织架构, 未获取到锁，companyId={}", companyId);
            throw new ArgumentException("未获取到锁");
        }
    }

    public void syncOrgEmployee(int openType, String companyId) {
        //获取当前时间戳
        String currentStr = StringUtil.obj2str(System.currentTimeMillis());
        Long currentTime = Long.valueOf(currentStr.substring(0, currentStr.length() - 3));
        //全量拉取部门数据
        List<WanYangOrgUnitDTO.WyOrgUnitDTO> departmentList = getOrgUnitList(companyId);
        //全量拉取人员数据
        List<WanYangEmployeeDTO.WyEmployeeDTO> userInfos = getEmployeeList(companyId);
        List<OpenThirdOrgUnitDTO> thirdOrgUnitDTOS = convertOrgUnitData(companyId, departmentList);
        List<OpenThirdEmployeeDTO> thirdEmployeeDTOS = convertEmployeeData(companyId, userInfos);
        //同步
        openSyncThirdOrgService.syncThird(openType, companyId, thirdOrgUnitDTOS, thirdEmployeeDTOS);
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        // 判断设置部门主管配置时否为空
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            openSyncThirdOrgService.setAllDepManagePackV2(thirdOrgUnitDTOS, companyId);
        }
    }

    /**
     * 获取万洋部门列表信息
     *
     * @param companyId
     * @return List<WanYangOrgUnitDTO>
     * @author helu
     * @date 2022/8/3 上午10:54
     */
    public List<WanYangOrgUnitDTO.WyOrgUnitDTO> getOrgUnitList(String companyId) {
        Integer pageSize = 1000;
        Integer pageNum = 1;
        List<WanYangOrgUnitDTO.WyOrgUnitDTO> orgUnitAllList = new ArrayList<>();
        WanYangOrgUnitDTO orgUnitDTO = new WanYangOrgUnitDTO();
        do {
            String orgformat = "/departmentEHR/getDepartmentInfo?sysName=OA系统&pageSize="+pageSize+"&pageNum="+pageNum;
            String result = RestHttpUtils.get(wyHost + orgformat, null, Maps.newHashMap());
            BaseDTO orgQueryResult = JsonUtils.toObj(result, BaseDTO.class);
            if (orgQueryResult == null || !orgQueryResult.success()) {
                String msg = orgQueryResult == null ? "" : Optional.ofNullable(orgQueryResult.getMsg()).orElse("");
                throw new FinhubException(500, msg);
            }
            orgUnitDTO = JsonUtils.toObj(JsonUtils.toJson(orgQueryResult.getData()), new TypeReference<WanYangOrgUnitDTO>() {
            });
            orgUnitAllList.addAll(orgUnitDTO.getList());
            pageNum++;
            //saas延迟，等8秒
            ThreadUtils.sleep(8, TimeUnit.SECONDS);
        } while (Integer.parseInt(orgUnitDTO.getPages()) >= pageNum);
        return orgUnitAllList;
    }

    /**
     * 获取万洋人员列表信息
     *
     * @param companyId
     * @return List<WanYangEmployeeDTO>
     * @author helu
     * @date 2022/8/3 上午10:54
     */
    public List<WanYangEmployeeDTO.WyEmployeeDTO> getEmployeeList(String companyId) {
        Integer pageSize = 1000;
        Integer pageNum = 1;
        List<WanYangEmployeeDTO.WyEmployeeDTO> employeeAllList = new ArrayList<>();
        WanYangEmployeeDTO employeePageDTO = new WanYangEmployeeDTO();
        do {
            String empFormat = "/personEHR/getPersonInfo?sysName=OA系统&pageSize="+pageSize+"&pageNum="+pageNum;
            WanYangEmployeeDTO employeeDTO = new WanYangEmployeeDTO();
            String result = RestHttpUtils.get(wyHost + empFormat, null, Maps.newHashMap());
            BaseDTO empQueryResult = JsonUtils.toObj(result, BaseDTO.class);
            if (empQueryResult == null || !empQueryResult.success()) {
                String msg = empQueryResult == null ? "" : Optional.ofNullable(empQueryResult.getMsg()).orElse("");
                throw new FinhubException(500, msg);
            }
            employeePageDTO = JsonUtils.toObj(JsonUtils.toJson(empQueryResult.getData()), new TypeReference<WanYangEmployeeDTO>() {
            });
            employeeAllList.addAll(employeePageDTO.getList());
            pageNum++;
            //saas延迟，等8秒
            ThreadUtils.sleep(8, TimeUnit.SECONDS);
        } while (Integer.parseInt(employeePageDTO.getPages()) >= pageNum);
        return employeeAllList;
    }

    /**
     * 过滤部门并转换数据
     *
     * @param companyId
     * @param orgUnitList
     * @return List<OpenThirdOrgUnitDTO>
     * @author helu
     * @date 2022/8/3 上午11:21
     */
    public List<OpenThirdOrgUnitDTO> convertOrgUnitData(String companyId, List<WanYangOrgUnitDTO.WyOrgUnitDTO> orgUnitList) {
        //过滤部门并转换数据
        List<OpenThirdOrgUnitDTO> thirdOrgUnitList = new ArrayList<>();
        orgUnitList.stream().filter(o -> "0".equals(o.getIS_DELETED())).filter(o -> "1".equals(o.getIS_ENABLE())).forEach(o -> {
            OpenThirdOrgUnitDTO thirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            thirdOrgUnitDTO.setCompanyId(companyId);
            thirdOrgUnitDTO.setThirdOrgUnitId(o.getDepID());
            thirdOrgUnitDTO.setThirdOrgUnitName(o.getDepName());
            thirdOrgUnitDTO.setThirdOrgUnitParentId(o.getSuperDepID());
            thirdOrgUnitDTO.setOrgUnitCode(o.getDepCode());
            //部门主管是否同步
            thirdOrgUnitList.add(thirdOrgUnitDTO);
        });

        //部门排序
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitList = Lists.newArrayList();

        if (!ObjectUtils.isEmpty(thirdOrgUnitList) && thirdOrgUnitList.size() > 0) {
            List<OpenThirdOrgUnitDTO> firstLevel = new ArrayList();
            String rootId = null;
            thirdOrgUnitList.forEach(o -> {
                if (StringUtils.isBlank(o.getThirdOrgUnitParentId()) || "0".equals(o.getThirdOrgUnitParentId())) {
                    //三方父部门为空，则放入list
                    firstLevel.add(o);
                }
            });
            if (firstLevel.size() > 1) {
                //一级部门不止一个
                firstLevel.forEach(c -> {
                    for (OpenThirdOrgUnitDTO openThirdOrgUnit : thirdOrgUnitList) {
                        if (c.getThirdOrgUnitId().equals(openThirdOrgUnit.getThirdOrgUnitId())) {
                            openThirdOrgUnit.setThirdOrgUnitParentId(ROOT_ID);
                        }
                    }
                });
                rootId = ROOT_ID;
            } else {
                //一个一级部门
                rootId = firstLevel.get(0).getThirdOrgUnitId();
                firstLevel.get(0).setThirdOrgUnitParentId("0");
            }
            List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOS = JsonUtils.toObj(JsonUtils.toJson(thirdOrgUnitList), new TypeReference<List<OpenThirdOrgUnitDTO>>() {
            });
            if (StringUtils.isNotBlank(rootId)) {
                log.info("departmentConvert,排序前部门集合：{}", JsonUtils.toJson(openThirdOrgUnitDTOS));

                openThirdOrgUnitList = departmentUtilService.deparmentSortAuto(openThirdOrgUnitDTOS, "万洋集团", 0, rootId, false);
                log.info("departmentConvert,排序后部门集合：{}", JsonUtils.toJson(openThirdOrgUnitList));
            }
        }
            return openThirdOrgUnitList;
    }

    /**
     * 过滤人员并转换数据
     *
     * @param companyId
     * @param employeeList
     * @return List<OpenThirdEmployeeDTO>
     * @author helu
     * @date 2022/8/3 下午12:04
     */
        public List<OpenThirdEmployeeDTO> convertEmployeeData(String companyId, List<WanYangEmployeeDTO.WyEmployeeDTO> employeeList) {

            List<String> personTypes = Arrays.asList("1", "2", "3", "4");
            List<OpenThirdEmployeeDTO> thirdEmployeeList = new ArrayList<>();
            List<OpenEmployeeRuleTemplateConfig> companyEmployeeTemplates =
                openEmployeeRuleTemplateConfigDao.listByCompanyId(companyId);
            employeeList.stream().filter(p -> "1".equals(p.getIS_ENABLE())).filter(p -> {
                if (StringUtils.isEmpty(p.getState()) || personTypes.contains(p.getState())) {
                    return true;
                }
                return false;
            }).filter(p -> "0".equals(p.getIS_DELETED())).filter(p -> personTypes.contains(p.getPersonType())).collect(Collectors.toList()).forEach(p -> {
                OpenThirdEmployeeDTO thirdEmployeeDTO = new OpenThirdEmployeeDTO();
                thirdEmployeeDTO.setCompanyId(companyId);
                thirdEmployeeDTO.setThirdEmployeeId(p.getPersonID());
                thirdEmployeeDTO.setEmployeeNumber(p.getPersonCode());
                thirdEmployeeDTO.setThirdEmployeeName(p.getPersonName());
                thirdEmployeeDTO.setThirdDepartmentId(p.getPersonDepID());
                thirdEmployeeDTO.setThirdEmployeePhone(p.getPhoneNo());
                if (!StringUtils.isEmpty(p.getPersonSex())) {
                    thirdEmployeeDTO.setThirdEmployeeGender("1".equals(p.getPersonSex()) ? 1 : 2);
                }
                //生日日期格式化处理
                thirdEmployeeDTO.setThirdEmployeeBirthday(DateUtils.toStr(DateUtils.toDate(p.getBirthday(),"yyyyMMdd"),"yyyyMMdd"));
                Map<Integer, OpenEmployeeRuleTemplateConfig> ruleTemplateConfigMap =
                    ObjUtils.isEmpty(companyEmployeeTemplates) ? Maps.newHashMap() : companyEmployeeTemplates.stream().collect(
                        Collectors.toMap(OpenEmployeeRuleTemplateConfig::getRoleType, Function.identity(), (o, n) -> n));
                OpenEmployeeRuleTemplateConfig ruleTemplateConfig = ruleTemplateConfigMap.get(Integer.parseInt(p.getPersonPositionID()));
                if (ruleTemplateConfig != null && ObjUtils.isNotBlank(ruleTemplateConfig.getTemplateId())) {
                    //消费权限模版，配置获取
                    thirdEmployeeDTO.setThirdEmployeeTemplateId(ruleTemplateConfig.getTemplateId());
                }
                CertDTO certDTO = new CertDTO();
                certDTO.setCertNo(p.getCardID());
                certDTO.setCertType(1);
                thirdEmployeeDTO.setCerts(Arrays.asList(certDTO));
                thirdEmployeeList.add(thirdEmployeeDTO);
            });
            return thirdEmployeeList;
        }
}
