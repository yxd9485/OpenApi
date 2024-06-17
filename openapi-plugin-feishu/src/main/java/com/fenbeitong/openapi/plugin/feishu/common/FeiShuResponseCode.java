package com.fenbeitong.openapi.plugin.feishu.common;

/**
 * 返回码
 *
 * @author lizhen
 */
public interface FeiShuResponseCode {

    /**
     * aes加解密失败
     */
    Integer AES_ERROR = 180001;

    /**
     * app_ticket为空
     */
    Integer FEISHU_ISV_APP_TICKET_IS_NULL = 180002;

    /**
     * 获取app_access_token失败
     */
    Integer FEISHU_ISV_GET_APP_ACCESS_TOKEN_FAILED = 180003;

    /**
     * 获取tenant_access_token失败
     */
    Integer FEISHU_ISV_GET_TENANT_ACCESS_TOKEN_FAILED = 180004;

    /**
     * 批量获取用户信息失败
     */
    Integer FEISHU_ISV_USER_BATCH_GET_FAILED = 180005;

    /**
     * 创建企业失败%s
     */
    Integer FEISHU_ISV_CREATE_COMPANY_FAILED = 180006;

    /**
     * 企业未授权
     */
    Integer FEISHU_ISV_COMPANY_UNDEFINED = 180007;

    /**
     * 获取子部门列表失败
     */
    Integer FEISHU_ISV_DEPARTMENT_SIMPLE_LIST_FAILED = 180008;

    /**
     * 获取部门用户详情失败
     */
    Integer FEISHU_ISV_DEPARTMENT_USER_DETAIL_LIST_FAILED = 180009;

    /**
     * 获取部门详情失败
     */
    Integer FEISHU_ISV_GET_DEPARTMENT_INFO_FAILED = 180010;

    /**
     * 应用免登code校验失败
     */
    Integer TOKEN_LOGIN_VALIDATE_FAILED = 180011;


    /**
     * 后台免登code校验失败
     */
    Integer WEB_LOGIN_VALIDATE_FAILED = 180012;

    /**
     * 校验应用管理员失败
     */
    Integer CHECK_IS_USER_ADMIN_FAILED = 180013;

    /**
     * 校验应用管理员失败
     */
    Integer CHECK_IS_NOT_ADMIN = 180014;

    /**
     * 企业授权已过期
     */
    Integer COMPANY_STATE_CLOSE = 180015;


    /**
     * web授权失败%s
     */
    Integer WEB_AUTH_FAILED = 180016;

    /**
     * 消息推送失败%s
     */
    Integer SEND_MESSAGE_FAILED = 180017;

    /**
     * 获取通讯录授权范围失败
     */
    Integer GET_CONTACT_SCOPE_FAILED = 180018;

    /**
     * 获取审批单详情失败
     */
    Integer GET_APPROVAL_DETAIL_FAILED = 180019;

    /**
     * 获取订单失败
     */
    Integer GET_ORDER_FAILED = 180020;

    /**
     * 获取审批定义
     */
    Integer GET_APPROVAL_DEFINE_FAILED = 180021;

    /**
     * 创建审批实例
     */
    Integer CREATE_APPROVAL_INSTANCE_FAILED = 180022;

    /**
     * EIA 企业未授权
     */
    Integer FEISHU_EIA_COMPANY_UNDEFINED = 180023;

    /**
     * 根据手机号或者邮箱获取用户详情失败
     */
    Integer GET_USER_INFO_DETAIL_FAILED = 180024;

    /**
     * 获取考勤组详情失败
     */
    Integer GET_ATTENDANCE_GROUP_DETAIL_INFO_FAILED = 180025;

    /**
     * 获取打卡记录失败
     */
    Integer GET_ATTENDANCE_RECORD_FAILED = 180026;

    /**
     * 获取锁失败
     */
    Integer GET_LOCK_FAILED = 180027;

    /**
     * 获取单个用户详情失败
     */
    Integer GET_SINGLE_USER_INFO_FAILED = 180028;

    /**
     * 上传文件失败
     */
    Integer FEISHU_UPLOAD_FAILED = 180029;

    /**
     * 批量获取员工花名册信息 失败
     */
    Integer GET_EHR_V1_EMPLOYEES_FAILED = 180030;

    /**
     * 配置信息有误
     */
    Integer CONFIGURATION_ERROR = 180031;

    /**
     * 接口没有权限
     */
    Integer FEISHU_NO_PERMISSION = 99991672;

    /**
     * 接口调用成功
     */
    Integer FEISHU_SUCESS = 0;

    /**
     *  飞书申请单解析失败
     */
    Integer FEISHU_APPROVAL_FORM_PARSE_ERROR = 180031;
    /**
     *  该飞书申请单不需要处理
     */
    Integer FEISHU_APPROVAL_FORM_SKIP = 180032;
    /**
     * 飞书用车申请单未配置控件：外出控件组
     */
    Integer FEISHU_CAR_FORM_OUT_GROUP_IS_NULL = 180033;
    /**
     * 飞书用车申请单未配置控件：用车城市
     */
    Integer FEISHU_CAR_FORM_CITY_IS_NULL = 180034;
    /**
     * 飞书用车申请单未配置控件：日期区间(DateInterval)
     */
    Integer FEISHU_CAR_FORM_DATE_INTERVAL_IS_NULL = 180035;

}
