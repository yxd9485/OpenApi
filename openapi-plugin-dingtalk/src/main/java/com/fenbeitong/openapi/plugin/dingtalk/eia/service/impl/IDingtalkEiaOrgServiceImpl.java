package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.request.OapiAuthScopesRequest;
import com.dingtalk.api.request.OapiDepartmentGetRequest;
import com.dingtalk.api.request.OapiDepartmentListRequest;
import com.dingtalk.api.response.OapiAuthScopesResponse;
import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkEiaOrgService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.utils.DingtalkEiaClientUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ctl
 * @date 2021/4/28
 */
@ServiceAspect
@Service
@Slf4j
public class IDingtalkEiaOrgServiceImpl implements IDingtalkEiaOrgService {

    @Autowired
    private DingtalkEiaClientUtils dingtalkEiaClientUtils;

    @Override
    public OapiAuthScopesResponse getAuthScope(String corpId) {
        String url = dingtalkEiaClientUtils.getProxyUrlByCorpId(corpId) + "/auth/scopes";
        OapiAuthScopesRequest request = new OapiAuthScopesRequest();
        request.setHttpMethod("GET");
        OapiAuthScopesResponse oapiAuthScopesResponse = dingtalkEiaClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return oapiAuthScopesResponse;
    }

    @Override
    public List<OapiDepartmentListResponse.Department> getDepartmentList(String deptId, String corpId) {
        String url = dingtalkEiaClientUtils.getProxyUrlByCorpId(corpId) + "/department/list";
        OapiDepartmentListRequest request = new OapiDepartmentListRequest();
        request.setId(deptId);
        request.setHttpMethod("GET");
        OapiDepartmentListResponse oapiDepartmentListResponse = dingtalkEiaClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return filterJiaXiaoTongXunLu(oapiDepartmentListResponse.getDepartment());
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
        List<OapiDepartmentListResponse.Department> subDeptList = sortedDepartments.stream().filter(d -> com.fenbeitong.openapi.plugin.util.NumericUtils.obj2long(d.getParentid(), 0) == id).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(subDeptList)) {
            subDeptList.forEach(d -> idList.addAll(getAllJiaXiaoSubDeptIdList(d.getId(), sortedDepartments)));
        }
        return idList;
    }

    @Override
    public OapiDepartmentGetResponse getDepartmentDetail(String deptId, String corpId) {
        String url = dingtalkEiaClientUtils.getProxyUrlByCorpId(corpId) + "/department/get";
        OapiDepartmentGetRequest request = new OapiDepartmentGetRequest();
        request.setId(deptId);
        request.setHttpMethod("GET");
        OapiDepartmentGetResponse oapiDepartmentGetResponse = dingtalkEiaClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return oapiDepartmentGetResponse;
    }
}
