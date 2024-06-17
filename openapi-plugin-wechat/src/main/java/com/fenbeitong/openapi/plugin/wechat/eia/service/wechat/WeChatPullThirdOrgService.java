package com.fenbeitong.openapi.plugin.wechat.eia.service.wechat;

import cn.hutool.core.util.ObjectUtil;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.common.dao.OpenExtInfoDao;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenExtInfo;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dto.CompanySuperAdmin;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportBindEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportEmployeeBindInfo;
import com.fenbeitong.openapi.plugin.support.employee.service.IThirdOrgPostProcessService;
import com.fenbeitong.openapi.plugin.support.employee.service.ThirdEmployeePostProcessService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdExpandFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdExpandFieldConfig;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.init.enums.EmployeeDefineEnum;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.*;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WechatEiaPullOrgConstant;
import com.fenbeitong.openapi.plugin.wechat.eia.dao.WechatTokenConfDao;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.QywxEmployee;
import com.fenbeitong.openapi.plugin.wechat.eia.service.employee.WeChatEiaEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.organization.WeChatEiaOrgUnitService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.exception.ArgumentException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 调用企业微信接口获取企业微信组织架构数据
 * Created by Z.H.W on 20/02/18.
 */
@ServiceAspect
@Service
@Slf4j
@SuppressWarnings("all")
public class WeChatPullThirdOrgService {

    @Autowired
    private WeChatOrganizationService weChatOrganizationService;

    @Autowired
    private WechatTokenService wechatTokenService;

    @Autowired
    private WeChatEmployeeService weChatEmployeeService;

    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private WechatTokenConfDao wechatTokenConfDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private OpenThirdExpandFieldConfigDao expandFieldConfigDao;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    OpenExtInfoDao openExtInfoDao;

    @Autowired
    OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    WeChatEiaOrgUnitService weChatEiaOrgUnitService;

    @Autowired
    WeChatEiaEmployeeService weChatEiaEmployeeService;

    @Autowired
    DepartmentUtilService departmentUtilService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    private ThirdEmployeePostProcessService thirdEmployeePostProcessService;

    @Autowired
    private IThirdOrgPostProcessService thirdOrgPostProcessService;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    PluginCallWeChatEiaService pluginCallWeChatEiaService;



    /**
     * 同步企业微信的组织架构到分贝通
     *
     * @param corpId
     * @param deptId
     */
    public String pullThirdOrgBak(String corpId, String deptId) {
        long start = System.currentTimeMillis();
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        //公司id
        String companyId = corpDefinition.getAppId();
        deptId = ObjectUtils.isEmpty(deptId) ? "1" : deptId;
        //企业wqxc token
        String wechatToken = wechatTokenService.getWeChatContactToken(companyId);
        //同步部门结果
        Map<String, Object> departmentMap = weChatOrganizationService.syncWechatOrgUnit(wechatToken, corpId, deptId);
        //同步人员结果
        Map<String, Object> userMap = weChatEmployeeService.syncWechatUser(wechatToken, corpId, deptId);
        //先删除人员
        deleteUser(userMap);
        //删除部门
        deleteDepartment(departmentMap);
        //更新或增加部门
        upsertDepartment(companyId, departmentMap);
        //更新或增加人员
        upsertUser(userMap);
        //绑定授权负责人
        bindSuperAdmin(companyId);
        long end = System.currentTimeMillis();
        log.info("同步完成，用时{}分钟{}秒...", (end - start) / 60000, ((end - start) % 60000) / 1000);
        //返回成功
        return "Success";
    }

    private void bindSuperAdmin(String companyId) {
        CompanySuperAdmin superAdmin = superAdmin(companyId);
        if (superAdmin != null) {
            String superAdminId = superAdmin.getId();
            String phoneNum = superAdmin.getPhoneNum();
            QywxEmployee employee = weChatEmployeeService.getQywxEmployeeByMobile(phoneNum);
            if (employee != null) {
                SupportBindEmployeeReqDTO bindEmployeeReq = new SupportBindEmployeeReqDTO();
                bindEmployeeReq.setCompanyId(companyId);
                bindEmployeeReq.setBindList(Lists.newArrayList(SupportEmployeeBindInfo.builder().phone(phoneNum).thirdEmployeeId(employee.getUserId()).build()));
                bindEmployeeReq.setOperatorId(superAdminId);
                weChatEmployeeService.bindUser(bindEmployeeReq);
            }
        }
    }

    private CompanySuperAdmin superAdmin(String companyId) {
        return superAdminUtils.companySuperAdmin(companyId);
    }

