package com.fenbeitong.openapi.plugin.dingtalk.customize.service;

import com.dingtalk.api.response.OapiDepartmentGetResponse;

import java.util.Map;

/**
 * <p>Title: DingTalkCustomizeService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/1/7 1:39 下午
 */
public interface DingTalkCustomizeService {
    /**
     * 获取部门详情
     */
    Map<String,String> getDepDetail(String companyId, String deptId);
}
