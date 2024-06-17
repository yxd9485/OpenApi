package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.response.*;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.SyncConstant;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.*;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.dto.*;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.employee.service.BaseEmployeeRefServiceImpl;
import com.fenbeitong.openapi.plugin.support.employee.service.ThirdEmployeePostProcessService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.CompanyInitUserDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.CompanyInitUser;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.init.enums.EmployeeDefineEnum;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyCreatVo;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeBaseInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.exception.ArgumentException;

import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>Title: DingTalkSyncThirdEmployeeServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/12 6:24 PM
 */
@SuppressWarnings("all")
@Primary
@ServiceAspect
@Service
@Slf4j
public class DingTalkSyncThirdEmployeeServiceImpl extends AbstractEmployeeService implements IDingTalkSyncThirdEmployeeService {

    @Autowired
    private IApiDepartmentService apiDepartmentService;

    @Autowired
    private IApiUserService dingtalkUserService;

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private CompanyInitUserDao companyInitUserDao;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private BaseEmployeeRefServiceImpl refService;

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Autowired
    OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    private IDingtalkEiaOrgService dingtalkEiaOrgService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    private ThirdEmployeePostProcessService postProcessService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IDingtalkRosterInfoService iDingtalkRosterInfoService;

    @Autowired
    private EmployeeDTOBuilderServiceImpl employeeDTOBuilderServiceImpl;

    @Override
    public void syncThirdEmployee(String companyId) {
        //分贝用户信息
        List<EmployeeBaseInfo> fbUserList = listFbEmployee(companyId);
        //公司信息
        PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCompanyId(companyId);
        //钉钉平台企业ID
        String thirdCorpId = corpDefinition.getThirdCorpId();
        //钉钉用户信息
        List<DingtalkUser> dingtalkUserList = listDingtalkUser(companyId, thirdCorpId);
        dingtalkUserList = filterDingtalkUser(thirdCorpId, dingtalkUserList);
        //同步部门信息
        Map<String, Object> employeeMap = groupEmployee(fbUserList, dingtalkUserList, companyId, thirdCorpId);
        //需要更新的员工
        List<SupportUpdateEmployeeReqDTO> updateEmployeeReqList = (List<SupportUpdateEmployeeReqDTO>) employeeMap.get(SyncConstant.UPDATE);
        //需要新建的员工
        List<SupportCreateEmployeeReqDTO> createEmployeeReqList = (List<SupportCreateEmployeeReqDTO>) employeeMap.get(SyncConstant.INSERT);
        //需要绑定的员工
        List<SupportBindEmployeeReqDTO> bindEmployeeReqList = (List<SupportBindEmployeeReqDTO>) employeeMap.get(SyncConstant.BIND);
        //同步人员到分贝通
        sync2Finhub(updateEmployeeReqList, createEmployeeReqList, bindEmployeeReqList);
    }

    private List<DingtalkUser> filterDingtalkUser(String thirdCorpId, List<DingtalkUser> dingtalkUserList) {
        CompanyInitUser companyInitUser = companyInitUserDao.getBYCorpId(thirdCorpId);
        List<String> dingtalkUserPhoneList = companyInitUser == null ? Lists.newArrayList() : companyInitUser.getDingtalkUserPhoneList();
        return companyInitUser == null || ObjectUtils.isEmpty(companyInitUser.getDingtalkUserPhones()) ? dingtalkUserList :
                dingtalkUserList.stream().filter(u -> {
                    String userPhone = Optional.ofNullable(u.getFbtMobile()).orElse("").trim();
                    return userPhone.length() >= 11 && dingtalkUserPhoneList.contains(userPhone.substring(userPhone.length() - 11));
                }).collect(Collectors.toList());
    }

