package com.fenbeitong.openapi.plugin.wechat.eia.constant;

/**
 * 微信回调tag
 * Created by dave.hansins on 19/12/8.
 */
public interface WeChatEiaCallbackTagConstant {
    /**
     * 企业微信审批单
     */
    String WECHAT_APPLY_INSTANCE = "sys_approval_change";

    /**
     * suite_ticket
     */
    String WECHAT_SUITE_TICKET = "suite_ticket";

    /**
     * 企业授权
     */
    String WECHAT_CREATE_AUTH = "create_auth";

    /**
     * 企业变更授权
     */
    String WECHAT_CHANGE_AUTH = "change_auth";

    /**
     * 取消授权
     */
    String WECHAT_CANCEL_AUTH = "cancel_auth";

    /**
     * 重发授权码
     */
    String WECHAT_RESET_PERMANENT_CODE = "reset_permanent_code";

    /**
     * 通讯录变更
     */
    String WECHAT_CHANGE_CONTACT = "change_contact";

    /**
     * 通讯录增加用户
     */
    String WECHAT_CREATE_USER = "create_user";

    /**
     * 通讯录更改用户
     */
    String WECHAT_UPDATE_USER = "update_user";

    /**
     * 通讯录用户离职
     */
    String WECHAT_DELETE_USER = "delete_user";
    /**
     * 通讯录企业部门创建
     */
    String WECHAT_ORG_DEPT_CREATE = "create_party";
    /**
     * 通讯录企业部门修改
     */
    String WECHAT_ORG_DEPT_MODIFY = "update_party";
    /**
     * 通讯录企业部门删除
     */
    String WECHAT_ORG_DEPT_REMOVE = "delete_party";
    /**
     * 通讯录增加用户
     */
    String WECHAT_EIA_CREATE_OR_UPDATE_USER = "wechat_eia_create_or_update_user";

    /**
     * 通讯录用户离职
     */
    String WECHAT_EIA_DELETE_USER = "wechat_eia_delete_user";
    /**
     * 通讯录企业部门创建
     */
    String WECHAT_EIA_CREATE_OR_UPDATE_DEPT = "wechat_eia_create_or_update_dept";
    /**
     * 通讯录企业部门删除
     */
    String WECHAT_EIA_REMOVE_DEPT = "wechat_eia_remove_dept";

}
