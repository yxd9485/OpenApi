package com.fenbeitong.openapi.plugin.welink.common;

/**
 * welink返回码
 */
public interface WeLinkResponseCode {

    Integer SIGN_ERROR = 160001;

    /**
     * 获取access_token异常
     */
    Integer WELINK_ISV_ACCESS_TOKEN_IS_NULL = 160002;

    /**
     * 租户信息异常
     */
    Integer WELINK_ISV_TENANTS_INFO_IS_NULL = 160003;

    /**
     * 创建企业失败
     */
    Integer WELINK_ISV_CREATE_COMPANY_FAILED = 160004;


    /**
     * auth_code换取access_toke失败
     */
    Integer WELINK_ISV_AUTH_CODE_TO_ACCESS_TOKEN_FAILED = 160005;

    /**
     * 查询is_admin失败
     */
    Integer WELINK_ISV_AUTH_CODE_TO_IS_ADMIN_FAILED = 160006;

    /**
     * 查询用户id失败
     */
    Integer WELINK_ISV_AUTH_CODE_TO_USER_FAILED = 160007;

    /**
     * 企业未授权
     */
    Integer WELINK_ISV_COMPANY_UNDEFINED = 160008;

    /**
     * 企业授权已过期
     */
    Integer WELINK_ISV_COMPANY_STATE_CLOSE = 160009;

    /**
     * 查询用户基本信息失败
     */
    Integer WELINK_ISV_USER_SIMPLE_FAILED = 160010;

    /**
     * WELINK用户信息获取失败
     */
    Integer WELINK_CORP_EMPLOYEE_NOT_EXISTS = 160011;

    /**
     * 查询子部门信息失败
     */
    Integer WELINK_DEPARTMENTS_LIST_FAILED = 160012;

    /**
     * 查询部门人员信息列表失败
     */
    Integer WELINK_ISV_USERS_LIST_FAILED = 160013;

    /**
     * 查询用户邮箱信息失败
     */
    Integer WELINK_ISV_USERS_EMAIL_FAILED = 160014;

    /**
     * 发送消息失败
     */
    Integer WELINK_ISV_SEND_MESSAGE_FAILED = 160015;

    /**
     * 非管理员
     */
    Integer WELINK_ISV_IS_NOT_ADMIN = 160016;

    /**
     * web授权失败
     */
    Integer WELINK_ISV_WEB_AUTH_FAILED= 160017;

}