    private Map<String, Object> groupEmployee(List<EmployeeBaseInfo> fbUserList, List<DingtalkUser> dingtalkUserList, String companyId, String thirdCorpId) {
        Map<String, Object> employeeMap = Maps.newHashMap();
        Map<String, EmployeeBaseInfo> fbEmployeeMap = fbUserList.stream().filter(u -> !ObjectUtils.isEmpty(u.getThirdEmployeeId())).collect(Collectors.toMap(EmployeeBaseInfo::getThirdEmployeeId, e -> e));
        String superAdmin = superAdmin(companyId);
        dingtalkUserList.stream().filter(u -> !ObjectUtils.isEmpty(u.getUserid())).forEach(dingtalkUser -> {
            EmployeeBaseInfo fbUser = fbEmployeeMap.get(dingtalkUser.getUserid());
            String roleType = dingtalkUser.getFbtRoleType();
            //钉钉人员可以同时在多个部门下，先取扩展字段主部门，如果未设置，分贝默认取第一个部门
            String mainDepartment = dingtalkUser.getMainDepartment();
            String orgId = StringUtils.isBlank(mainDepartment) ? String.valueOf(dingtalkUser.getDepartment().get(0)) : mainDepartment;
            //如果用户是直接添加在公司下，则上级部门设置为第三方公司ID
            if ("1".equals(orgId)) {
                orgId = thirdCorpId;
            }
            String fbtMobile = StringUtils.obj2str(dingtalkUser.getFbtMobile(), "");
            String userPhoneNum = getUserPhoneNum(fbtMobile);
            if (fbUser == null) {
                //1、绑定;2:新增
                int opt = fbUserList.stream().anyMatch(u -> u.getPhone().equals(userPhoneNum)) ? 1 : 2;
                if (opt == 1) {
                    SupportBindEmployeeReqDTO employeeReq = new SupportBindEmployeeReqDTO();
                    employeeReq.setCompanyId(companyId);
                    employeeReq.setBindList(Lists.newArrayList(SupportEmployeeBindInfo.builder().phone(userPhoneNum).thirdEmployeeId(dingtalkUser.getUserid()).build()));
                    employeeReq.setOperatorId(superAdmin);
                    employeeMap.putIfAbsent(SyncConstant.BIND, Lists.newArrayList());
                    List<SupportBindEmployeeReqDTO> bindEmployeeReqList = (List<SupportBindEmployeeReqDTO>) employeeMap.get(SyncConstant.BIND);
                    bindEmployeeReqList.add(employeeReq);
                    //绑定后需要更新权限
                    SupportUpdateEmployeeReqDTO updateEmployeeReq = getSupportUpdateEmployeeReq(companyId, superAdmin, dingtalkUser, roleType, orgId, userPhoneNum);
                    employeeMap.putIfAbsent(SyncConstant.UPDATE, Lists.newArrayList());
                    List<SupportUpdateEmployeeReqDTO> updateEmployeeReqList = (List<SupportUpdateEmployeeReqDTO>) employeeMap.get(SyncConstant.UPDATE);
                    beforeBindUpdate(companyId, updateEmployeeReq);
                    updateEmployeeReqList.add(updateEmployeeReq);
                } else {
                    SupportCreateEmployeeReqDTO employeeReq = new SupportCreateEmployeeReqDTO();
                    employeeReq.setCompanyId(companyId);
                    SupportEmployeeInsertDTO employeeInsert = new SupportEmployeeInsertDTO();
                    employeeInsert.setName(dingtalkUser.getName());
                    employeeInsert.setPhone(userPhoneNum);
                    employeeInsert.setThirdOrgUnitId(orgId);
                    employeeInsert.setThirdEmployeeId(dingtalkUser.getUserid());
                    employeeInsert.setRole(3);
                    employeeInsert.setRoleType(roleType);
                    employeeInsert.setEmail(dingtalkUser.getEmail());
                    employeeInsert.setCertList(Lists.newArrayList());
                    //人员更新前
                    beforeEmployeeInsert(companyId, employeeInsert);
                    employeeReq.setEmployeeList(Lists.newArrayList(employeeInsert));
                    employeeReq.setOperatorId(superAdmin);
                    employeeMap.putIfAbsent(SyncConstant.INSERT, Lists.newArrayList());
                    List<SupportCreateEmployeeReqDTO> createEmployeeReqList = (List<SupportCreateEmployeeReqDTO>) employeeMap.get(SyncConstant.INSERT);
                    createEmployeeReqList.add(employeeReq);
                }
            } else {
                SupportUpdateEmployeeReqDTO employeeReq = getSupportUpdateEmployeeReq(companyId, superAdmin, dingtalkUser, roleType, orgId, userPhoneNum);
                beforeUpdate(companyId, employeeReq);
                employeeMap.putIfAbsent(SyncConstant.UPDATE, Lists.newArrayList());
                List<SupportUpdateEmployeeReqDTO> updateEmployeeReqList = (List<SupportUpdateEmployeeReqDTO>) employeeMap.get(SyncConstant.UPDATE);
                updateEmployeeReqList.add(employeeReq);
            }
        });
        return employeeMap;
    }

