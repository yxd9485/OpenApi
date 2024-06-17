package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.dingtalk.api.response.OapiDepartmentListResponse;

import java.util.List;

/**
 * <p>Title: IDingTalkSyncThirdOrgService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/11 5:54 PM
 */
public interface IDingTalkSyncThirdOrgService {

    void syncThirdOrg(String companyId);

    List<OapiDepartmentListResponse.Department> checkDingtalkDepartment(String companyId);
}
