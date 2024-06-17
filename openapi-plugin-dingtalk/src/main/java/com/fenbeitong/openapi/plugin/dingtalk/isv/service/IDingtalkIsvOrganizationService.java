package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse;

import java.util.List;

/**
 * @author lizhen
 */
public interface IDingtalkIsvOrganizationService {

    List<OapiDepartmentListResponse.Department> getAllDepartments(List<Long> authedDept, String corpId, String companyName);

    OapiDepartmentGetResponse getDepartmentDetail(String deptId, String corpId);
}