    private void upsertUser(Map<String, Object> userMap) {
        //需要新建的人员
        WeChatEmployeeService.WeChatUserAdd userAdd = (WeChatEmployeeService.WeChatUserAdd) userMap.get(WechatEiaPullOrgConstant.INSERT);
        //增加人员
        weChatEmployeeService.createEmployee(userAdd);
        //需要更新的人员
        WeChatEmployeeService.WeChatUserUpdate userUpdate = (WeChatEmployeeService.WeChatUserUpdate) userMap.get(WechatEiaPullOrgConstant.UPDATE);
        //更新人员
        weChatEmployeeService.updateEmployee(userUpdate);
    }

    private void upsertDepartment(String companyId, Map<String, Object> departmentMap) {
        //需要新建的部门
        List<WeChatOrganizationService.WechatOrgUnitAdd> addOrgList = (List<WeChatOrganizationService.WechatOrgUnitAdd>) departmentMap.get(WechatEiaPullOrgConstant.INSERT);
        weChatOrganizationService.addOrgUnit(companyId, addOrgList);
        //需要更新的部门
        List<WeChatOrganizationService.WechatOrgUnitUpdate> updateOrgList = (List<WeChatOrganizationService.WechatOrgUnitUpdate>) departmentMap.get(WechatEiaPullOrgConstant.UPDATE);
        weChatOrganizationService.updateOrgUnit(companyId, updateOrgList);
    }

    private void deleteDepartment(Map<String, Object> departmentMap) {
        //需要删除的部门
        List<WeChatOrganizationService.WechatOrgUnitDelete> deleteOrgList = (List<WeChatOrganizationService.WechatOrgUnitDelete>) departmentMap.get(WechatEiaPullOrgConstant.DELETE);
        //删除部门
        weChatOrganizationService.deleteOrgUnit(deleteOrgList);
    }

    private void deleteUser(Map<String, Object> userMap) {
        //需要删除的人员
        WeChatEmployeeService.WeChatUserDelete userDelete = (WeChatEmployeeService.WeChatUserDelete) userMap.get(WechatEiaPullOrgConstant.DELETE);
        //删除人员
        weChatEmployeeService.deleteEmployee(userDelete);
    }