    protected void beforeBindUpdate(String companyId, SupportUpdateEmployeeReqDTO updateEmployeeReq) {

    }

    protected void beforeUpdate(String companyId, SupportUpdateEmployeeReqDTO updateEmployeeReq) {
    }


    protected void beforeEmployeeInsert(String companyId, SupportEmployeeInsertDTO employeeInsert) {

    }

    private SupportUpdateEmployeeReqDTO getSupportUpdateEmployeeReq(String companyId, String superAdmin, DingtalkUser dingtalkUser, String roleType, String orgId, String userPhoneNum) {
        SupportUpdateEmployeeReqDTO employeeReq = new SupportUpdateEmployeeReqDTO();
        employeeReq.setCompanyId(companyId);
        SupportEmployeeUpdateDTO employeeUpdate = new SupportEmployeeUpdateDTO();
        employeeUpdate.setUpdateFlag(false);
        employeeUpdate.setName(dingtalkUser.getName());
        employeeUpdate.setPhone(userPhoneNum);
        employeeUpdate.setThirdOrgUnitId(orgId);
        employeeUpdate.setThirdEmployeeId(dingtalkUser.getUserid());
        employeeUpdate.setRoleType(roleType);
        employeeUpdate.setEmail(dingtalkUser.getEmail());
        employeeUpdate.setCertList(Lists.newArrayList());
        employeeReq.setEmployeeList(Lists.newArrayList(employeeUpdate));
        employeeReq.setOperatorId(superAdmin);
        return employeeReq;
    }


    private String getUserPhoneNum(String fbtMobile) {
        String[] arr = fbtMobile.split("-");
        if (arr.length > 1) {
            return arr[1];
        }
        return arr[0];
    }

