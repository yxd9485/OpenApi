package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;

import java.util.List;

/**
 * <p>Title: IDingTalkSyncThirdEmployeeService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/12 6:24 PM
 */
public interface IDingTalkSyncThirdEmployeeService {

    /**
     * 同步第三方人员
     *
     * @param companyId 分贝公司id
     */
    void syncThirdEmployee(String companyId);

    /**
     * 检查用户
     *
     * @param companyId 分贝公司id
     * @return 未同步的用户列表
     */
    List<DingtalkUser> checkDingtalkEmployee(String companyId);

    /**
     * 同步人员组织机构
     *
     * @param companyId
     */
    void syncThirdOrgEmployee(String companyId);

    /**
     * 按权限同步部门人员
     *
     * @param companyId
     * @param flag      0:true（增量）   1:false（全量）
     */
    void syncThirdOrgEmployeeByAuth(String companyId, String flag);

    /**
     * 全量同步部门主管
     *
     * @param companyId
     */
    void syncThirdOrgManagers(String companyId);
}
