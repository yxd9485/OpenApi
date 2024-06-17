package com.fenbeitong.openapi.plugin.dingtalk.common;

/**
 * <p>Title: DingtalkResponseCode</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/21 5:49 PM
 */
public interface DingtalkResponseCode {


    String TOKEN_INFO_IS_ERROR = "403";

    /**
     * 钉钉异常
     */
    String DINGTALK_ERROR ="900001";

    Long CALLBACK_NOT_EXISTS = 71007L;

    /**
     * 钉钉部门未同步
     */
    int DINGTALK_DEPARTMENT_UN_SYNC = 130401;

    /**
     * 钉钉用户未同步
     */
    int DINGTALK_USER_UN_SYNC = 130402;

    /**
     * 钉钉同步审批失败
     */
    int DINGTALK_SYNC_APPLY_FAILED = 130403;

    /**
     * 钉钉未配置有效的申请单模版
     */
    int DINGTALK_UNREGISTER_APPLY_PROCESS_CODE =130404;
    /**
     * 钉钉推送数据为空
     */
    int DINGTALK_PUSH_DATA_IS_NULL = 130405;
    /**
     * 未配置公司来源信息
     */
    int DINGTALK_UNREGISTER_COMPANY = 130406;

    int DINGTALK_PUSH_APPLY_ERROR = 130407;
    /**
     * 钉钉内嵌版未配置企业
     */
    int DINGTALK_EIA_UNDEFINED = 130408;

    //-------------以下为钉钉三方应用返回码,135前缀-----------

    /**
     * 调用钉钉异常%
     */
    int DINGTALK_ISV_DINGTALK_ERROR = 135000;

    /**
     *suite_ticket为空
     */
    int DINGTALK_ISV_SUITE_TICKET_IS_NULL = 135001;

    /**
     * 创建钉钉企业失败%s
     */
    int DINGTALK_ISV_CREATE_COMPANY_FAILED = 135002;

    /**
     * 企业未授权
     */
    int DINGTALK_ISV_COMPANY_UNDEFINED = 135003;

    /**
     * 非管理员，无法登录
     */
    int CHECK_IS_NOT_ADMIN = 135004;

    /**
     * 企业授权已过期
     */
    int COMPANY_STATE_CLOSE = 135005;

    /**
     * web授权失败%s
     */
    int WEB_AUTH_FAILED = 135006;

    /**
     * 钉钉签名错误
     */
    int DINGTALK_SIGN_ERROR = 135007;

    /**
     * 人员不存在
     */
    int DINGTALK_USER_NOT_EXISTS = 135008;

    /**
     * 获取日程失败
     */
    int GET_SCHEDULE_LIST_FAILED = 135009;

    /**
     * 获取消费信息失败
     */
    int GET_CONSUME_FAILED = 135010;

    /**
     * 未获取到锁
     */
    int GET_LOCK_FAILED = 135011;

    /**
     * 主企业id不存在
     */
    int DINGTALK_ISV_COMPANY_MAINCORPID_NOTEXIST = 135012;

    /**
     * 钉钉企业未注册
     */
    int CORP_INALID = 100001;

    /**
     * 接入未初始化完成
     */
    int CORP_UNINITIALIZED = 100002;

    /**
     * 未配置个人用户登陆企业
     */
    int PERSON_COMPANY_UNCONFIGURATION = 100003;





}
