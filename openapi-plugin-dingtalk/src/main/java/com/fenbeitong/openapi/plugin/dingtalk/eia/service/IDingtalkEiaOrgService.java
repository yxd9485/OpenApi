package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.dingtalk.api.response.OapiAuthScopesResponse;
import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse;

import java.util.List;

/**
 * @author ctl
 * @date 2021/4/28
 */
public interface IDingtalkEiaOrgService {

    /**
     * 获取授权范围
     *
     * @param corpId
     * @return
     */
    OapiAuthScopesResponse getAuthScope(String corpId);

    /**
     * 获取全量部门
     *
     * @param deptId
     * @param corpId
     * @return
     */
    List<OapiDepartmentListResponse.Department> getDepartmentList(String deptId, String corpId);

    /**
     * 获取部门详情
     *
     * @param deptId
     * @param corpId
     * @return
     */
    OapiDepartmentGetResponse getDepartmentDetail(String deptId, String corpId);
}
