package com.fenbeitong.openapi.plugin.zhongxin.isv.util;

/**
 * 返回码
 */
public interface ZhongxinResponseCode {

    /**
     * 加密失败
     */
    Integer ENCODE_ERROR = 200001;

    /**
     * 解密失败
     */
    Integer DECODE_ERROR = 200002;

    /**
     * 验签失败
     */
    Integer VERIFY_ERROR = 200003;

    /**
     * 创建企业失败
     */
    Integer ZHONG_XIN_ISV_CREATE_COMPANY_FAILED = 200004;

    /**
     * 获取短信失败
     */
    Integer GET_MESSAGE_FAILED = 200005;

    /**
     * 授权码授权失败
     */
    Integer VERIFY_MESSAGE_FAILED = 200006;

    /**
     * 中信银行增加员工失败
     */
    Integer EMPLOYEE_ADD_FAILED = 200007;

    /**
     * 获取锁失败
     */
    Integer GET_LOCK_FAILED = 200008;

    /**
     * 企业未授权
     */
    Integer ZHONG_XIN_ISV_COMPANY_UNDEFINED = 200009;

    /**
     * 用户不存在
     */
    Integer ZHONG_XIN_ISV_USER_QUERY_FAILED = 200010;

    /**
     * 查询三方用户不存在
     */
    Integer ZHONG_XIN_THIRD_USER_QUERY_FAILED = 200011;

    /**
     * 请求三方通讯异常
     */
    Integer ZHONG_XIN_REQUEST_FAILED = 200012;

    /**
     * 链接超时失效
     */
    Integer AES_ERROR_OVER_TIME = 200013;

    /**
     * 用户已授权
     */
    Integer USER_AUTH_EXIST = 200014;

    /**
     * 系统异常
     */
    Integer SYSTEM_ERROR = 999999;
}
