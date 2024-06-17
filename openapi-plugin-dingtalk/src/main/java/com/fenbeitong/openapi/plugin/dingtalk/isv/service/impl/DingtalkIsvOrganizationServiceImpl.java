package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.request.OapiDepartmentGetRequest;
import com.dingtalk.api.request.OapiDepartmentListRequest;
import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvDepartmentRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvEmployeeService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvOrganizationService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author lizhen
 * @date 2020/7/15
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvOrganizationServiceImpl implements IDingtalkIsvOrganizationService {

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Autowired
    private DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Autowired
    private IDingtalkIsvEmployeeService dingtalkIsvEmployeeService;

    /**
     * 获取全量部门
     *
     * @param corpId
     */
    public List<OapiDepartmentListResponse.Department> getDepartmentList(String deptId, String corpId) {
        String url = dingtalkHost + "department/list";
        OapiDepartmentListRequest request = new OapiDepartmentListRequest();
        request.setId(deptId);
        request.setHttpMethod("GET");
        OapiDepartmentListResponse oapiDepartmentListResponse = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return filterJiaXiaoTongXunLu(oapiDepartmentListResponse.getDepartment());
    }

    @Override
    public List<OapiDepartmentListResponse.Department> getAllDepartments(List<Long> authedDept, String corpId, String companyName) {
        //1.获取授权范围
        List<OapiDepartmentListResponse.Department> departmentAll = new ArrayList<>();
        if (!ObjectUtils.isEmpty(authedDept)) {
            for (Long deptId : authedDept) {
                //2.授权部门子部门
                List<OapiDepartmentListResponse.Department> departmentList = getDepartmentList(StringUtils.obj2str(deptId), corpId);
                if (!ObjectUtils.isEmpty(departmentList)) {
                    departmentAll.addAll(departmentList);
                }
                //当前授权部门
                OapiDepartmentGetResponse departmentDetail = getDepartmentDetail(StringUtils.obj2str(deptId), corpId);
                OapiDepartmentListResponse.Department department = new OapiDepartmentListResponse.Department();
                department.setId(departmentDetail.getId());
                department.setName(departmentDetail.getName());
                department.setParentid(departmentDetail.getParentid());
                departmentAll.add(department);
            }
        }
        //3.去重
        List<OapiDepartmentListResponse.Department> distinctList = departmentAll
                .stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getId()))), ArrayList::new))
                .stream().sorted(Comparator.comparing(OapiDepartmentListResponse.Department::getId)).collect(Collectors.toList());
        //4.转换isv部门（排序，填被父部门、根部门信息）
        DingtalkIsvDepartmentRespDTO dingtalkIsvDepartmentRespDTO = new DingtalkIsvDepartmentRespDTO();
        dingtalkIsvDepartmentRespDTO.setDepartmentInfos(distinctList);
        List<OapiDepartmentListResponse.Department> isvDepartmentList = dingtalkIsvDepartmentRespDTO.getIsvDepartmentList(companyName);
        return isvDepartmentList;
    }

    @Override
    public OapiDepartmentGetResponse getDepartmentDetail(String deptId, String corpId) {
        String url = dingtalkHost + "department/get";
        OapiDepartmentGetRequest request = new OapiDepartmentGetRequest();
        request.setId(deptId);
        request.setHttpMethod("GET");
        OapiDepartmentGetResponse oapiDepartmentGetResponse = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return oapiDepartmentGetResponse;
    }


    private List<OapiDepartmentListResponse.Department> filterJiaXiaoTongXunLu(List<OapiDepartmentListResponse.Department> sortedDepartments) {
        List<Long> needFilterDeptIdList = Lists.newArrayList();
        List<OapiDepartmentListResponse.Department> jiaXiaoTongXunLu = sortedDepartments.stream().filter(d -> d.getId() < 0).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(jiaXiaoTongXunLu)) {
            jiaXiaoTongXunLu.forEach(d -> needFilterDeptIdList.addAll(getAllJiaXiaoSubDeptIdList(d.getId(), sortedDepartments)));
        }
        return ObjectUtils.isEmpty(needFilterDeptIdList)
                ? sortedDepartments
                : sortedDepartments.stream().filter(d -> !needFilterDeptIdList.contains(d.getId())).collect(Collectors.toList());
    }


    private List<Long> getAllJiaXiaoSubDeptIdList(Long id, List<OapiDepartmentListResponse.Department> sortedDepartments) {
        List<Long> idList = Lists.newArrayList();
        idList.add(id);
        List<OapiDepartmentListResponse.Department> subDeptList = sortedDepartments.stream().filter(d -> NumericUtils.obj2long(d.getParentid(), 0) == id).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(subDeptList)) {
            subDeptList.forEach(d -> idList.addAll(getAllJiaXiaoSubDeptIdList(d.getId(), sortedDepartments)));
        }
        return idList;
    }
}