    public Object checkDepartment(String corpId) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        //公司id
        String companyId = corpDefinition.getAppId();
        //公司名称
        String companyName = corpDefinition.getAppName();
        //企业wqxc token
        String wechatToken = wechatTokenService.getWeChatContactToken(companyId);
        return weChatOrganizationService.checkDepartment(wechatToken, companyId, corpId, companyName);
    }

    public Object checkEmployee(String corpId) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        //公司id
        String companyId = corpDefinition.getAppId();
        //企业wqxc token
        String wechatToken = wechatTokenService.getWeChatContactToken(companyId);
        return weChatEmployeeService.checkEmployee(wechatToken, companyId);
    }

    /**
     * 同步企业微信的组织架构到分贝通
     *
     * @param corpId
     * @param deptId
     */
    @Async
    public String pullThirdOrg(String corpId, String deptId) {
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, corpId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                long start = System.currentTimeMillis();
                PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
                //公司id
                String companyId = corpDefinition.getAppId();
                // 查询配置判断该企业是否要同步部门主管
                OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
                deptId = ObjectUtils.isEmpty(deptId) ? "1" : deptId;
                String wechatToken = "";
                if ("scope".equals(deptId)) {
                    deptId = "";
                    wechatToken = wechatTokenService.getWeChatAppToken(companyId);
                } else {
                    wechatToken = wechatTokenService.getWeChatContactToken(companyId);
                }
                List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
                List<WechatDepartmentListRespDTO.WechatDepartment> wechatDepartmentList = getWechatDept(companyId, corpId, wechatToken, deptId, corpDefinition.getAppName(), departmentList);
                List<OpenThirdEmployeeDTO> employeeList = getWechatEmployee(companyId, corpId, openSysConfig, departmentList, deptId, wechatDepartmentList, wechatToken, false);
                //同步
                openSyncThirdOrgService.syncThird(OpenType.WECHAT_EIA.getType(), companyId, departmentList, employeeList);
                // 设置部门主管
                setDepManager(companyId, departmentList, openSysConfig);
                long end = System.currentTimeMillis();
                log.info("同步完成，用时{}分钟{}秒...", (end - start) / 60000, ((end - start) % 60000) / 1000);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("未获取到锁，corpId={}", corpId);
            throw new ArgumentException("未获取到锁");
        }
        //返回成功
        return "Success";
    }


    private List<WechatDepartmentListRespDTO.WechatDepartment> getWechatDept(String companyId, String corpId, String wechatToken, String deptId, String appName, List<OpenThirdOrgUnitDTO> departmentList) {
        //获取企业微信全量部门
        List<WechatDepartmentListRespDTO.WechatDepartment> wechatDepartmentList = weChatOrganizationService.getWechatDepartmentList(wechatToken, deptId, appName);
        //转换部门
        packageDep(departmentList, wechatDepartmentList, companyId, corpId);
        return wechatDepartmentList;
    }

    private List<OpenThirdEmployeeDTO> getWechatEmployee(String companyId, String corpId, OpenSysConfig openSysConfig, List<OpenThirdOrgUnitDTO> departmentList, String deptId, List<WechatDepartmentListRespDTO.WechatDepartment> wechatDepartmentList, String wechatToken, Boolean isUnion) {
        //获取企业微信全量人员
        List<WechatUserListRespDTO.WechatUser> wechatUserList = Lists.newArrayList();
        if (StringUtils.isBlank(deptId)) {
            wechatUserList = weChatEmployeeService.getWechatUserListByDeptList(wechatToken, wechatDepartmentList);
        } else {
            wechatUserList = weChatEmployeeService.getWechatUserList(wechatToken, deptId);
        }
        // 部门数据转Map
        Map<String, OpenThirdOrgUnitDTO> openThirdOrgUnitDTOMap = departmentList.stream().collect(Collectors.toMap(t -> t.getThirdOrgUnitId(), Function.identity(), (o, n) -> n, LinkedHashMap::new));
        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        packageEmployee(employeeList, wechatUserList, openThirdOrgUnitDTOMap, companyId, corpId, openSysConfig, isUnion);
        // map 转list map里面封装了部门负责ID所以需要在转成list
        departmentList = openThirdOrgUnitDTOMap.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
        return employeeList;
    }

    /**
     * 部门数据封装
     */
    public void packageDep(List<OpenThirdOrgUnitDTO> departmentList, List<WechatDepartmentListRespDTO.WechatDepartment> wechatDepartmentList, String companyId, String corpId) {
        OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.DEPARTMENT_SYNC);
        for (WechatDepartmentListRespDTO.WechatDepartment wechatDepartment : wechatDepartmentList) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            openThirdOrgUnitDTO.setCompanyId(companyId);
            openThirdOrgUnitDTO.setThirdOrgUnitFullName(wechatDepartment.getThirdOrgUnitFullName());
            openThirdOrgUnitDTO.setThirdOrgUnitName(wechatDepartment.getName());
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(StringUtils.obj2str(wechatDepartment.getParentId()));
            openThirdOrgUnitDTO.setThirdOrgUnitId(StringUtils.obj2str(wechatDepartment.getId()));
            if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
                openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
            }
            if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
                openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
            }
            openThirdOrgUnitDTO = thirdOrgPostProcessService.process(openThirdOrgUnitDTO,wechatDepartment,companyId,departmentConfig);
            if ( null != openThirdOrgUnitDTO ){
                departmentList.add(openThirdOrgUnitDTO);
            }
        }

    }

    /**
     * 人员数据封装
     */
    public void packageEmployee(List<OpenThirdEmployeeDTO> employeeList, List<WechatUserListRespDTO.WechatUser> wechatUserList, Map<String, OpenThirdOrgUnitDTO> openThirdOrgUnitDTOMap, String companyId, String corpId, OpenSysConfig openSysConfig, Boolean isUnion) {
        //扩展字段配置
        OpenThirdExpandFieldConfig expandFieldConfig = expandFieldConfigDao.getByCompanyId(companyId);
        //人员扩展字段
        String userExpandFields = expandFieldConfig == null ? null : expandFieldConfig.getUserExpandFields();
        // 查询是否需要修改用户
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
        /**
         * 查询该公司下的配置
         */
        Map<String, OpenExtInfo> thirdPermissionMap = openExtInfoDao.getOpenExtInfosByCompanyId(companyId);
        for (WechatUserListRespDTO.WechatUser wechatUser : wechatUserList) {
            OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
            openThirdEmployeeDTO.setCompanyId(companyId);
            openThirdEmployeeDTO.setThirdDepartmentId(wechatUser.getDepartmentStr());
            openThirdEmployeeDTO.setThirdEmployeeId(wechatUser.getUserId());
            openThirdEmployeeDTO.setThirdEmployeePhone(wechatUser.getMobile());
            openThirdEmployeeDTO.setThirdEmployeeEmail(wechatUser.getEmail());
            if (!StringUtils.isBlank(wechatUser.getGender())) {
                openThirdEmployeeDTO.setThirdEmployeeGender(Integer.valueOf(wechatUser.getGender()));
            }
            // 1=已激活，2=已禁用，4=未激活，5=退出企业。
            if (1 == wechatUser.getStatus() || 2 == wechatUser.getStatus()) {
                openThirdEmployeeDTO.setStatus(wechatUser.getStatus());
            }
            // 未激活算正常状态
            if (4 == wechatUser.getStatus()) {
                openThirdEmployeeDTO.setStatus(1);
            }
            // 退出企业丢弃删除
            if (5 == wechatUser.getStatus()) {
                continue;
            }
            // 判断是否是互联企业，如果是互联企业不处理
            if (!isUnion) {
                if ("1".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
                    openThirdEmployeeDTO.setThirdDepartmentId(corpId);
                }
            }

            // 权限
            String nFbPriv = "";
            if (ObjectUtils.isEmpty(thirdPermissionMap)) {
                nFbPriv = wechatUser.getAttrValueByAttrName("分贝权限", "");
                openThirdEmployeeDTO.setThirdEmployeeRoleTye(nFbPriv);
            } else {
                //分贝权限
                OpenExtInfo openExtInfoFirst = null;
                for (OpenExtInfo val : thirdPermissionMap.values()) {
                    openExtInfoFirst = val;
                    break;
                }
                nFbPriv = wechatUser.getAttrValueByAttrName(openExtInfoFirst.getMapKey() != null ?
                        openExtInfoFirst.getMapKey() : "", "");

                if ((!StringUtils.isBlank(nFbPriv)) && ObjectUtil.isNotNull(thirdPermissionMap.get(nFbPriv))) {
                    openThirdEmployeeDTO.setThirdEmployeeRoleTye(StringUtils.obj2str(thirdPermissionMap.get(nFbPriv).getRoleType()));
                }
            }
            //新身份证号
            String nIdCard = wechatUser.getAttrValueByAttrName("身份证号", "");
            if (!StringUtils.isBlank(nIdCard)) {
                openThirdEmployeeDTO.setThirdEmployeeIdCard(nIdCard);
            }
            //分贝通手机号
            String fbtMobile = wechatUser.getAttrValueByAttrName("分贝手机", "");
            if (!StringUtils.isBlank(fbtMobile)) {
                openThirdEmployeeDTO.setThirdEmployeePhone(fbtMobile);
            }

            String thirdEmployeeName = "";


            // 获取配置
            Map map = openSysConfigService.getEmployeeDefinedConfig(companyId);
            if (ObjectUtils.isEmpty(map)) {
                thirdEmployeeName = wechatUser.getAttrValueByAttrName("分贝姓名", "");
            } else {
                //分贝通名字
                thirdEmployeeName = wechatUser.getAttrValueByAttrName(map.get(EmployeeDefineEnum.THIRDEMPLOYEENAME.getValue()) != null ?
                        map.get(EmployeeDefineEnum.THIRDEMPLOYEENAME.getValue()).toString() : "", "");
            }
            if (StringUtils.isTrimBlank(thirdEmployeeName)) {
                openThirdEmployeeDTO.setThirdEmployeeName(wechatUser.getName());
            } else {
                openThirdEmployeeDTO.setThirdEmployeeName(thirdEmployeeName);
            }


            if (!ObjectUtils.isEmpty(userExpandFields)) {
                Map expandJson = Maps.newHashMap();
                Lists.newArrayList(userExpandFields.split(",")).forEach(field -> {
                    String value = wechatUser.getAttrValueByAttrName(field, null);
                    if (!ObjectUtils.isEmpty(value)) {
                        expandJson.put(field, value);
                    }
                });
                if (!ObjectUtils.isEmpty(expandJson)) {
                    openThirdEmployeeDTO.setExtAttr(expandJson);
                }
            }
            // 获取企业人员三方id名称配置
            String thirdEmployeeIdName = openSysConfigService.getOpenSysConfigByTypeCode(OpenSysConfigCode.TYPE_THIRD_EMPLOYEE_ID_NAME.getCode(), corpId);
            if (!StringUtils.isBlank(thirdEmployeeIdName)) {
                // 取自定义三方id
                String thirdEmployeeId = wechatUser.getAttrValueByAttrName(thirdEmployeeIdName, "");
                if (!StringUtils.isBlank(thirdEmployeeId)) {
                    openThirdEmployeeDTO.setThirdEmployeeId(thirdEmployeeId);
                } else {
                    log.info("已配置自定义人员三方id，但未从企业微信获取到值，将使用userId字段。companyId={}, userInfo={}", companyId, JsonUtils.toJson(wechatUser));
                }
            }
            openThirdEmployeeDTO = thirdEmployeePostProcessService.process(openThirdEmployeeDTO,wechatUser,companyId,employeeConfig);
            if ( null != openThirdEmployeeDTO ){
                employeeList.add(openThirdEmployeeDTO);
            }

            // 封装部门主管数据
            if (!ObjectUtils.isEmpty(openSysConfig) && !ObjectUtils.isEmpty(openThirdOrgUnitDTOMap)) {
                try {
                    packageManager(wechatUser, openThirdOrgUnitDTOMap, corpId);
                } catch (Exception e) {
                    log.info("企业微信部门主管数据异常 corpId:{},wechatUser:{}",corpId,JsonUtils.toJson(wechatUser));
                }

            }
        }

    }

    /**
     * 封装部门主管数据
     */
    public void packageManager(WechatUserListRespDTO.WechatUser wechatUser, Map<String, OpenThirdOrgUnitDTO> openThirdOrgUnitDTOMap, String corpId) {
        // 两集合变map
        Map<String, String> map = (Map) wechatUser.getDepartmentList().stream().collect(Collectors.toMap(key -> key.toString(), key -> wechatUser.getIsLeaderInDept().get(wechatUser.getDepartmentList().indexOf(key)).toString()));
        map.forEach((key, value) -> {
            // 1是主管  0不是主管
            if ("1".equals(value)) {
                OpenThirdOrgUnitDTO openThirdOrgUnitDTO = openThirdOrgUnitDTOMap.get("1".equals(key) ? corpId : key);
                if (!ObjectUtils.isEmpty(openThirdOrgUnitDTO)) {
                    openThirdOrgUnitDTO.setOrgUnitMasterIds((openThirdOrgUnitDTO.getOrgUnitMasterIds() == null ? "" : openThirdOrgUnitDTO.getOrgUnitMasterIds()) + wechatUser.getUserId() + ",");
                    openThirdOrgUnitDTOMap.put(key, openThirdOrgUnitDTO);
                }
            }
        });
    }

    /**
     * 设置部门主管
     */
    public void setDepManager(String companyId, List<OpenThirdOrgUnitDTO> departmentList, OpenSysConfig openSysConfig) {
        // 判断设置部门主管配置时否为空
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            List<OpenThirdOrgUnitManagers> openThirdOrgUnitManagers = openThirdOrgUnitManagersDao.getListOrgUnitManagersByCompanyIdAndStatus(companyId);
            if (!ObjectUtils.isEmpty(openThirdOrgUnitManagers)) {
                Set<String> set1 = openThirdOrgUnitManagers.stream().map(t -> t.getThirdOrgUnitId()).collect(Collectors.toSet());
                Set<String> set2 = departmentList.stream().map(t -> t.getThirdOrgUnitId()).collect(Collectors.toSet());
                set1.removeAll(set2);
                if (!ObjectUtils.isEmpty(set1)) {
                    set1.forEach(t -> {
                        openThirdOrgUnitManagersDao.deleteByThirdOrgUnitId(companyId, t);
                    });
                }
            }
            openSyncThirdOrgService.setAllDepManagePackV2(departmentList, companyId);
        }
    }


    /**
     * 互联企业组织架构同步
     */
    @Async
    public void pullUnionThirdOrg(String corpId) {
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList = new ArrayList<>();
        List<OpenThirdEmployeeDTO> openThirdEmployeeDTOList = new ArrayList<>();
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        String companyId = corpDefinition.getAppId();
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        // 获取主公司token 用于查询应用人员
        String wechatMainToken = wechatTokenService.getWeChatAppToken(companyId);
        // 获取应用token 用于同步互联企业
        String wechatUnionToken = wechatTokenService.getWeChatAppTokenByCorpId(corpId);
        // 获取主企业部门
        WechatDepartmentListRespDTO departmentListResp = weChatEiaOrgUnitService.getAllDepByDepId(wechatUnionToken, "");
        List<OpenThirdOrgUnitDTO> departmentList = unionMainDepPackage(departmentListResp, companyId, corpId);
        departmentList = departmentUtilService.deparmentSortAuto(departmentList, corpDefinition.getAppName(), 0, corpId,true);
        List<OpenThirdEmployeeDTO> employeeList = getWechatEmployee(companyId, corpId, openSysConfig, departmentList, "", departmentListResp.getDepartmentList(), wechatMainToken, true);
        openThirdOrgUnitDTOList.addAll(departmentList);
        openThirdEmployeeDTOList.addAll(employeeList);
        // 用于过滤三方部门是否存在
        Set<String> thirdOrgUnitIdSet = new HashSet<>();
        // 用于过滤三方人员是否存在
        Set<String> thirdEmployeePhoneSet = employeeList.stream().filter(t -> !StringUtils.isBlank(t.getThirdEmployeePhone())).map(t -> t.getThirdEmployeePhone()).collect(Collectors.toSet());
        // 查询人员封装脚本配置是否开启
        OpenThirdScriptConfig openThirdScriptConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.UNION_WECHAT_EMPLOYEE_SYNC);
        // 查询互联企业
        WeChatLinkedCorpDTO weChatLinkedCorpDTO = weChatEiaOrgUnitService.getLinkedCorp(wechatUnionToken);
        weChatLinkedCorpDTO.getDepartmentIds().forEach(department -> {
            String unionCorpId = StringUtils.obj2str(department.split("/")[0]);
            // 根据部门ID获取当前部门及子部门数据
            WeChatLinkedCorpDepListDTO weChatLinkedCorpDepListDTO = weChatEiaOrgUnitService.getLinkedCorpDepartmentList(wechatUnionToken, department);
            // 封装部门数据
            List<OpenThirdOrgUnitDTO> unionDeptList = unionDeptPackage(weChatLinkedCorpDepListDTO.getDepartmentList(), unionCorpId, thirdOrgUnitIdSet, companyId);
            if (!ObjectUtils.isEmpty(unionDeptList)) {
                // 部门排序
                List<OpenThirdOrgUnitDTO> orderedUnionDeptList = departmentUtilService.deparmentSortAuto(unionDeptList, corpDefinition.getAppName(), 0, corpId,true);
                if (!ObjectUtils.isEmpty(orderedUnionDeptList)) {
                    openThirdOrgUnitDTOList.addAll(orderedUnionDeptList);
                    // 获取互联企业人员
                    orderedUnionDeptList.forEach(dept -> {
                        WeChatLinkedCorpUserlistDTO weChatLinkedCorpUserlistDTO = weChatEiaEmployeeService.getLinkedCorpUserlist(wechatUnionToken, dept.getThirdOrgUnitId());
                        List<OpenThirdEmployeeDTO> unionThirdEmployeeDTOS = new ArrayList<>();
                        if (!ObjectUtils.isEmpty(openThirdScriptConfig)) {
                            unionThirdEmployeeDTOS = EtlUtils.etlFilter(openThirdScriptConfig, new HashMap<String, Object>() {
                                {
                                    put("weChatLinkedCorpUserlistDTO", weChatLinkedCorpUserlistDTO);
                                    put("thirdEmployeePhoneSet", thirdEmployeePhoneSet);
                                    put("companyId", companyId);
                                }
                            });
                        } else {
                            unionThirdEmployeeDTOS = unionEmployeePackage(weChatLinkedCorpUserlistDTO, thirdEmployeePhoneSet, companyId);
                        }
                        openThirdEmployeeDTOList.addAll(unionThirdEmployeeDTOS);
                    });
                }
            }
        });
        // 组织架构同步
        openSyncThirdOrgService.syncThird(OpenType.WECHAT_EIA.getType(), companyId, openThirdOrgUnitDTOList, openThirdEmployeeDTOList);
        // 设置部门主管,互联企业只支持主企业主管设置
        setDepManager(companyId, departmentList, openSysConfig);
    }

    /**
     * 互联企业部门数据封装
     */
    private List<OpenThirdOrgUnitDTO> unionDeptPackage(List<WeChatLinkedCorpDepListDTO.Department> weChatDepartmentList, String unionCorpId, Set<String> thirdOrgUnitIdSet, String companyId) {
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        weChatDepartmentList.forEach(weChatDepartment -> {
            String thirdOrgUnitId = unionCorpId.concat("/").concat(weChatDepartment.getDepartmentId());
            if (!thirdOrgUnitIdSet.contains(thirdOrgUnitId)) {
                thirdOrgUnitIdSet.add(thirdOrgUnitId);
                departmentList.add(OpenThirdOrgUnitDTO.builder()
                        .thirdOrgUnitId(thirdOrgUnitId)
                        .thirdOrgUnitName(weChatDepartment.getDepartmentName())
                        .thirdOrgUnitParentId(unionCorpId.concat("/").concat(weChatDepartment.getParentid()))
                        .companyId(companyId)
                        .build());
            } else {
                log.warn("企业微信互联企业部门权限范围设置重复:{},", JsonUtils.toJson(weChatDepartment));
            }

        });
        return departmentList;
    }

    /**
     * 互联企业封装人员数据
     */
    private List<OpenThirdEmployeeDTO> unionEmployeePackage(WeChatLinkedCorpUserlistDTO weChatLinkedCorpUserlistDTO, Set<String> thirdEmployeePhoneSet, String companyId) {
        List<OpenThirdEmployeeDTO> openThirdEmployeeDTOList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(weChatLinkedCorpUserlistDTO) && !ObjectUtils.isEmpty(weChatLinkedCorpUserlistDTO.getUserlist())) {
            weChatLinkedCorpUserlistDTO.getUserlist().forEach(user -> {
                String phone = StringUtils.isBlank(user.getMobile()) ? null : org.apache.commons.lang3.StringUtils.normalizeSpace(user.getMobile()).substring(org.apache.commons.lang3.StringUtils.normalizeSpace(user.getMobile()).length() - 11);
                if (!thirdEmployeePhoneSet.contains(phone) || phone == null) {
                    if (phone != null) {
                        thirdEmployeePhoneSet.add(phone);
                    }
                    Map<String, String> extMap = new HashMap<>();
                    if (!ObjectUtils.isEmpty(user.getExtattr().getAttrs())) {
                        extMap = user.getExtattr().getAttrs().stream().collect(Collectors.toMap(t -> t.getName(), t -> t.getValue(), (o, n) -> n));
                    }
                    openThirdEmployeeDTOList.add(OpenThirdEmployeeDTO.builder()
                            .companyId(companyId)
                            .thirdEmployeeId(user.corpid.concat("/").concat(user.getUserid()))
                            .thirdEmployeeName(user.getName())
                            .thirdDepartmentId(user.getDepartment().get(0))
                            .thirdEmployeePhone(phone)
                            .thirdEmployeeEmail(user.getEmail())
                            // 权限字段默认名为 "分贝权限" 其他名称获取请写脚本获取,
                            .thirdEmployeeRoleTye(ObjectUtils.isEmpty(extMap) ? null : StringUtils.isBlank(extMap.get("分贝权限")) ? "-1" : extMap.get("分贝权限"))
                            .thirdEmployeeIdCard(ObjectUtils.isEmpty(extMap) ? null : extMap.get("身份证号"))
                            .status(1)
                            .build());
                } else {
                    log.warn("企业微信互联企业人员手机号已经存在:{}", JsonUtils.toJson(user));
                }
            });
        }
        return openThirdEmployeeDTOList;
    }

    /**
     * 获取联合企业主公司部门集合
     */
    private List<OpenThirdOrgUnitDTO> unionMainDepPackage(WechatDepartmentListRespDTO departmentListResp, String companyId, String corpId) {
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(departmentListResp) && !ObjectUtils.isEmpty(departmentListResp.getDepartmentList())) {
            departmentListResp.getDepartmentList().forEach(t -> {
                departmentList.add(OpenThirdOrgUnitDTO.builder()
                        .thirdOrgUnitId(StringUtils.obj2str(t.getId()))
                        .thirdOrgUnitName(t.getName())
                        .thirdOrgUnitParentId("0".equals(StringUtils.obj2str(t.getParentId())) ? corpId : StringUtils.obj2str(t.getParentId()))
                        .companyId(companyId)
                        .build());
            });
        }
        return departmentList;
    }

    /**
     * 1 清空中间表
     * 2 初始化UC信息到中间表
     * 3 根据手机号获取三方id，并绑定
     *
     * @param corpId 三方企业id
     */
    @Async
    public void transferInitOrg(String corpId) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (ObjectUtils.isEmpty(corpDefinition)) {
            log.warn("未找到对应的企业corpId:{}", corpId);
            return;
        }
        // 1 清空中间表
        clearEmployeeAndDept(corpDefinition.getAppId());
        // 2 初始化UC员工信息到中间表
        openSyncThirdOrgService.init(OpenType.WECHAT_EIA.getType(), corpDefinition.getAppId());
        // 3 获取三方id，并绑定
        bindThird(corpDefinition);
    }

    /**
     * 清空公司下面的所有员工和部门中间表
     *
     * @param companyId 公司id
     */
    private void clearEmployeeAndDept(String companyId) {
        openThirdOrgUnitDao.deleteOpenThirdOrgUnit(companyId,OpenType.WECHAT_EIA.getType());
        openThirdEmployeeDao.deleteOpenThirdEmployee(companyId, OpenType.WECHAT_EIA.getType());
    }

    /**
     * 1 获取token
     * 2 组装人员信息
     * 3 调用support层同步组织架构方法
     *
     * @param corpDefinition 公司信息
     * @return
     */
    public String bindThird(PluginCorpDefinition corpDefinition) {
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, corpDefinition.getThirdAdminId());
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                long start = System.currentTimeMillis();
                // 1 获取token
                String wechatToken = wechatTokenService.getWeChatContactToken(corpDefinition.getAppId());
                // 2 组装人员信息
                List<OpenThirdEmployeeDTO> employeeList = getEmployeeByPhone(corpDefinition, wechatToken);
                // 3 调用support层同步组织架构方法
                openSyncThirdOrgService.syncThird(OpenType.WECHAT_EIA.getType(), corpDefinition.getAppId(), new ArrayList<>(), employeeList);
                long end = System.currentTimeMillis();
                log.info("同步完成，用时{}分钟{}秒...", (end - start) / 60000, ((end - start) % 60000) / 1000);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.warn("未获取到锁，corpId={}", corpDefinition.getThirdCorpId());
            throw new ArgumentException("未获取到锁");
        }
        //返回成功
        return "Success";
    }

    /**
     * 1 从人员中间表获取所有员工信息，得到手机号
     * 2 根据手机号去微信拿到三方id
     *
     * @param corpDefinition 公司信息
     * @param wechatToken    token
     * @return
     */
    private List<OpenThirdEmployeeDTO> getEmployeeByPhone(PluginCorpDefinition corpDefinition, String wechatToken) {
        List<OpenThirdEmployee> openThirdEmployeeList = openThirdEmployeeDao.listEmployeeByCompanyIdAndOpenType(OpenType.WECHAT_EIA.getType(), corpDefinition.getAppId());
        if (ObjectUtils.isEmpty(openThirdEmployeeList)) {
            return new ArrayList<>();
        }
        //根据手机号查询不到信息的微信员工
        List<OpenThirdEmployee> unknownPhoneEmployeeList = new ArrayList<>();
        //根据手机号能查询到信息的微信员工
        List<OpenThirdEmployee> employeeList = new ArrayList<>();
        openThirdEmployeeList.stream().filter(Objects::nonNull).forEach(
            openThirdEmployee -> {
                if (StringUtils.isBlank(openThirdEmployee.getThirdEmployeePhone())) {
                    unknownPhoneEmployeeList.add(openThirdEmployee);
                } else {
                    WeChatUserIdGetRespDTO weChatUserDTO = pluginCallWeChatEiaService.getWeChatUserId(wechatToken, openThirdEmployee.getThirdEmployeePhone());
                    if (ObjectUtils.isEmpty(weChatUserDTO) || StringUtils.isTrimBlank(weChatUserDTO.getUserid())) {
                        log.info("根据人员手机号查询企微三方人员id失败，corpId:{},phone:{}", corpDefinition.getThirdCorpId(), openThirdEmployee.getThirdEmployeePhone());
                        unknownPhoneEmployeeList.add(openThirdEmployee);
                    } else {
                        openThirdEmployee.setThirdEmployeeId(weChatUserDTO.getUserid());
                        employeeList.add(openThirdEmployee);
                    }
                }
            }
        );
        if (!ObjectUtils.isEmpty(unknownPhoneEmployeeList)) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error_count", unknownPhoneEmployeeList.size() + "");
            String errorMsg = String.join("；", unknownPhoneEmployeeList.stream().map(e -> "姓名" + ":" + e.getThirdEmployeeName() + "," + "手机号：" + e.getThirdEmployeePhone()).collect(Collectors.toList()));
            errorMap.put("error_msg", errorMsg);
            log.info("以下存量员工因为手机号在微信中未查询到，同步失败，corpId:{},errorMsg:{}", corpDefinition.getThirdCorpId(), JsonUtils.toJson(errorMap));
        }
        return employeeList.stream()
            .map(
                openThirdEmployee ->
                    OpenThirdEmployeeDTO
                        .builder()
                        .companyId(openThirdEmployee.getCompanyId())
                        .thirdEmployeeId(openThirdEmployee.getThirdEmployeeId())
                        .thirdEmployeePhone(openThirdEmployee.getThirdEmployeePhone())
                        .build()
            )
            .collect(Collectors.toList());
    }
}



