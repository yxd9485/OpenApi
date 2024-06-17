package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.*;
import com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3.FeiShuBatchUserListDetailV3RespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3.FeiShuSingleUserDetailRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3.FeiShuSubDepartmentListRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.eia.dto.FeiShuEhrV1EmployeesDTO;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaOrganizationService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdExpandFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdExpandFieldConfig;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenSyncThirdOrgServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.PhoneCheckUtil;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeBaseInfo;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 飞书人员service
 *
 * @author lizhen
 * @date 2020/6/1
 */
@ServiceAspect
@Service
@Slf4j
public abstract class AbstractFeiShuEmployeeService extends AbstractEmployeeService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Autowired
    private OpenSyncThirdOrgServiceImpl openSyncThirdOrgService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private FeiShuEiaOrganizationService feiShuEiaOrganizationService;

    @Autowired
    private OpenThirdExpandFieldConfigDao expandFieldConfigDao;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;

    // 身份证号
    private static final String ID_NO = "id_no";

    /**
     * 查询飞书用信信息
     *
     * @param id
     * @param corpId
     * @return
     */
    public FeiShuUserInfoDTO getUserInfo(String idType, String id, String corpId) {
        FeiShuUserBatchGetRespDTO feiShuUserBatchGetRespDTO = userBatchGet(idType, Lists.newArrayList(id), corpId);
        List<FeiShuUserInfoDTO> userInfos = feiShuUserBatchGetRespDTO.getData().getUserInfos();
        if (!ObjectUtils.isEmpty(userInfos)) {
            return userInfos.get(0);
        }
        return null;
    }

    /**
     * 批量获取用户信息
     *
     * @param openIdList
     * @param corpId
     * @return
     */
    public FeiShuUserBatchGetRespDTO userBatchGet(String idType, List<String> openIdList, String corpId) {
        String url = feishuHost + FeiShuConstant.USER_BATCH_GET;
        StringBuffer idsSb = new StringBuffer();
        for (String id : openIdList) {
            if (idsSb.length() != 0) {
                idsSb.append("&");
            }
            idsSb.append(idType + "=").append(id);
        }
        url = url + idsSb.toString();
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, null, corpId);
        FeiShuUserBatchGetRespDTO feiShuUserBatchGetRespDTO = JsonUtils.toObj(res, FeiShuUserBatchGetRespDTO.class);
        if (feiShuUserBatchGetRespDTO == null || 0 != feiShuUserBatchGetRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_USER_BATCH_GET_FAILED);
        }
        return feiShuUserBatchGetRespDTO;
    }

    /**
     * 获取部门用户详情
     *
     * @param departmentId
     * @param corpId
     * @param pageToken
     * @return
     */
    public List<FeiShuUserInfoDTO> departmentUserDetailList(String departmentId, String corpId, String pageToken) {
        String url = feishuHost + FeiShuConstant.DEPARTMENT_USER_DETAIL_LIST_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("department_id", departmentId);
        param.put("page_size", 100);
        param.put("fetch_child", true);
        param.put("page_token", pageToken);
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, param, corpId);
        FeiShuDepartmentUserDetailListRespDTO feiShuDepartmentUserDetailListRespDTO = JsonUtils.toObj(res, FeiShuDepartmentUserDetailListRespDTO.class);
        if (feiShuDepartmentUserDetailListRespDTO == null || 0 != feiShuDepartmentUserDetailListRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_DEPARTMENT_USER_DETAIL_LIST_FAILED);
        }
        List<FeiShuUserInfoDTO> userInfosAll = feiShuDepartmentUserDetailListRespDTO.getData().getUserInfos();
        boolean hasMore = feiShuDepartmentUserDetailListRespDTO.getData().isHasMore();
        if (hasMore) {
            pageToken = feiShuDepartmentUserDetailListRespDTO.getData().getPageToken();
            List<FeiShuUserInfoDTO> userInfos = departmentUserDetailList(departmentId, corpId, pageToken);
            userInfosAll.addAll(userInfos);
        }
        return userInfosAll;
    }


    /**
     * 全量同步部门人员
     *
     * @param corpId
     */
    public void syncOrgEmployee(int openType, String corpId, String companyId, String companyName ) {
        //获取飞书全量部门
        List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> departmentInfos = getOrganizationService().getAllDepartments(corpId, companyName);
        //获取飞书全量人员
        List<FeiShuUserInfoDTO> userInfos = getAllUserInfosV2(departmentInfos, corpId, openType);

        //飞书人员追加飞书花名册字段
        getAddFieldFeiShuUserInfoDTOS(corpId, userInfos, companyId);

        //获取飞书配置的分贝通权限自定义字段,数据初始化之前需配置表数据
        List<OpenMsgSetup> feishuFbtPriv = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("company_feishu_fbt_priv"));
        String fbtPrivIdKey = "";
        if (!ObjectUtils.isEmpty(feishuFbtPriv)) {
            OpenMsgSetup openMsgSetup = feishuFbtPriv.get(0);
            Integer intVal1 = openMsgSetup.getIntVal1();
            if (intVal1 == 1) {//获取分贝通权限字段ID,用于获取飞书分贝通权限
                fbtPrivIdKey = openMsgSetup.getStrVal1();
            }
        }

        // 先查一下配置表 看是否需要过滤
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getEmployeeConfig(companyId);
        OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getDepartmentConfig(companyId);
        boolean employeeNeedFilter = employeeConfig != null;
        boolean departmentNeedFilter = departmentConfig != null;

        //转换部门
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        for (FeiShuDepartmentSimpleListRespDTO.DepartmentInfo departmentInfo : departmentInfos) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            openThirdOrgUnitDTO.setCompanyId(companyId);
            openThirdOrgUnitDTO.setThirdOrgUnitFullName(departmentInfo.getThirdOrgUnitFullName());
            openThirdOrgUnitDTO.setThirdOrgUnitName(departmentInfo.getName());
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(departmentInfo.getParentId());
            openThirdOrgUnitDTO.setThirdOrgUnitId(departmentInfo.getId());
            // 增加部门主管ID
            openThirdOrgUnitDTO.setOrgUnitMasterIds(departmentInfo.getLeaderUserId());
            if ("0".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
                openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
            }
            if ("0".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
                openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
            }
            OpenThirdOrgUnitDTO targetDTO = null;
            if (departmentNeedFilter) {
                targetDTO = feiShuEiaOrganizationService.departmentBeforeSyncFilter(departmentConfig, departmentInfo, openThirdOrgUnitDTO);
            }
            departmentList.add(targetDTO == null ? openThirdOrgUnitDTO : targetDTO);
        }
        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        for (FeiShuUserInfoDTO userInfo : userInfos) {
            OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
            openThirdEmployeeDTO.setCompanyId(companyId);
            openThirdEmployeeDTO.setThirdDepartmentId(userInfo.getDepartments().get(0));
            openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
            openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
            openThirdEmployeeDTO.setThirdEmployeePhone(PhoneCheckUtil.getMobileWithoutCountryCode(userInfo.getMobile()));
            openThirdEmployeeDTO.setThirdEmployeeGender(userInfo.getGender());
            openThirdEmployeeDTO.setEmployeeNumber(userInfo.getEmployeeNo());
            if ("0".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
                openThirdEmployeeDTO.setThirdDepartmentId(corpId);
            }
            if (OpenType.FEISHU_EIA.getType() == openType) {
                openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getEmployeeId());
            } else {
                openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getOpenId());
            }
            if (StringUtils.isNotBlank(fbtPrivIdKey)) {//不为空，进行分贝通权限字段设置
                Map<String, Object> customAttrs = userInfo.getCustomAttrs();
                if (!ObjectUtils.isEmpty(customAttrs)) {//如果人员没有配置分贝通权限，则拉取的详情数据中不会包含自定义字段内容
                    Map fbtPrivMap = (Map) customAttrs.get(fbtPrivIdKey);
                    if (!ObjectUtils.isEmpty(fbtPrivMap)) {
                        String fbtPriv = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(fbtPrivMap.get("value"));
                        if (StringUtils.isNotBlank(fbtPriv)) {//如果没有填写分贝通权限字段，则默认不进行权限同步，全部关闭
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
                        if (ID_NO.equals(v) && (customAttrs.get(k) != null)) {
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
            OpenThirdEmployeeDTO targetDTO = null;
            if (employeeNeedFilter) {
                targetDTO = employeeBeforeSyncFilter(employeeConfig, userInfo, openThirdEmployeeDTO);
            }
            employeeList.add(targetDTO == null ? openThirdEmployeeDTO : targetDTO);
        }
        //同步
        openSyncThirdOrgService.syncThird(openType, companyId, departmentList, employeeList);
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        // 判断设置部门主管配置时否为空
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            openSyncThirdOrgService.setAllDepManagePackV2(departmentList, companyId);
        }
    }

    /**
     * 人员同步前按需过滤
     *
     * @param employeeConfig
     * @param userInfo
     * @param openThirdEmployeeDTO
     * @return
     */
    public OpenThirdEmployeeDTO employeeBeforeSyncFilter(OpenThirdScriptConfig employeeConfig, FeiShuUserInfoDTO userInfo, OpenThirdEmployeeDTO openThirdEmployeeDTO) {

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userInfo", userInfo);
            put("openThirdEmployeeDTO",openThirdEmployeeDTO);
        }};
        if (StringUtils.isNotBlank(employeeConfig.getParamJson()) && JsonUtils.toObj(employeeConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(employeeConfig.getParamJson(), Map.class));
        }
        return (OpenThirdEmployeeDTO) EtlUtils.execute(employeeConfig.getScript(), params);
    }

    /**
     * 获取通讯录授权范围FeiShuResponseCode
     *
     * @param corpId
     */
    public FeiShuContactScopeReqDTO.ContactScope getContactScope(String corpId) {
        String url = feishuHost + FeiShuConstant.GET_CONTACT_SCOPT_URL;
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, null, corpId);
        FeiShuContactScopeReqDTO feiShuContactScopeReqDTO = JsonUtils.toObj(res, FeiShuContactScopeReqDTO.class);
        if (feiShuContactScopeReqDTO == null || 0 != feiShuContactScopeReqDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_CONTACT_SCOPE_FAILED);
        }
        return feiShuContactScopeReqDTO.getData();
    }


    /**
     * 获取企业所有可见范围内人员
     * 1.遍历授权部门
     * 2.获取可见范围选中人员、根部门人员
     * 3.去重
     *
     * @param corpId
     * @return
     */
    public List<FeiShuUserInfoDTO> getAllUserInfos(List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> departmentInfos, String corpId , int openType) {
        List<FeiShuUserInfoDTO> userInfos = new ArrayList<>();
        //1.遍历授权部门
        for (FeiShuDepartmentSimpleListRespDTO.DepartmentInfo departmentInfo : departmentInfos) {
            // 一级部门
            if (FeiShuConstant.ROOT_DEPARTMENT_CODE.equals(departmentInfo.getParentId())) {
                List<FeiShuUserInfoDTO> feiShuIsvUserInfos = departmentUserDetailList(departmentInfo.getId(), corpId, null);
                if (!ObjectUtils.isEmpty(feiShuIsvUserInfos)) {
                    userInfos.addAll(feiShuIsvUserInfos);
                }
            }
        }
        List<String> allDepartmentIds = departmentInfos.stream().map(d -> d.getId()).collect(Collectors.toList());
        //2.获取可见范围选中人员、根部门人员
        FeiShuContactScopeReqDTO.ContactScope contactScope = getContactScope(corpId);
        List<String> authedOpenIds = contactScope.getAuthedOpenIds();
        boolean isEia = OpenType.FEISHU_EIA.getType() == openType;
        if (authedOpenIds != null && authedOpenIds.size() > 0) {
            for (String authedOpenId : authedOpenIds) {
                FeiShuUserInfoDTO userInfo = null;
                if (isEia){
                    userInfo = getUserInfo(FeiShuConstant.ID_TYPE_OPEN_ID, authedOpenId, corpId);
                } else {
                    userInfo = getUserInfo(FeiShuConstant.ID_TYPE_OPEN_ID, authedOpenId, corpId);
                }
                if (userInfo != null) {
                    if (ObjectUtils.isEmpty(userInfo.getDepartments()) || !allDepartmentIds.contains(userInfo.getDepartments().get(0))) {
                        //无部门信息的放根部门
                        userInfo.setDepartments(Lists.newArrayList("0"));
                    }
                    userInfos.add(userInfo);
                }
            }
        }
        //3.人员去重
        List<FeiShuUserInfoDTO> distinctList = userInfos
                .stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getOpenId()))), ArrayList::new))
                .stream().sorted(Comparator.comparing(FeiShuUserInfoDTO::getOpenId)).collect(Collectors.toList());
        return distinctList;
    }

    public List<FeiShuUserInfoDTO> getAllUserInfosV2(List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> departmentInfos, String corpId , int openType) {
        List<FeiShuUserInfoDTO> userInfos = new ArrayList<>();
        List<FeiShuSubDepartmentListRespDTO.DataDTO.ItemsDTO> allDepartmentList = Lists.newArrayList();
        List<FeiShuSubDepartmentListRespDTO.DataDTO.ItemsDTO> allSubDepartmentList = Lists.newArrayList();
        // 1、获取所有部门列表
        for (FeiShuDepartmentSimpleListRespDTO.DepartmentInfo departmentInfo : departmentInfos) {
            if (FeiShuConstant.ROOT_DEPARTMENT_CODE.equals(departmentInfo.getParentId())) {
                FeiShuSubDepartmentListRespDTO.DataDTO.ItemsDTO departmentDTO = new FeiShuSubDepartmentListRespDTO.DataDTO.ItemsDTO();
                departmentDTO.setDepartmentId(departmentInfo.getId());
                allDepartmentList.add(departmentDTO);
                // 一级部门
                List<FeiShuSubDepartmentListRespDTO.DataDTO.ItemsDTO> subDepartmentLists = subDepartmentList(departmentInfo.getId(),corpId,null);
                if (CollectionUtils.isNotBlank(subDepartmentLists)){
                    allSubDepartmentList.addAll(subDepartmentLists);
                }
            } else {
                if (!FeiShuConstant.ROOT_DEPARTMENT_CODE.equals(departmentInfo.getId())){
                    FeiShuSubDepartmentListRespDTO.DataDTO.ItemsDTO departmentDTO = new FeiShuSubDepartmentListRespDTO.DataDTO.ItemsDTO();
                    departmentDTO.setDepartmentId(departmentInfo.getId());
                    allDepartmentList.add(departmentDTO);
                }
            }
        }
        // 所有授权的部门及其子部门,不包括根部门
        allDepartmentList.addAll(allSubDepartmentList);
        // 2、根据部门获取部门直属员工列表信息
        List<FeiShuBatchUserListDetailV3RespDTO.DataDTO.ItemsDTO> userInfoList = Lists.newArrayList();
        for (FeiShuSubDepartmentListRespDTO.DataDTO.ItemsDTO itemsDTO : allDepartmentList) {
            List<FeiShuBatchUserListDetailV3RespDTO.DataDTO.ItemsDTO> userDetailListV3 = departmentUserDetailListV3(itemsDTO.getDepartmentId(),corpId,null);
            if (CollectionUtils.isBlank(userDetailListV3)){
                continue;
            }
            userInfoList.addAll(userDetailListV3);
        }
        userInfos.addAll(FeiShuUserInfoDTO.Builder.convertNewUserDTO(userInfoList));
        List<String> allDepartmentIds = allDepartmentList.stream().map(d -> d.getDepartmentId()).collect(Collectors.toList());
        //2.获取可见范围选中人员、根部门人员
        FeiShuContactScopeReqDTO.ContactScope contactScope = getContactScope(corpId);
        boolean isEia = OpenType.FEISHU_EIA.getType() == openType;
        List<String> authedOpenIds = Lists.newArrayList();
        if (isEia){
            authedOpenIds = contactScope.getAuthedEmployeeIds();
        } else {
            authedOpenIds = contactScope.getAuthedOpenIds();
        }
        if (authedOpenIds != null && authedOpenIds.size() > 0) {
            for (String authedOpenId : authedOpenIds) {
                FeiShuUserInfoDTO userInfo = null;
                if( isEia ){
                    userInfo = getSingleUserDetailInfo(FeiShuConstant.EIA_USER_ID_TYPE, authedOpenId, corpId).buildSingleOldDTO();
                } else {
                    userInfo = getSingleUserDetailInfo(FeiShuConstant.ISV_USER_ID_TYPE, authedOpenId, corpId).buildSingleOldDTO();
                }
                if (userInfo != null) {
                    if (ObjectUtils.isEmpty(userInfo.getDepartments()) || !allDepartmentIds.contains(userInfo.getDepartments().get(0))) {
                        //无部门信息的放根部门
                        userInfo.setDepartments(Lists.newArrayList("0"));
                    }
                    userInfos.add(userInfo);
                }
            }
        }
        //3.人员去重
        List<FeiShuUserInfoDTO> distinctList = userInfos
            .stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getOpenId()))), ArrayList::new))
            .stream().sorted(Comparator.comparing(FeiShuUserInfoDTO::getOpenId)).collect(Collectors.toList());
        return distinctList;
    }


    public Map<String, Object> checkFeiShuEmployee(String companyId, String corpId, List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> departmentInfoList, int openType) {
        //查询分贝通全量人员数据
        List<EmployeeBaseInfo> fbtEmployeeList = listFbEmployee(companyId);
        List<String> thirdUserIdList = fbtEmployeeList.stream().filter(e -> !ObjectUtils.isEmpty(e.getThirdEmployeeId())).map(EmployeeBaseInfo::getThirdEmployeeId).collect(Collectors.toList());
        //查询飞书全量人员数据
        List<FeiShuUserInfoDTO> feishuUserInfoList = getAllUserInfosV2(departmentInfoList, corpId,openType);
        //比对部门不一致的人员数据
        Map<String, Map<String, String>> differentDepartmentMap = Maps.newHashMap();
        List<String> differentEmpIdList = Lists.newArrayList();
        for (EmployeeBaseInfo fbEmpInfo : fbtEmployeeList) {
            String thirdEmployeeId = fbEmpInfo.getThirdEmployeeId();
            if (StringUtils.isNotBlank(thirdEmployeeId)) {
                ThirdEmployeeRes thirdEmployeeRes = getEmployeeByThirdId(companyId, thirdEmployeeId);
                if (!ObjectUtils.isEmpty(thirdEmployeeRes)) {
                    String thirdOrgId = thirdEmployeeRes.getEmployee().getThird_org_id();
                    for (FeiShuUserInfoDTO feiShuUserInfoDTO : feishuUserInfoList) {
                        if (fbEmpInfo.getThirdEmployeeId().equals(feiShuUserInfoDTO.getEmployeeId())) {
                            String feishuDepartmentId = feiShuUserInfoDTO.getDepartments().get(0);
                            if (!thirdOrgId.equals(feishuDepartmentId)) {//同一个人分贝通部门ID和飞书部门ID不相同
                                Map<String, String> departmentMap = Maps.newHashMap();
                                departmentMap.put("分贝通人员姓名", fbEmpInfo.getName());
                                departmentMap.put("分贝通部门全名称", thirdEmployeeRes.getEmployee().getOrg_unit_name());
                                departmentMap.put("飞书人员姓名", feiShuUserInfoDTO.getName());
//                                departmentMap.put("飞书部门名称",feishuDepartmentId);
                                if (!feishuDepartmentId.equals("0")) {//根部门
                                    FeiShuDepartmentSimpleListRespDTO.DepartmentInfo departmentInfo = feiShuEiaOrganizationService.getDepartmentInfo(feishuDepartmentId, corpId);
                                    if (!ObjectUtils.isEmpty(departmentInfo)) {
                                        String thirdOrgUnitFullName = departmentInfo.getName();
                                        departmentMap.put("飞书部门名称", thirdOrgUnitFullName);
                                    }
                                }
                                differentDepartmentMap.put(fbEmpInfo.getThirdEmployeeId(), departmentMap);
                                differentEmpIdList.add(fbEmpInfo.getThirdEmployeeId());
                            }
                        }
                    }
                }
            }
        }
        List<FeiShuUserInfoDTO> unSyncUserList = feishuUserInfoList.stream().filter(e -> !thirdUserIdList.contains(e.getEmployeeId())).collect(Collectors.toList());
        Map<String, Object> resultMap = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(unSyncUserList)) {
            //查询部门不一致的人员数据
            resultMap.put("未同步至分贝通人员数量", unSyncUserList.size());
            List<String> unSyncUserIds = unSyncUserList.stream().map(e -> e.getEmployeeId()).collect(Collectors.toList());
            resultMap.put("未同步至分贝通人员数据", unSyncUserIds);
//            String errorUserIds = String.join(",", unSyncUserList.stream().map(u -> "'" + u.getEmployeeId() + "'").collect(Collectors.toList()));
//            resultMap.put("error_user_ids", "(" + errorUserIds + ")");
            String errorMsg = "以下人员未同步到分贝通:" + String.join(",", unSyncUserList.stream().map(e -> e.getEmployeeId() + ":" + e.getName() + ":" + (ObjectUtils.isEmpty(e.getMobile()) ? null : e.getMobile())).collect(Collectors.toList()));
            resultMap.put("未同步至分贝通人员错误信息", errorMsg);
            resultMap.put("一个人不同部门数据", differentDepartmentMap);
            resultMap.put("一个人不同部门ID集合", differentEmpIdList);
        }
        return resultMap;
    }


    /**
     * 获取企业所有可见范围内人员
     * 1.遍历授权部门
     * 2.获取可见范围选中人员、根部门人员
     * 3.去重
     *
     * @param corpId
     * @return
     */
    public Set<String> getAllUserOpenIds(List<String> departmentInfos, String corpId) {
        Set<String> openIds = new HashSet<>();
        //1.遍历授权部门
        for (String departmentId : departmentInfos) {
            // 一级部门
            //if (FeiShuConstant.ROOT_DEPARTMENT_CODE.equals(departmentInfo.getParentId())) {
            List<FeiShuUserInfoDTO> feiShuIsvUserInfos = departmentUserIdsList(departmentId, corpId, null);
            if (!ObjectUtils.isEmpty(feiShuIsvUserInfos)) {
                List<String> userOpenIds = feiShuIsvUserInfos.stream().map(FeiShuUserInfoDTO::getOpenId).collect(Collectors.toList());
                openIds.addAll(userOpenIds);
            }
            // }
        }
        //2.获取可见范围选中人员、根部门人员
        FeiShuContactScopeReqDTO.ContactScope contactScope = getContactScope(corpId);
        List<String> authedOpenIds = contactScope.getAuthedOpenIds();
        openIds.addAll(authedOpenIds);
        //3.人员去重
//        List<FeiShuUserInfoDTO> distinctList = userInfos
//                .stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(string))), ArrayList::new))
//                .stream().sorted(Comparator.comparing(FeiShuUserInfoDTO::getOpenId)).collect(Collectors.toList());
        return openIds;
    }


    /**
     * * 获取企业所有可见范围内人员
     * 1.遍历授权部门
     * 2.获取可见范围选中人员、根部门人员
     * 3.去重
     *
     * @return
     */
    public List<String> getAllUserOpenIdsFromOpenThirdEmployee(int openType, String companyId) {
        List<OpenThirdEmployee> openThirdEmployeeList = openThirdEmployeeDao.listEmployeeByCompanyIdAndOpenType(openType, companyId);
        List<String> orderIds = openThirdEmployeeList.stream().map(OpenThirdEmployee::getThirdEmployeeId).collect(Collectors.toList());
        return orderIds;
    }


    /**
     * 递归获取部门用户id
     * zwj
     *
     * @param departmentId
     * @param corpId
     * @param pageToken
     * @return
     */
    public List<FeiShuUserInfoDTO> departmentUserIdsList(String departmentId, String corpId, String pageToken) {
        String url = feishuHost + FeiShuConstant.DEPARTMENT_USER_LIST_URL;
        url = url.replaceAll(" ","");
        Map<String, Object> param = new HashMap<>();
        param.put("department_id", departmentId);
        param.put("page_size", 100);
        param.put("fetch_child", true);
        param.put("page_token", pageToken);
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, param, corpId);
        FeiShuDepartmentListRespDTO feiShuDepartmentUserDetailListRespDTO = JsonUtils.toObj(res, FeiShuDepartmentListRespDTO.class);
        if (feiShuDepartmentUserDetailListRespDTO == null || 0 != feiShuDepartmentUserDetailListRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_DEPARTMENT_USER_DETAIL_LIST_FAILED);
        }
        List<FeiShuUserInfoDTO> userInfosAll = feiShuDepartmentUserDetailListRespDTO.getData().getUserInfos();
        boolean hasMore = feiShuDepartmentUserDetailListRespDTO.getData().isHasMore();
        if (hasMore) {
            pageToken = feiShuDepartmentUserDetailListRespDTO.getData().getPageToken();
            List<FeiShuUserInfoDTO> userInfos = departmentUserIdsList(departmentId, corpId, pageToken);
            userInfosAll.addAll(userInfos);
        }
        return userInfosAll;
    }

    /**
     * 获取飞书考勤记录
     * @param userIds
     * @param checkDateFrom
     * @param checkDateTo
     * @param corpId
     * @return
     */
    public List<FeishuAttendanceRespDTO.TaskResult> getCheckRecordInfoList(List<String> userIds, int checkDateFrom, int checkDateTo, String corpId) {
        String url = feishuHost + FeiShuConstant.GET_ATTENDANCE_RECORD;
        url = url.replaceAll(" ","");
        Map<String, Object> param = new HashMap<>();
        param.put("user_ids", userIds);
        param.put("check_date_from", checkDateFrom);
        param.put("check_date_to", checkDateTo);
        String res = getFeiShuHttpUtils().postJsonWithTenantAccessToken(url, JsonUtils.toJson(param), corpId);
        FeishuAttendanceRespDTO feishuAttendanceRespDTO = JsonUtils.toObj(res, FeishuAttendanceRespDTO.class);
        if (feishuAttendanceRespDTO == null || 0 != feishuAttendanceRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_ATTENDANCE_RECORD_FAILED);
        }
        List<FeishuAttendanceRespDTO.TaskResult> taskResultList = feishuAttendanceRespDTO.getData().getUser_task_results();
        return taskResultList;
    }

    /**
     * 获取飞书考勤组详情
     * @param groupId
     * @param corpId
     * @return
     */
    public FeishuAttendanceGroupDetail.GroupDetail getAttendanceDetail(String groupId, String corpId ) {
        String url = feishuHost + String.format(FeiShuConstant.GET_ATTENDANCE_GROUP_INFO, groupId);
        url = url.replaceAll(" ","");
        Map<String, Object> param = new HashMap<>();
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, param, corpId);
        FeishuAttendanceGroupDetail attendanceGroupDetail = JsonUtils.toObj(res, FeishuAttendanceGroupDetail.class);
        if (attendanceGroupDetail == null || 0 != attendanceGroupDetail.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_ATTENDANCE_GROUP_DETAIL_INFO_FAILED);
        }
        return attendanceGroupDetail.getData();
    }

    /**
     * 获取FeiShuHttpUtils
     *
     * @return
     */
    protected abstract AbstractFeiShuHttpUtils getFeiShuHttpUtils();

    protected abstract AbstractFeiShuOrganizationService getOrganizationService();

    /**
     * 同步部门主管,实现中间表对比
     */
    public void syncThirdOrgManagers(String companyId, String corpId, String companyName) {
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> departmentInfos = getOrganizationService().getAllDepartments(corpId, companyName);
            if (!ObjectUtils.isEmpty(departmentInfos)) {
                List<OpenThirdOrgUnitManagers> openThirdOrgUnitManagersList = new ArrayList<>();
                departmentInfos.forEach(department -> {
                    String managers = department.getLeaderUserId();
                    if (!com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(managers)) {
                        openThirdOrgUnitManagersList.add(OpenThirdOrgUnitManagers.builder()
                                .id(RandomUtils.bsonId())
                                .companyId(companyId)
                                .thirdEmployeeIds(managers)
                                .thirdOrgUnitId(com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(department.getId()))
                                .status(0)
                                .createTime(new Date())
                                .updateTime(new Date())
                                .build());
                    }
                });
                openSyncThirdOrgService.setAllDepManageV2(openThirdOrgUnitManagersList, companyId);
            }
        } else {
            throw new FinhubException(0, "企业:" + companyId + "未开放同步部门主管功能，请检查配置是否正确");
        }
    }

    /**
     * 查询飞书用户信息 , （接口权限同原有批量获取员工接口 , 只需要开通通讯录调用权限即可 ）
     * @param idType id 类型
     * @param id     用户id
     * @param corpId 企业分贝通id
     * @return FeiShuUserInfoDTO 飞书用户信息
     */
    public FeiShuSingleUserDetailRespDTO getSingleUserDetailInfo(String idType , String id, String corpId ) {
        FeiShuSingleUserDetailRespDTO singleUserDetailRespDTO = userSingleDetailGet(idType , id, corpId);
        if ( null == singleUserDetailRespDTO || null == singleUserDetailRespDTO.getData() ){
            return null;
        }
        return singleUserDetailRespDTO;
    }

    /**
     * 获取单个员工详情 V3
     * @param idType 渠道类型
     * @param openId 员工id
     * @param corpId 飞书企业id
     * @return 员工详情DTO
     */
    public FeiShuSingleUserDetailRespDTO userSingleDetailGet( String idType , String openId, String corpId) {
        String url = feishuHost + FeiShuConstant.SINGLE_USER_DETAIL;
        url = String.format(url,openId,idType);
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, null, corpId);
        FeiShuSingleUserDetailRespDTO feiShuSingleUserDetailRespDTO = JsonUtils.toObj(res, FeiShuSingleUserDetailRespDTO.class);
        if (feiShuSingleUserDetailRespDTO == null || 0 != feiShuSingleUserDetailRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_SINGLE_USER_INFO_FAILED);
        }
        return feiShuSingleUserDetailRespDTO;
    }

    /**
     * 获取部门用户详情(带自定义字段的)
     *
     * @param departmentId 部门id
     * @param corpId       飞书corpId
     * @param pageToken    pageToken
     * @return  飞书批量员工接口
     */
    public List<FeiShuBatchUserListDetailV3RespDTO.DataDTO.ItemsDTO> departmentUserDetailListV3(String departmentId, String corpId, String pageToken) {
        String url = feishuHost + FeiShuConstant.GET_DEPARTMENT_USERS_V3;
        url = String.format(url,departmentId);
        Map<String, Object> param = new HashMap<>();
        param.put("department_id", departmentId);
        param.put("page_size", 50);
        param.put("page_token", pageToken);
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, param, corpId);
        FeiShuBatchUserListDetailV3RespDTO feiShuDepartmentUserDetailListRespDTO = JsonUtils.toObj(res, FeiShuBatchUserListDetailV3RespDTO.class);
        if (feiShuDepartmentUserDetailListRespDTO == null || 0 != feiShuDepartmentUserDetailListRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_DEPARTMENT_USER_DETAIL_LIST_FAILED);
        }
        List<FeiShuBatchUserListDetailV3RespDTO.DataDTO.ItemsDTO> userInfosAll = feiShuDepartmentUserDetailListRespDTO.getData().getItems();
        boolean hasMore = feiShuDepartmentUserDetailListRespDTO.getData().getHasMore();
        if (hasMore) {
            pageToken = feiShuDepartmentUserDetailListRespDTO.getData().getPageToken();
            List<FeiShuBatchUserListDetailV3RespDTO.DataDTO.ItemsDTO> userInfos = departmentUserDetailListV3(departmentId, corpId, pageToken);
            userInfosAll.addAll(userInfos);
        }
        return userInfosAll;
    }

    /**
     * 获取子部门列表
     *
     * @param departmentId 部门id
     * @param corpId       飞书corpId
     * @param pageToken    pageToken
     * @return  子部门列表
     */
    public List<FeiShuSubDepartmentListRespDTO.DataDTO.ItemsDTO> subDepartmentList(String departmentId, String corpId, String pageToken) {
        String url = feishuHost + FeiShuConstant.GET_SUB_DEPARTMENT_LIST;
        url = String.format(url,departmentId);
        Map<String, Object> param = new HashMap<>();
        param.put("department_id", departmentId);
        param.put("page_size", 50);
        param.put("fetch_child", true);
        param.put("page_token", pageToken);
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, param, corpId);
        FeiShuSubDepartmentListRespDTO subDepartmentListRespDTO = JsonUtils.toObj(res, FeiShuSubDepartmentListRespDTO.class);
        if (subDepartmentListRespDTO == null || 0 != subDepartmentListRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_DEPARTMENT_USER_DETAIL_LIST_FAILED);
        }
        List<FeiShuSubDepartmentListRespDTO.DataDTO.ItemsDTO> subDepartmentList = subDepartmentListRespDTO.getData().getItems();
        boolean hasMore = subDepartmentListRespDTO.getData().getHasMore();
        if (hasMore) {
            pageToken = subDepartmentListRespDTO.getData().getPageToken();
            List<FeiShuSubDepartmentListRespDTO.DataDTO.ItemsDTO> userInfos = subDepartmentList(departmentId, corpId, pageToken);
            subDepartmentList.addAll(userInfos);
        }
        return subDepartmentList;
    }

    /**
     * 批量获取飞书员工花名册信息
     * @param unionIds 飞书员工唯一员工id
     * @param corpId 飞书corpId
     * @param pageSize 分页大小
     * @return 飞书员工花名册信息
     */
    public FeiShuEhrV1EmployeesDTO getEhrV1Employees(Set<String> unionIds, String corpId, Integer pageSize) {
        String url = feishuHost + FeiShuConstant.GET_EHR_V1_EMPLOYEES;
        url = String.format(url,pageSize, String.join("&user_ids=", unionIds));
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, null, corpId);
        FeiShuEhrV1EmployeesDTO feiShuEhrV1EmployeesDTO = JsonUtils.toObj(res, FeiShuEhrV1EmployeesDTO.class);
        if (feiShuEhrV1EmployeesDTO == null || 0 != feiShuEhrV1EmployeesDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_EHR_V1_EMPLOYEES_FAILED);
        }
        return feiShuEhrV1EmployeesDTO;
    }

    /**
     *
     * 在飞书员工信息中追加飞书批量获取员工花名册信息（目前仅仅追加了合同公司的公司名称字段）
     * 因为有的公司没有权限，可能无法调用成功，所以要自己处理异常信息，不能影响后面的流程
     * @param corpId 飞书corpId
     * @param distinctList 原来获取到的飞书员工信息
     *
     */
    public void getAddFieldFeiShuUserInfoDTOS(String corpId, List<FeiShuUserInfoDTO> distinctList ,String companyId) {
        try {
            //TODO 此日志较大 刚上线时为了排查可能产生的问题，需要打印，等程序稳定后可去除日志打印
            log.info("{}:{}飞书人员追加花名册之前为:{}", companyId,corpId, JsonUtils.toJson(distinctList));
            //配置飞书智能人事接口查询开关，is_checked==0时才查询飞书智能人事接口,否则提前结束方法什么也不干
            OpenMsgSetup openMsgSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, ItemCodeEnum.FEISHU_ROSTER_CONFIG.getCode());
            if (openMsgSetup == null || openMsgSetup.getIsChecked() != 0) {
                return;
            }

            int pageSize = 50;
            List<Set<String>> distinctListSteps = CollectionUtils.batch(distinctList, pageSize).stream()
                .map(t -> t.stream().map(FeiShuUserInfoDTO::getUnionId).collect(Collectors.toSet())).collect(Collectors.toList());

            Map<String, FeiShuUserInfoDTO> distinctListMap = distinctList.stream().collect(Collectors.toMap(FeiShuUserInfoDTO::getUnionId, t -> t));
            if (distinctListSteps.size() > 0) {
                distinctListSteps.forEach(t -> {
                    FeiShuEhrV1EmployeesDTO ehrV1Employees = getEhrV1Employees(t, corpId, pageSize);
                    List<FeiShuEhrV1EmployeesDTO.DataDTO.ItemsDTO> items = ehrV1Employees.getData().getItems();
                    items.forEach(item -> {
                        String userId = item.getUserId();
                        FeiShuUserInfoDTO feiShuUserInfoDTO = distinctListMap.get(userId);
                        feiShuUserInfoDTO.setFeiShuEhrV1EmployeesItem(item);
                    });
                });
            }
        } catch (Exception e) {
            log.warn("{}飞书员工追加字段异常:{}", corpId, e);
        }
        //TODO 此日志较大 刚上线时为了排查可能产生的问题，需要打印，等程序稳定后可去除日志打印
        log.info("{}:{}飞书人员追加花名册之后为:{}", companyId,corpId, JsonUtils.toJson(distinctList));
    }

}
