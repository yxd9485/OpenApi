package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuContactScopeReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuDepartmentInfoRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuDepartmentSimpleListRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeishuAttendanceRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.support.organization.dto.OrgUnitDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant.EIA_INVOKE_TYPE;
import static com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant.ISV_INVOKE_TYPE;

/**
 * 飞书部门service
 *
 * @author lizhen
 * @date 2020/6/2
 */
@ServiceAspect
@Service
@Slf4j
public abstract class AbstractFeiShuOrganizationService extends AbstractOrganizationService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    /**
     * 获取子部门列表
     *
     * @param corpId
     * @param pageToken
     * @return
     */
    public List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> departmentSimpleList(String departmentId, String corpId, String pageToken) {
        String url = feishuHost + FeiShuConstant.DEPARTMENT_SIMPLE_LIST_URL;
        int openType = getOpenType();
        String invokeType = openType == OpenType.FEISHU_EIA.getType() ? EIA_INVOKE_TYPE : ISV_INVOKE_TYPE;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id_type", invokeType);
        param.put("parent_department_id", departmentId);
        param.put("department_id_type", "department_id");
        param.put("page_size", 50);
        param.put("fetch_child", true);
        param.put("page_token", pageToken);
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, param, corpId);
        FeiShuDepartmentSimpleListRespDTO feiShuDepartmentSimpleListRespDTO = JsonUtils.toObj(res, FeiShuDepartmentSimpleListRespDTO.class);
        if (feiShuDepartmentSimpleListRespDTO == null || 0 != feiShuDepartmentSimpleListRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_DEPARTMENT_SIMPLE_LIST_FAILED);
        }
        List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> departmentInfosAll = feiShuDepartmentSimpleListRespDTO.getData().getItems();
        boolean hasMore = feiShuDepartmentSimpleListRespDTO.getData().isHasMore();
        if (hasMore) {
            pageToken = feiShuDepartmentSimpleListRespDTO.getData().getPageToken();
            List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> departmentInfos = departmentSimpleList(departmentId, corpId, pageToken);
            departmentInfosAll.addAll(departmentInfos);
        }
        return departmentInfosAll;
    }

    /**
     * 获取部门详情
     *
     * @param departmentId
     * @param corpId
     * @return
     */
    public FeiShuDepartmentSimpleListRespDTO.DepartmentInfo getDepartmentInfo(String departmentId, String corpId) {
        String url = feishuHost + FeiShuConstant.DEPARTMENT_INFO_GET_URL;
        int openType = getOpenType();
        Map<String, Object> param = new HashMap<>();
        url = String.format(url, departmentId);
        param.put("department_id_type", "department_id");
        String invokeType = openType == OpenType.FEISHU_EIA.getType() ? EIA_INVOKE_TYPE : ISV_INVOKE_TYPE;
        param.put("user_id_type", invokeType);
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, param, corpId);
        FeiShuDepartmentInfoRespDTO feiShuDepartmentInfoRespDTO = JsonUtils.toObj(res, FeiShuDepartmentInfoRespDTO.class);
        if (feiShuDepartmentInfoRespDTO == null || 0 != feiShuDepartmentInfoRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_GET_DEPARTMENT_INFO_FAILED);
        }
        FeiShuDepartmentSimpleListRespDTO.DepartmentInfo departmentInfo = feiShuDepartmentInfoRespDTO.getData().getDepartmentInfo();
        return departmentInfo;
    }


    /**
     * 获取全量部门
     * 1.获取授权范围
     * 2.遍历授权部门获得子部门及授权部门信息
     * 3.去重。飞书父、子部门都被勾选授权时，授权范围中会同时出现父、子部门，遍历获取子部门时造成数据重复
     * 4.排序，填充根部门、父部门
     *
     * @param corpId
     * @param companyName
     * @return
     */
    public List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> getAllDepartments(String corpId, String companyName) {
        //1.获取授权部门
        FeiShuContactScopeReqDTO.ContactScope contactScope = getFeiShuEmployeeService().getContactScope(corpId);
        List<String> authedDepartments = contactScope.getAuthedDepartments();
        List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> departmentAll = new ArrayList<>();
        if (!ObjectUtils.isEmpty(authedDepartments)) {
            for (String departmentId : authedDepartments) {
                //2.授权部门子部门
                List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> departmentInfos = departmentSimpleList(departmentId, corpId, null);
                if (!ObjectUtils.isEmpty(departmentInfos)) {
                    departmentAll.addAll(departmentInfos);
                }
                //当前授权部门
                FeiShuDepartmentSimpleListRespDTO.DepartmentInfo departmentInfo = getDepartmentInfo(departmentId, corpId);
                if (!ObjectUtils.isEmpty(departmentInfo)) {
                    departmentAll.add(departmentInfo);
                }

            }
        }
        //3.去重
        List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> distinctList = departmentAll
                .stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getId()))), ArrayList::new))
                .stream().sorted(Comparator.comparing(FeiShuDepartmentSimpleListRespDTO.DepartmentInfo::getId)).collect(Collectors.toList());
        //4.转换isv部门（排序，填被父部门、根部门信息）
        FeiShuDepartmentSimpleListRespDTO feiShuDepartmentSimpleListRespDTO = new FeiShuDepartmentSimpleListRespDTO();
        FeiShuDepartmentSimpleListRespDTO.DepartmentSimpleList departmentSimpleList = new FeiShuDepartmentSimpleListRespDTO.DepartmentSimpleList();
        departmentSimpleList.setItems(distinctList);
        feiShuDepartmentSimpleListRespDTO.setData(departmentSimpleList);
        List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> isvDepartmentList = feiShuDepartmentSimpleListRespDTO.getIsvDepartmentList(companyName);
        return isvDepartmentList;
    }

    public List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> checkFeiShuDepartment(String companyId, String corpId, String companyName, int openType) {
        List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> allDepartments = getAllDepartments(corpId, companyName);
        if (!ObjectUtils.isEmpty(allDepartments)) {
            List<OrgUnitDTO> fbOrgUnitList = listFbOrgUnit(companyId);
            fbOrgUnitList = fbOrgUnitList.stream().filter(d -> d.getOrgUnitParentId() != null).collect(Collectors.toList());
            List<String> thirdOrgIdList = fbOrgUnitList.stream().map(OrgUnitDTO::getOrgThirdUnitId).collect(Collectors.toList());
            List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> collect = allDepartments.stream().filter(d -> !thirdOrgIdList.contains(d.getId())).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 查询所有的一级部门
     *
     * @param corpId
     * @return
     */
    public List<String> getFeishuOneDepartmentList(String corpId) {
        //1.获取授权部门
        FeiShuContactScopeReqDTO.ContactScope contactScope = getFeiShuEmployeeService().getContactScope(corpId);
        List<String> authedDepartments = contactScope.getAuthedDepartments();
        return authedDepartments;
    }

//    /**
//     * 获取部门列表id
//     * @param corpId
//     * @return
//     */
//    public  List<FeiShuDepartmentListDataDTO.DepartmentDataInfo> getFeishuDepartmentList(String corpId,String parentDepartmentId,String pageToken) {
//        String url = feishuHost + FeiShuConstant.DEPARTMENTID_LIST_INFO_GET_URL;
//        Map<String, Object> param = new HashMap<>();
//        param.put("fetch_child", true);
//        param.put("parent_department_id", parentDepartmentId);
//        param.put("page_token", pageToken);
//        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, param, corpId);
//        FeiShuDepartmentListDataDTO feiShuDepartmentInfoRespDTO = JsonUtils.toObj(res, FeiShuDepartmentListDataDTO.class);
//        if (feiShuDepartmentInfoRespDTO == null || 0 != feiShuDepartmentInfoRespDTO.getCode()) {
//            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_GET_DEPARTMENT_INFO_FAILED);
//        }
//        List<FeiShuDepartmentListDataDTO.DepartmentDataInfo> departmentsIds = feiShuDepartmentInfoRespDTO.getData().getItems();
//        boolean hasMore = feiShuDepartmentInfoRespDTO.getData().isHasMore();
//        if (hasMore) {
//            pageToken = feiShuDepartmentInfoRespDTO.getData().getPageToken();
//            List<FeiShuDepartmentListDataDTO.DepartmentDataInfo>  departmentInfos = getFeishuDepartmentList(corpId,pageToken);
//            departmentsIds.addAll(departmentInfos);
//        }
//        return departmentsIds;
//    }

    /**
     * 获取FeiShuHttpUtils
     *
     * @return
     */
    protected abstract AbstractFeiShuHttpUtils getFeiShuHttpUtils();

    /**
     * 获取FeiShuEmployeeService
     *
     * @return
     */
    protected abstract AbstractFeiShuEmployeeService getFeiShuEmployeeService();

    /**
     * 获取 openType
     *
     * @return
     */
    protected abstract int getOpenType();

    /**
     * 部门同步前按需过滤
     *
     * @param departmentConfig
     * @param departmentInfo
     * @param openThirdOrgUnitDTO
     * @return
     */
    public OpenThirdOrgUnitDTO departmentBeforeSyncFilter(OpenThirdScriptConfig departmentConfig, FeiShuDepartmentSimpleListRespDTO.DepartmentInfo departmentInfo, OpenThirdOrgUnitDTO openThirdOrgUnitDTO) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("departmentInfo", departmentInfo);
            put("openThirdOrgUnitDTO", openThirdOrgUnitDTO);
        }};
        if (StringUtils.isNotBlank(departmentConfig.getParamJson()) && JsonUtils.toObj(departmentConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(departmentConfig.getParamJson(), Map.class));
        }
        return (OpenThirdOrgUnitDTO) EtlUtils.execute(departmentConfig.getScript(), params);
    }
}
