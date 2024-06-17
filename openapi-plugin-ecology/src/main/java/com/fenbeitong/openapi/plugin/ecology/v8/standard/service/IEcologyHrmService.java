package com.fenbeitong.openapi.plugin.ecology.v8.standard.service;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyDepartmentInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologySubCompanyInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page.EcologyUserInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.weaver.v8.hrm.DepartmentBean;
import com.weaver.v8.hrm.SubCompanyBean;
import com.weaver.v8.hrm.UserBean;

import java.util.List;

/**
 * <p>Title: IEcologyHrmService</p>
 * <p>Description: 泛微hrm服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/1 8:19 PM
 */
public interface IEcologyHrmService {

    /**
     * 根据配置加载用户信息
     *
     * @param workflowConfig
     * @param userCode       员工工号
     * @return
     */
    UserBean getUserByUserCode(OpenEcologyWorkflowConfig workflowConfig, String userCode);

    /**
     * 根据泛微人员id查询对应的员工信息
     *
     * @param workflowConfig 配置信息
     * @param userIdList     泛微人员id
     * @return
     */
    List<UserBean> getUserByUserId(OpenEcologyWorkflowConfig workflowConfig, List<String> userIdList);

    /**
     * 根据配置加载部门列表
     *
     * @param workflowConfig
     * @return
     */
    List<DepartmentBean> getDepartmentInfoList(OpenEcologyWorkflowConfig workflowConfig);

    /**
     * 根据配置加载人员列表
     *
     * @param workflowConfig
     * @return
     */
    List<UserBean> getUserInfoList(OpenEcologyWorkflowConfig workflowConfig);

    /**
     * 拉取分部信息
     *
     * @param workflowConfig
     * @return
     */
    List<SubCompanyBean> getHrmSubcompanyInfo(OpenEcologyWorkflowConfig workflowConfig);

    /**
     * 根据分部获取人员列表
     *
     * @param workflowConfig
     * @return
     */
    List<UserBean> getUserInfoListWithSubCompany(OpenEcologyWorkflowConfig workflowConfig);

    List<UserBean> getUserInfoListWithDepartment(OpenEcologyWorkflowConfig workflowConfig);

    /**
     * 分页获取人员
     *
     * @param workflowConfig
     * @return
     */
    List<EcologyUserInfo> getUserInfoListPage(OpenEcologyWorkflowConfig workflowConfig);

    /**
     * 分页获取分部
     *
     * @param workflowConfig
     * @return
     */
    List<EcologySubCompanyInfo> getSubCompanyInfoListPage(OpenEcologyWorkflowConfig workflowConfig);

    /**
     * 分页获取部门
     *
     * @param workflowConfig
     * @return
     */
    List<EcologyDepartmentInfo> getDepartmentListPage(OpenEcologyWorkflowConfig workflowConfig);
}