    protected List<DingtalkUser> listDingtalkUser(String companyId, String corpId) {
        Set<DingtalkUser> dingtalkUserSet = Sets.newHashSet();
        List<OapiDepartmentListResponse.Department> departments = apiDepartmentService.listDepartment(corpId);
        if (!ObjectUtils.isEmpty(departments)) {
            departments.forEach(department -> {
                try {
                    //请求钉钉不要太快 防止超过频率限制
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<DingtalkUser> userList = dingtalkUserService.getAllUserByDepartment(department.getId(), corpId);
                if (!userList.isEmpty()) {
                    dingtalkUserSet.addAll(userList);
                }
            });
        }
        List<DingtalkUser> userList = dingtalkUserService.getAllUserByDepartment(1L, corpId);
        if (!userList.isEmpty()) {
            dingtalkUserSet.addAll(userList);
        }
        return ObjectUtils.isEmpty(dingtalkUserSet)
                ? Lists.newArrayList()
                : new ArrayList<>(dingtalkUserSet);
    }

    protected List<DingtalkUser> listDingtalkUser(List<OapiDepartmentListResponse.Department> departments, String corpId) {
        Set<DingtalkUser> dingtalkUserSet = Sets.newHashSet();
        if (!ObjectUtils.isEmpty(departments)) {
            departments.forEach(department -> {
                try {
                    //请求钉钉不要太快 防止超过频率限制
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<DingtalkUser> userList = dingtalkUserService.getAllUserByDepartment(department.getId(), corpId);
                if (!userList.isEmpty()) {
                    dingtalkUserSet.addAll(userList);
                }
            });
        }
        List<DingtalkUser> userList = dingtalkUserService.getAllUserByDepartment(1L, corpId);
        if (!userList.isEmpty()) {
            dingtalkUserSet.addAll(userList);
        }
        return ObjectUtils.isEmpty(dingtalkUserSet)
                ? Lists.newArrayList()
                : new ArrayList<>(dingtalkUserSet);
    }

    @Override
    public List<DingtalkUser> checkDingtalkEmployee(String companyId) {
        //分贝用户信息
        List<EmployeeBaseInfo> fbUserList = listFbEmployee(companyId);
        //公司信息
        PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCompanyId(companyId);
        //钉钉平台企业ID
        String thirdCorpId = corpDefinition.getThirdCorpId();
        //钉钉用户
        List<DingtalkUser> dingtalkUsers = listDingtalkUser(companyId, thirdCorpId);
        dingtalkUsers = dingtalkUsers.stream().filter(u -> !ObjectUtils.isEmpty(u.getUserid())).collect(Collectors.toList());
        List<String> thirdEmployeeIdList = fbUserList.stream().map(EmployeeBaseInfo::getThirdEmployeeId).collect(Collectors.toList());
        return dingtalkUsers.stream().filter(u -> !thirdEmployeeIdList.contains(u.getUserid())).collect(Collectors.toList());
    }

    @Override
    @Async
    public void syncThirdOrgEmployee(String companyId) {
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                //公司信息
                PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCompanyId(companyId);
                //钉钉平台企业ID
                String thirdCorpId = corpDefinition.getThirdCorpId();
                //钉钉用户信息
                List<OapiDepartmentListResponse.Department> departments = apiDepartmentService.listDepartment(thirdCorpId);
                List<DingtalkUser> dingtalkUserList = listDingtalkUser(departments, thirdCorpId);
                dingtalkUserList = filterDingtalkUser(thirdCorpId, dingtalkUserList);
                CompanyCreatVo companyCreatVo = refService.getCompanyNewInfoService().info(companyId);
                // 创建存储部门ID和名称对应关系map
                Map<Long, String> idMap = departments.stream().collect(Collectors.toMap(OapiDepartmentListResponse.Department::getId, OapiDepartmentListResponse.Department::getName));
                idMap.put(1L, companyCreatVo.getCompanyName());

                String accessToken = dingtalkTokenService.getAccessToken(thirdCorpId);
                String proxyUrl = dingtalkRouteService.getRouteByCorpId(thirdCorpId).getProxyUrl();
                //转换部门
                List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
                // 查询是否需要修改用户
                OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
                Map<String,Long> departName2DepartmentIdMap = new HashMap<>();
                for (OapiDepartmentListResponse.Department departmentInfo : departments) {
                    departName2DepartmentIdMap.put(departmentInfo.getName(),departmentInfo.getId());
                    List<String> nameList = apiDepartmentService.listParentDeptIds(departmentInfo.getId(), thirdCorpId)
                        .stream().map(idMap::get).collect(Collectors.toList());
                    Collections.reverse(nameList);
                    String fullName = String.join("/", nameList);
                    OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
                    openThirdOrgUnitDTO.setCompanyId(companyId);
                    openThirdOrgUnitDTO.setThirdOrgUnitFullName(fullName);
                    openThirdOrgUnitDTO.setThirdOrgUnitName(departmentInfo.getName());
                    openThirdOrgUnitDTO.setThirdOrgUnitParentId(StringUtils.obj2str(departmentInfo.getParentid()));
                    openThirdOrgUnitDTO.setThirdOrgUnitId(StringUtils.obj2str(departmentInfo.getId()));
                    if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
                        openThirdOrgUnitDTO.setThirdOrgUnitId(thirdCorpId);
                    }
                    if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
                        openThirdOrgUnitDTO.setThirdOrgUnitParentId(thirdCorpId);
                    }
                    departmentList.add(openThirdOrgUnitDTO);
                }
                //转换人员
                List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();

                // 获取配置
                Map map = openSysConfigService.getEmployeeDefinedConfig(companyId);
                List<String> userIds = dingtalkUserList.stream().map(DingtalkUser::getUserid).collect(Collectors.toList());
                // 员工ID和花名册信息映射
                Map<String,Map<String,String>> userIdToRouterInfoMap = employeeDTOBuilderServiceImpl.getRouterInfo(companyId,thirdCorpId, userIds);
                for (DingtalkUser userInfo : dingtalkUserList) {
                    // 设置花名册信息
                    userInfo.setRouterInfo(userIdToRouterInfoMap.get(userInfo.getUserid()));
                    userInfo.setDepartmentNameTwoIdMap(departName2DepartmentIdMap);
                    OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                    // 判断配置是否为空，不为空时走配置
                    String roleType = userInfo.getFbtRoleType();
                    String mainDepartment = userInfo.getMainDepartment();
                    String userPhoneNum = getUserPhoneNum(StringUtils.obj2str(userInfo.getFbtMobile(), ""));
                    String idCard = userInfo.getIdCard();
                    if (!ObjectUtils.isEmpty(map) && !ObjectUtils.isEmpty(userInfo.getExtattr())) {
                        roleType = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEROLETYE.getValue())) == null ? roleType : StringUtils.obj2str(userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEROLETYE.getValue())));
                        mainDepartment = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.MAINDEPARTMENT.getValue())) == null ? mainDepartment : userInfo.getExtattr().get(map.get(EmployeeDefineEnum.MAINDEPARTMENT.getValue())).toString();
                        userPhoneNum = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEPHONE.getValue())) == null ? userPhoneNum : userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEPHONE.getValue())).toString();
                        idCard = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.ID_CARD.getValue())) == null ? idCard : userInfo.getExtattr().get(map.get(EmployeeDefineEnum.ID_CARD.getValue())).toString();
                    }
                    String departmentId = StringUtils.isBlank(mainDepartment) ? String.valueOf(userInfo.getDepartment().get(0)) : mainDepartment;
                    openThirdEmployeeDTO.setCompanyId(companyId);
                    openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getUserid());
                    openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
                    openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
                    // 如果手机号自定义字段为空,则取钉钉的默认手机号
                    if (!"".equals(userPhoneNum) && userPhoneNum != null && userPhoneNum.length() >= 11) {
                        userPhoneNum = org.apache.commons.lang3.StringUtils.normalizeSpace(userPhoneNum).substring(org.apache.commons.lang3.StringUtils.normalizeSpace(userPhoneNum).length() - 11);
                        openThirdEmployeeDTO.setThirdEmployeePhone(userPhoneNum);
                    } else {
                        openThirdEmployeeDTO.setThirdEmployeePhone(userInfo.getMobile());
                    }
                    if ("1".equals(departmentId)) {
                        openThirdEmployeeDTO.setThirdDepartmentId(thirdCorpId);
                    } else {
                        openThirdEmployeeDTO.setThirdDepartmentId(departmentId);
                    }
                    if (!ObjectUtils.isEmpty(roleType)) {
                        openThirdEmployeeDTO.setThirdEmployeeRoleTye(roleType);
                    }
                    openThirdEmployeeDTO.setThirdEmployeeIdCard(idCard);
                    // 获取员工自定义的配置文件
                    Map employeeCustomizeAttributeMap = openSysConfigService.getEmployeeConfigByIdAndType(companyId, OpenSysConfigType.EMPLOYEE_CUSTOMIZE_ATTRIBUTE.getType());
                    if (!MapUtils.isBlank(employeeCustomizeAttributeMap)) {
                        Map<String, Object> mapResult = new HashMap<>();
                        Field[] fields = userInfo.getClass().getDeclaredFields();
                        if (!ObjectUtils.isEmpty(fields)) {
                            for (Field f : fields) {
                                f.setAccessible(true);
                                employeeCustomizeAttributeMap.forEach((entryKey, entryValue) -> {
                                    if (entryKey.equals(f.getName())) {
                                        try {
                                            mapResult.put(entryValue.toString(), f.get(userInfo));
                                        } catch (Exception e) {
                                            log.info("全量同步钉钉时反射属性出错,companyId={}", companyId);
                                        }
                                    }
                                });
                            }
                        }
                        if (userInfo.getExtattr() != null) {
                            for (String key : userInfo.getExtattr().keySet()) {
                                employeeCustomizeAttributeMap.forEach((entryKey, entryValue) -> {
                                    if (entryKey.equals(key)) {
                                        mapResult.put(entryValue.toString(), userInfo.getExtattr().get(key));
                                    }
                                });
                            }
                        }
                        if (!MapUtils.isBlank(mapResult)) {
                            openThirdEmployeeDTO.setExtAttr(mapResult);
                        }
                    }
                    openThirdEmployeeDTO = postProcessService.process(openThirdEmployeeDTO, userInfo, companyId, employeeConfig);
                    if ( null != openThirdEmployeeDTO ){
                        employeeList.add(openThirdEmployeeDTO);
                    }
                }
                // 同步部门
                openSyncThirdOrgService.syncThird(OpenType.DINGTALK_EIA.getType(), companyId, departmentList, employeeList);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("获取到锁，companyId={}", companyId);
            throw new ArgumentException("未获取到锁");
        }

    }

    @Override
    @Async
    public void syncThirdOrgEmployeeByAuth(String companyId, String flag) {
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                //公司信息
                PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCompanyId(companyId);
                //钉钉平台企业ID
                String thirdCorpId = corpDefinition.getThirdCorpId();
                //获取授权范围
                OapiAuthScopesResponse authScope = dingtalkEiaOrgService.getAuthScope(thirdCorpId);
                List<Long> authedDept = authScope.getAuthOrgScopes().getAuthedDept();
                List<String> authedUser = authScope.getAuthOrgScopes().getAuthedUser();
                //获取全量部门
                List<OapiDepartmentListResponse.Department> departments = apiDepartmentService.getAllDepartmentsByAuth(authedDept, thirdCorpId, corpDefinition.getAppName());
                //钉钉用户信息
                List<DingtalkUser> dingtalkUserList = listDingtalkUser(departments, thirdCorpId);
                dingtalkUserList = filterDingtalkUser(thirdCorpId, dingtalkUserList);
                CompanyCreatVo companyCreatVo = refService.getCompanyNewInfoService().info(companyId);
                // 创建存储部门ID和名称对应关系map
                Map<Long, String> idMap = departments.stream().collect(Collectors.toMap(OapiDepartmentListResponse.Department::getId, OapiDepartmentListResponse.Department::getName));
                idMap.put(1L, companyCreatVo.getCompanyName());

                String accessToken = dingtalkTokenService.getAccessToken(thirdCorpId);
                String proxyUrl = dingtalkRouteService.getRouteByCorpId(thirdCorpId).getProxyUrl();

                //转换部门
                List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
                for (OapiDepartmentListResponse.Department departmentInfo : departments) {
                    List<String> nameList = apiDepartmentService.listParentDeptIds(departmentInfo.getId(), thirdCorpId)
                            .stream().map(idMap::get).collect(Collectors.toList());
                    Collections.reverse(nameList);
                    String fullName = String.join("/", nameList);
                    OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
                    openThirdOrgUnitDTO.setCompanyId(companyId);
                    openThirdOrgUnitDTO.setThirdOrgUnitFullName(fullName);
                    openThirdOrgUnitDTO.setThirdOrgUnitName(departmentInfo.getName());
                    openThirdOrgUnitDTO.setThirdOrgUnitParentId(StringUtils.obj2str(departmentInfo.getParentid()));
                    openThirdOrgUnitDTO.setThirdOrgUnitId(StringUtils.obj2str(departmentInfo.getId()));
                    if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
                        openThirdOrgUnitDTO.setThirdOrgUnitId(thirdCorpId);
                    }
                    if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
                        openThirdOrgUnitDTO.setThirdOrgUnitParentId(thirdCorpId);
                    }
                    departmentList.add(openThirdOrgUnitDTO);
                }
                //转换人员
                List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();

                // 获取配置
                Map map = openSysConfigService.getEmployeeDefinedConfig(companyId);
                for (DingtalkUser userInfo : dingtalkUserList) {
                    OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                    // 判断配置是否为空，不为空时走配置
                    String roleType = userInfo.getFbtRoleType();
                    String mainDepartment = userInfo.getMainDepartment();
                    String userPhoneNum = getUserPhoneNum(StringUtils.obj2str(userInfo.getFbtMobile(), ""));
                    String idCard = userInfo.getIdCard();
                    if (!ObjectUtils.isEmpty(map) && !ObjectUtils.isEmpty(userInfo.getExtattr())) {
                        roleType = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEROLETYE.getValue())) == null ? roleType : StringUtils.obj2str(userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEROLETYE.getValue())));
                        mainDepartment = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.MAINDEPARTMENT.getValue())) == null ? mainDepartment : userInfo.getExtattr().get(map.get(EmployeeDefineEnum.MAINDEPARTMENT.getValue())).toString();
                        userPhoneNum = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEPHONE.getValue())) == null ? userPhoneNum : userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEPHONE.getValue())).toString();
                        idCard = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.ID_CARD.getValue())) == null ? idCard : userInfo.getExtattr().get(map.get(EmployeeDefineEnum.ID_CARD.getValue())).toString();
                    }
                    String departmentId = StringUtils.isBlank(mainDepartment) ? String.valueOf(userInfo.getDepartment().get(0)) : mainDepartment;
                    openThirdEmployeeDTO.setCompanyId(companyId);
                    openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getUserid());
                    openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
                    openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
                    // 如果手机号自定义字段为空,则取钉钉的默认手机号
                    if (!"".equals(userPhoneNum) && userPhoneNum != null) {
                        userPhoneNum = org.apache.commons.lang3.StringUtils.normalizeSpace(userPhoneNum).substring(org.apache.commons.lang3.StringUtils.normalizeSpace(userPhoneNum).length() - userPhoneNum.length());
                        openThirdEmployeeDTO.setThirdEmployeePhone(userPhoneNum);
                    } else {
                        openThirdEmployeeDTO.setThirdEmployeePhone(userInfo.getMobile());
                    }
                    if ("1".equals(departmentId)) {
                        openThirdEmployeeDTO.setThirdDepartmentId(thirdCorpId);
                    } else {
                        openThirdEmployeeDTO.setThirdDepartmentId(departmentId);
                    }
                    openThirdEmployeeDTO.setThirdEmployeeRoleTye(roleType);
                    openThirdEmployeeDTO.setThirdEmployeeIdCard(idCard);
                    // 获取员工自定义的配置文件
                    Map employeeCustomizeAttributeMap = openSysConfigService.getEmployeeConfigByIdAndType(companyId, OpenSysConfigType.EMPLOYEE_CUSTOMIZE_ATTRIBUTE.getType());
                    if (!MapUtils.isBlank(employeeCustomizeAttributeMap)) {
                        Map<String, Object> mapResult = new HashMap<>();
                        Field[] fields = userInfo.getClass().getDeclaredFields();
                        if (!ObjectUtils.isEmpty(fields)) {
                            for (Field f : fields) {
                                f.setAccessible(true);
                                employeeCustomizeAttributeMap.forEach((entryKey, entryValue) -> {
                                    if (entryKey.equals(f.getName())) {
                                        try {
                                            mapResult.put(entryValue.toString(), f.get(userInfo));
                                        } catch (Exception e) {
                                            log.info("全量同步钉钉时反射属性出错,companyId={}", companyId);
                                        }
                                    }
                                });
                            }
                        }
                        if (userInfo.getExtattr() != null) {
                            for (String key : userInfo.getExtattr().keySet()) {
                                employeeCustomizeAttributeMap.forEach((entryKey, entryValue) -> {
                                    if (entryKey.equals(key)) {
                                        mapResult.put(entryValue.toString(), userInfo.getExtattr().get(key));
                                    }
                                });
                            }
                        }
                        if (!MapUtils.isBlank(mapResult)) {
                            openThirdEmployeeDTO.setExtAttr(mapResult);
                        }
                    }
                    employeeList.add(openThirdEmployeeDTO);
                }

                // 是否增量 默认是全量 0:增量 1:全量
                boolean isUpdate = false;
                if ("0".equals(flag)) {
                    isUpdate = true;
                } else if ("1".equals(flag)) {
                    isUpdate = false;
                }

                // 同步部门
                openSyncThirdOrgService.syncThird(OpenType.DINGTALK_EIA.getType(), companyId, departmentList, employeeList, isUpdate);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("获取到锁，companyId={}", companyId);
        }
    }

    /**
     * 获取部门负责人
     */
    public String getDepMasters(String accessToken, String proxyUrl, String depId) {
        OapiDepartmentGetResponse oapiDepartmentGetResponse = apiDepartmentService.getDepartmentInfo(accessToken, proxyUrl, depId);
        if (oapiDepartmentGetResponse != null) {
            return oapiDepartmentGetResponse.getDeptManagerUseridList().replaceAll("\\|", ",");
        }
        return null;
    }

    /**
     * 部门主管同步，中间表对接
     */
    @Override
    @Async
    public void syncThirdOrgManagers(String companyId) {
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            //公司信息
            PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCompanyId(companyId);
            //钉钉平台企业ID
            String thirdCorpId = corpDefinition.getThirdCorpId();
            //钉钉用户信息
            List<OapiDepartmentListResponse.Department> departments = apiDepartmentService.listDepartment(thirdCorpId);
            String accessToken = dingtalkTokenService.getAccessToken(thirdCorpId);
            String proxyUrl = dingtalkRouteService.getRouteByCorpId(thirdCorpId).getProxyUrl();
            List<OpenThirdOrgUnitManagers> openThirdOrgUnitManagersList = new ArrayList<>();
            departments.forEach(department -> {
                String managers = getDepMasters(accessToken, proxyUrl, StringUtils.obj2str(department.getId()));
                if (!StringUtils.isBlank(managers) && !StringUtils.obj2str(department.getId()).equals(thirdCorpId)) {
                    openThirdOrgUnitManagersList.add(OpenThirdOrgUnitManagers.builder()
                            .id(RandomUtils.bsonId())
                            .companyId(companyId)
                            .thirdEmployeeIds(managers)
                            .thirdOrgUnitId(StringUtils.obj2str(department.getId()))
                            .status(0)
                            .createTime(new Date())
                            .updateTime(new Date())
                            .build());
                }
            });
            openSyncThirdOrgService.setAllDepManageV2(openThirdOrgUnitManagersList, companyId);
        } else {
            throw new FinhubException(0, "企业:" + companyId + "未开放同步部门主管功能，请检查配置是否正确");
        }
    }
}
