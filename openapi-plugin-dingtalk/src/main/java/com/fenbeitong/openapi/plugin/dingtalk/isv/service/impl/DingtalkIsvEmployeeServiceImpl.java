package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvEmployeeService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvOrganizationService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvRoleService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenSyncThirdOrgServiceImpl;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lizhen
 * @date 2020/7/14
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvEmployeeServiceImpl extends AbstractEmployeeService implements IDingtalkIsvEmployeeService {

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Autowired
    private DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Autowired
    private IDingtalkIsvOrganizationService dingtalkIsvOrganizationService;

    @Autowired
    private OpenSyncThirdOrgServiceImpl openSyncThirdOrgService;

    @Autowired
    private IDingtalkIsvRoleService dingtalkIsvRoleService;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private IDingtalkIsvEmployeeService dingtalkIsvEmployeeService;

    /**
     * 全量同步部门人员
     *
     * @param corpId
     */
    @Override
    public void syncOrgEmployee(String corpId) {
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        String companyId = dingtalkIsvCompany.getCompanyId();
        //获取授权范围
        OapiAuthScopesResponse authScope = getAuthScope(corpId);
        List<Long> authedDept = authScope.getAuthOrgScopes().getAuthedDept();
        List<String> authedUser = authScope.getAuthOrgScopes().getAuthedUser();
        //获取全量部门
        List<OapiDepartmentListResponse.Department> allDepartments = dingtalkIsvOrganizationService.getAllDepartments(authedDept, corpId, dingtalkIsvCompany.getCompanyName());
        //获取全量人员
        List<OapiUserListbypageResponse.Userlist> allUserInfos = getAllUserInfos(authedUser, allDepartments, corpId);
        //获取管理员列表
        //List<String> adminList = getAdmin(corpId);
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        // 先查一下配置表 看是否需要过滤
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
        Map employeeCustomizeAttributeMap = openSysConfigService.getEmployeeConfigByIdAndType(companyId, OpenSysConfigType.EMPLOYEE_CUSTOMIZE_ATTRIBUTE.getType());
        //转换部门
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        for (OapiDepartmentListResponse.Department departmentInfo : allDepartments) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            openThirdOrgUnitDTO.setCompanyId(companyId);
            openThirdOrgUnitDTO.setThirdOrgUnitFullName(departmentInfo.getSourceIdentifier());
            openThirdOrgUnitDTO.setThirdOrgUnitName(departmentInfo.getName());
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(StringUtils.obj2str(departmentInfo.getParentid()));
            openThirdOrgUnitDTO.setThirdOrgUnitId(StringUtils.obj2str(departmentInfo.getId()));
            if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
                openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
            }
            if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
                openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
            }
            // 判断设置部门主管配置是否为空
            if (!ObjectUtils.isEmpty(openSysConfig)) {
                // 查询部门主管
                if (departmentInfo.getId() != 1 || authedDept.contains(1L)) {
                    openThirdOrgUnitDTO.setOrgUnitMasterIds(getDepMasters(corpId, StringUtils.obj2str(departmentInfo.getId())));
                }
            }
            departmentList.add(openThirdOrgUnitDTO);
        }
        //转换人员
        boolean employeeNeedFilter = employeeConfig != null;
        //查询花名册信息
        OpenMsgSetup openMsgSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, ItemCodeEnum.DINGTALK_ROSTER_CONFIG.getCode());
        Map<String, Map<String, String>> userIdFieldMap = new HashMap<>();
        if (openMsgSetup != null && !StringUtils.isTrimBlank(openMsgSetup.getStrVal1()) && employeeNeedFilter) {
            userIdFieldMap = dingtalkIsvEmployeeService.batchGetSmartHrmEmployee(
                corpId,
                dingtalkIsvCompany.getAgentid(),
                allUserInfos.stream().map(OapiUserListbypageResponse.Userlist::getUserid).collect(Collectors.toList()),
                Arrays.asList(openMsgSetup.getStrVal1().split(",")));
        }
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        for (OapiUserListbypageResponse.Userlist userInfo : allUserInfos) {
            OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
            openThirdEmployeeDTO.setCompanyId(companyId);
            openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getUserid());
            openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
            openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
            openThirdEmployeeDTO.setThirdEmployeePhone(userInfo.getMobile());
            List<Long> department = userInfo.getDepartment();
            String departmentId = "";
            if (department == null || department.size() <= 0) {
                departmentId = corpId;
            } else {
                departmentId = userInfo.getDepartment().get(0).toString();
            }
            if ("1".equals(departmentId)) {
                openThirdEmployeeDTO.setThirdDepartmentId(corpId);
            } else {
                openThirdEmployeeDTO.setThirdDepartmentId(departmentId);
            }
            String extattr = userInfo.getExtattr();
            //自定义字段
            parseEmployeeCustomeAttr(employeeCustomizeAttributeMap , extattr , openThirdEmployeeDTO);
            OpenThirdEmployeeDTO targetDTO = null;
            if (employeeNeedFilter) {
                targetDTO = employeeBeforeSyncFilter(employeeConfig, userInfo, openThirdEmployeeDTO,userIdFieldMap.get(userInfo.getUserid()));
                log.info("经过脚本处理后的员工数据，targetDTO:{}",JsonUtils.toJson(targetDTO));
            }
            employeeList.add(targetDTO != null ? targetDTO : openThirdEmployeeDTO);
        }
        //同步
        openSyncThirdOrgService.syncThird(OpenType.DINGTALK_ISV.getType(), companyId, departmentList, employeeList);
        // 判断设置部门主管配置是否为空
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            openSyncThirdOrgService.setAllDepManagePackV2(departmentList, companyId);
        }
        dingtalkIsvRoleService.listRole(corpId);
    }

    /**
     * 人员同步前按需过滤
     *
     * @param employeeConfig
     * @param userInfo
     * @param openThirdEmployeeDTO
     * @return
     */
    @Override
    public <T> OpenThirdEmployeeDTO employeeBeforeSyncFilter(OpenThirdScriptConfig employeeConfig, T userInfo, OpenThirdEmployeeDTO openThirdEmployeeDTO,Map<String,String> hrmFieldMap) {

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userInfo", userInfo);
            put("openThirdEmployeeDTO",openThirdEmployeeDTO);
            put("hrmFieldMap",hrmFieldMap);
        }};
        if (org.apache.commons.lang3.StringUtils.isNotBlank(employeeConfig.getParamJson()) && JsonUtils.toObj(employeeConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(employeeConfig.getParamJson(), Map.class));
        }
        return (OpenThirdEmployeeDTO) EtlUtils.execute(employeeConfig.getScript(), params);
    }

    //人员拓展属性映射转换
    @Override
    public void parseEmployeeCustomeAttr(Map employeeCustomizeAttributeMap, String extattr , OpenThirdEmployeeDTO openThirdEmployeeDTO){
        Map<String, Object> mapResult = new HashMap<>();
        if (!StringUtils.isBlank( extattr )  && !MapUtils.isBlank(employeeCustomizeAttributeMap)) {
            Map<String,Object> extattrMap = JsonUtils.toObj( extattr , Map.class);
            for (String key : extattrMap.keySet()) {
                employeeCustomizeAttributeMap.forEach((entryKey, entryValue) -> {
                    if (entryKey.equals(key)) {
                        mapResult.put(entryValue.toString(), extattrMap.get(key));
                    }
                });
            }
            if (!MapUtils.isBlank(mapResult)) {
                openThirdEmployeeDTO.setExtAttr(mapResult);
            }
        }

    }


    /**
     * 获取钉钉用户信息
     *
     * @param userId
     * @param corpId
     * @return
     */
    @Override
    public OapiUserGetResponse getUserInfo(String userId, String corpId) {
        String url = dingtalkHost + "user/get";
        OapiUserGetRequest request = new OapiUserGetRequest();
        request.setUserid(userId);
        request.setHttpMethod("GET");
        OapiUserGetResponse oapiUserGetResponse = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return oapiUserGetResponse;
    }

    /**
     * 通讯录授权范围
     *
     * @param corpId
     * @return
     */
    @Override
    public OapiAuthScopesResponse getAuthScope(String corpId) {
        String url = dingtalkHost + "auth/scopes";
        OapiAuthScopesRequest request = new OapiAuthScopesRequest();
        request.setHttpMethod("GET");
        OapiAuthScopesResponse oapiAuthScopesResponse = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return oapiAuthScopesResponse;
    }


    /**
     * 获取部门用户
     *
     * @param deptId
     * @param corpId
     */
    public List<OapiUserListbypageResponse.Userlist> getUserList(Long deptId, String corpId, Long offset) {
        String url = dingtalkHost + "user/listbypage";
        OapiUserListbypageRequest request = new OapiUserListbypageRequest();
        request.setDepartmentId(deptId);
        request.setOffset(offset);
        request.setSize(100L);
        request.setOrder("custom");
        request.setHttpMethod("GET");
        List<OapiUserListbypageResponse.Userlist> userlistAll = new ArrayList<>();
        //根部门未授权时手动添加，但获取人员会报{"errcode":50004,"errmsg":"请求的部门id不在授权范围内"}，故不抛出一场，手动判断
        OapiUserListbypageResponse oapiUserListbypageResponse = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        if (!oapiUserListbypageResponse.isSuccess()) {
            return userlistAll;
        }
        List<OapiUserListbypageResponse.Userlist> userlist = oapiUserListbypageResponse.getUserlist();
        if (!ObjectUtils.isEmpty(userlist)) {
            userlistAll.addAll(userlist);
        }
        Boolean hasMore = oapiUserListbypageResponse.getHasMore();
        if (hasMore) {
            List<OapiUserListbypageResponse.Userlist> subUserlist = getUserList(deptId, corpId, offset + 1);
            userlistAll.addAll(subUserlist);
        }
        return userlistAll;
    }


    /**
     * 获取所有人员
     *
     * @param departmentInfos
     * @param corpId
     */
    public List<OapiUserListbypageResponse.Userlist> getAllUserInfos(List<String> authedUser, List<OapiDepartmentListResponse.Department> departmentInfos, String corpId) {
        List<OapiUserListbypageResponse.Userlist> userInfoAll = new ArrayList<>();
        //授权部门下的人员
        for (OapiDepartmentListResponse.Department department : departmentInfos) {
            Long deptId = department.getId();
            List<OapiUserListbypageResponse.Userlist> userList = getUserList(deptId, corpId, 0L);
            userInfoAll.addAll(userList);
        }
        //授权的人员
        if (!ObjectUtils.isEmpty(authedUser)) {
            //部门id集合，人员部门id不在集合中的，挂根部门
            List<Long> departmentIds = departmentInfos.stream().map(OapiDepartmentListResponse.Department::getId).collect(Collectors.toList());
            for (String userId : authedUser) {
                OapiUserGetResponse userInfo = getUserInfo(userId, corpId);
                if (!ObjectUtils.isEmpty(userId)) {
                    OapiUserListbypageResponse.Userlist user = new OapiUserListbypageResponse.Userlist();
                    user.setUserid(userId);
                    user.setName(userInfo.getName());
                    user.setEmail(userInfo.getEmail());
                    user.setMobile(userInfo.getMobile());
                    List<Long> userDepartmentIds = userInfo.getDepartment();
                    Iterator<Long> iterator = userDepartmentIds.iterator();
                    while (iterator.hasNext()) {
                        Long item = iterator.next();
                        if (!departmentIds.contains(item)) {
                            iterator.remove();
                        }
                    }
                    if (!ObjectUtils.isEmpty(userDepartmentIds)) {
                        user.setDepartment(userDepartmentIds);
                    } else {
                        userDepartmentIds.add(1l);
                        user.setDepartment(userDepartmentIds);
                    }
                    userInfoAll.add(user);
                }
            }
        }
        //人员去重
        List<OapiUserListbypageResponse.Userlist> distinctList = userInfoAll
                .stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getUserid()))), ArrayList::new))
                .stream().sorted(Comparator.comparing(OapiUserListbypageResponse.Userlist::getUserid)).collect(Collectors.toList());
        return distinctList;
    }


    /**
     * 获取管理员列表
     *
     * @param corpId
     */
    public List<String> getAdmin(String corpId) {
        List<String> adminList = new ArrayList<>();
        String url = dingtalkHost + "user/get_admin";
        OapiUserGetAdminRequest request = new OapiUserGetAdminRequest();
        OapiUserGetAdminResponse response = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        if (!response.isSuccess()) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, response.getMessage());
        } else {
            List<OapiUserGetAdminResponse.AdminList> adminListResult = response.getAdminList();
            adminList = adminListResult.stream().map(OapiUserGetAdminResponse.AdminList::getUserid).collect(Collectors.toList());
        }

        return adminList;
    }

    /**
     * 获取部门负责人
     */
    public String getDepMasters(String corpId, String depId) {
        String url = dingtalkHost + "/department/get";
        OapiDepartmentGetRequest request = new OapiDepartmentGetRequest();
        request.setId(depId);
        OapiDepartmentGetResponse response = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        if (!response.isSuccess()) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, response.getMessage());
        } else {
            String deptManagerUseridList = response.getDeptManagerUseridList();
            return deptManagerUseridList.replaceAll("\\|", ",");
        }
    }

    @Override
    public void syncOrgManagers(String corpId) {
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        String companyId = dingtalkIsvCompany.getCompanyId();
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            OapiAuthScopesResponse authScope = getAuthScope(corpId);
            List<Long> authedDept = authScope.getAuthOrgScopes().getAuthedDept();
            //获取全量部门
            List<OapiDepartmentListResponse.Department> allDepartments = dingtalkIsvOrganizationService.getAllDepartments(authedDept, corpId, dingtalkIsvCompany.getCompanyName());
            List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
            for (OapiDepartmentListResponse.Department departmentInfo : allDepartments) {
                OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
                openThirdOrgUnitDTO.setCompanyId(companyId);
                openThirdOrgUnitDTO.setThirdOrgUnitParentId(StringUtils.obj2str(departmentInfo.getParentid()));
                openThirdOrgUnitDTO.setThirdOrgUnitId(StringUtils.obj2str(departmentInfo.getId()));
                if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
                    openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
                }
                // 查询部门主管
                if (departmentInfo.getId() != 1 || authedDept.contains(1L)) {
                    openThirdOrgUnitDTO.setOrgUnitMasterIds(getDepMasters(corpId, StringUtils.obj2str(departmentInfo.getId())));
                }
                departmentList.add(openThirdOrgUnitDTO);
            }
            openSyncThirdOrgService.setAllDepManagePackV2(departmentList, companyId);
        } else {
            throw new FinhubException(0, "企业:" + companyId + "未开放同步部门主管功能，请检查配置是否正确");
        }
    }

    @Override
    public OapiSmartworkHrmEmployeeV2ListResponse getSmartHrmEmployeeList(String corpId, Long agentId, String userIdList, String fieldFilterList) {
        String url = dingtalkHost + "topapi/smartwork/hrm/employee/v2/list";
        OapiSmartworkHrmEmployeeV2ListRequest request = new OapiSmartworkHrmEmployeeV2ListRequest();
        request.setAgentid(agentId);
        request.setUseridList(userIdList);
        request.setFieldFilterList(fieldFilterList);
        return dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
    }

    @Override
    public Map<String, Map<String, String>> batchGetSmartHrmEmployee(String corpId, Long agentId, List<String> userIdList, List<String> fieldFilterList) {
        Map<String, Map<String, String>> userIdFiledMap = new HashMap<>();
        try {
            Optional.ofNullable(userIdList).ifPresent(
                userIds -> CollectionUtils.batch(userIds, 100).forEach(batch -> {
                    OapiSmartworkHrmEmployeeV2ListResponse response = getSmartHrmEmployeeList(
                        corpId,
                        agentId,
                        StringUtils.joinStr(",", batch),
                        StringUtils.joinStr(",", fieldFilterList));
                    if (response.isSuccess()) {
                        Optional.ofNullable(response.getResult())
                            .ifPresent(empRosterFieldVos -> empRosterFieldVos.stream()
                                .filter(Objects::nonNull)
                                .forEach(empRosterFieldVo -> {
                                    if (StringUtils.isTrimBlank(empRosterFieldVo.getUserid()) || CollectionUtils.isBlank(empRosterFieldVo.getFieldDataList())) {
                                        return;
                                    }
                                    String userId = empRosterFieldVo.getUserid();
                                    Map<String, String> fieldMap = empRosterFieldVo.getFieldDataList().stream().collect(Collectors.toMap(
                                        OapiSmartworkHrmEmployeeV2ListResponse.EmpFieldDataVo::getFieldCode,
                                        fieldData -> Optional.ofNullable(fieldData.getFieldValueList())
                                            .map(fieldValueVos -> fieldValueVos.get(0))
                                            .map(OapiSmartworkHrmEmployeeV2ListResponse.FieldValueVo::getValue)
                                            .orElse(""),
                                        (k1, k2) -> k2));
                                    userIdFiledMap.put(userId, fieldMap);
                                }));
                    }
                })
            );

        } catch (Exception e) {
            log.info("钉钉市场版 花名册数据转化异常", e);
            return userIdFiledMap;
        }
        return userIdFiledMap;
    }

}
