package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.dingtalk.api.response.OapiAuthScopesResponse;
import com.dingtalk.api.response.OapiSmartworkHrmEmployeeV2ListResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;

import java.util.List;
import java.util.Map;

/**
 * @author lizhen
 */
public interface IDingtalkIsvEmployeeService {

    /**
     * 获取钉钉用户信息
     *
     * @param userId
     * @param corpId
     * @return
     */
    OapiUserGetResponse getUserInfo(String userId, String corpId);

    /**
     * 获取通讯录权限范围
     *
     * @param corpId
     * @return
     */
    OapiAuthScopesResponse getAuthScope(String corpId);

    /**
     * 全量同步部门人员
     *
     * @param corpId
     */
    void syncOrgEmployee(String corpId);

    /**
     *
     *  钉钉人员信息转换
     * @param employeeConfig 脚本配置
     * @param userInfo 钉钉员工信息
     * @param openThirdEmployeeDTO  分贝通人员信息
     * @param hrmFieldMap  智能人事-花名册信息
     * @param <T>
     * @return
     */
    <T> OpenThirdEmployeeDTO employeeBeforeSyncFilter(OpenThirdScriptConfig employeeConfig, T userInfo, OpenThirdEmployeeDTO openThirdEmployeeDTO,Map<String,String> hrmFieldMap);

    //人员拓展属性映射转换
    void parseEmployeeCustomeAttr(Map employeeCustomizeAttributeMap, String extattr , OpenThirdEmployeeDTO openThirdEmployeeDTO);

    ThirdEmployeeRes getEmployeeByThirdId(String companyId, String thirdUserId);

    String superAdmin(String companyId);

    EmployeeContract getEmployeeByEmployeeId(String companyId, String employeeId);

    /**
     * 同步部门主管
     */
    void syncOrgManagers(String corpId);

    /**
     *  查询钉钉智能人事-花名册 信息
     * @param corpId 钉钉企业id
     * @param agentId 应用的AgentID
     * @param userIdListStr 员工的userid列表，多个userid之间使用逗号分隔，一次最多支持传100个值。
     * @param fieldFilterListStr 需要获取的花名册字段field_code值列表,多个field_code之间使用逗号分割
     * @return
     */
    OapiSmartworkHrmEmployeeV2ListResponse getSmartHrmEmployeeList(String corpId, Long agentId, String userIdListStr, String fieldFilterListStr);

    /**
     * 批量查询智能人事-花名册信息
     * @param corpId 钉钉企业id
     * @param agentId 应用的AgentID
     * @param userIdList 员工的userid列表
     * @param fieldFilterList 需要获取的花名册字段field_code值列表
     * @return Map<userId,<field_code,field_value>>
     */
    Map<String,Map<String,String>> batchGetSmartHrmEmployee(String corpId, Long agentId, List<String> userIdList,List<String> fieldFilterList);
}
