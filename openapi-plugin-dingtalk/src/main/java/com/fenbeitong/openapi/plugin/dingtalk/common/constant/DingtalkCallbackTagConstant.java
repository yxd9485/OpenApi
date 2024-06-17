package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

public interface DingtalkCallbackTagConstant {
    /**
     * suite_ticket
     */
    String SUITE_TICKET = "suite_ticket";
    /**
     * 通讯录用户增加
     */
    String USER_ADD_ORG = "user_add_org";
    /**
     * 通讯录用户更改
     */
    String USER_MODIFY_ORG = "user_modify_org";
    /**
     * 通讯录用户离职
     */
    String USER_LEAVE_ORG = "user_leave_org";
    /**
     * 通讯录企业部门创建
     */
    String ORG_DEPT_CREATE = "org_dept_create";
    /**
     * 通讯录企业部门修改
     */
    String ORG_DEPT_MODIFY = "org_dept_modify";
    /**
     * 通讯录企业部门删除
     */
    String ORG_DEPT_REMOVE = "org_dept_remove";
    /**
     * 审批实例开始，结束
     */
    String BPMS_INSTANCE_CHANGE = "bpms_instance_change";

    /**
     * urlcheck
     */
    String CHECK_URL = "check_url";

    /**
     * 验证回调
     */
    String CHECK_CREATE_SUITE_URL = "check_create_suite_url";

    /**
     * 更新回调地址
     */
    String CHECK_UPDATE_SUITE_URL = "check_update_suite_url";

    /**
     * 授权
     */
    String TMP_AUTH_CODE = "tmp_auth_code";

    /**
     * 停用应用
     */
    String ORG_MICRO_APP_STOP = "org_micro_app_stop";

    /**
     * 启用应用
     */
    String ORG_MICRO_APP_RESTORE = "org_micro_app_restore";

    /**
     * 解除授权
     */
    String SUITE_RELIEVE = "suite_relieve";

    /**
     * 通讯录授权范围变更事件
     */
    String CHANGE_AUTH = "change_auth";


    /**
     * market_buy
     */
    String MARKET_BUY = "market_buy";

    /**
     * 表示企业授权套件
     */
    String ORG_SUITE_AUTH = "org_suite_auth";

    /**
     * 表示企业变更授权范围
     */
    String ORG_SUITE_CHANGE = "org_suite_change";

    /**
     * 表示企业解除授权
     */
    String ORG_SUITE_RELIEVE = "org_suite_relieve";

    /**
     * 微应用删除，保留企业对套件的授权
     */
    String ORG_MICRO_APP_REMOVE = "org_micro_app_remove";

    /**
     * 企业删除
     */
    String ORG_REMOVE = "org_remove";
}
