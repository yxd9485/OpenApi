package com.fenbeitong.openapi.plugin.wechat.common;

/**
 * Created by dave.hansins on 19/12/25.
 */
public interface WeChatApiResponseCode {
    /**
     * msg.403=Token失效或者企业ID不存在，请登录
     */
    String TOKEN_INFO_IS_ERROR = "403";

    /**
     * msg.120001=sign签名不正确
     */
    String SIGN_ERROR = "120001";

    /**
     * 未查到企业信息
     */
    String WECHAT_CORP_INFO_NOT_EXIST = "120002";

    /**
     * wechat.msg.120004=用车城市名称为空
     */
    String CAR_CITY_IS_NULL="120004";
    /**
     * wechat.msg.120005=corp_id为空
     */
    String WECHAT_CORP_ID_NOT_EXIST ="120005";
    /**
     * wechat.msg.120006=corp_access_token为空
     */
    String WECHAT_CORP_ID_NOT_NULL = "120006";
    /**
     * wechat.msg.120007=corp_access_token为空
     */
    String WECHAT_CORP_ACCESS_TOKEN_IS_NULL = "120007";
    /**
     * wechat.msg.120008=corp_approvol为空
     */
    String WECHAT_CORP_APPROVOL_IS_NULL = "120008";

    /**
     * wechat.msg.120008=分贝员工不存在
     */
    String WECHAT_FB_USER_IS_NOT_EXIST = "120008";


    /**
     * 该用户尚未加入该企业组织架构中
     */
    String  USER_NOT_IN_COMPANY = "10001";
    /**
     * 用户第三方ID不存在
     */
    String USER_THIRD_ID_NOT_EXISTS = "100007";
    /**
     * wechat.msg.120009=创建审批单异常
     */
    String CREATE_APPLY_ERROR="120009";

    /**
     * 获取企业微信全量部门数据异常
     */
    String WECHAT_CORP_DEPT_IS_NULL = "120010";
    /**
     * 获取企业微信全量人员数据异常
     */
    String WECHAT_CORP_EMPLOYEE_IS_NULL = "120011";

    /**
     * 企业微信用户信息获取失败
     */
    String WECHAT_CORP_EMPLOYEE_NOT_EXISTS = "120012";

    /**
     * 获取企业微信用户详情失败
     */
    String WECHAT_EIA_GET_WECHAT_USER_DETAIL_FAIL = "120013";

    /**
     * 获取用户手机号失败
     */
    String WECHAT_EIA_GET_WECHAT_USER_MOBILE_FAIL = "120014";
    /**
     * 请求UC获取员工信息失败
     */
    String WECHAT_EIA_GET_UC_USER_INFO_FAIL = "120015";
    /**
     * 更新用户手机号失败
     */
    String WECHAT_EIA_UPDATE_USER_PHONE_FAIL = "120016";

    /**
     * 获取suite_access_token异常
     */
    String WECHAT_SUITE_ACCESS_TOKEN_IS_NULL = "120101";

    /**
     * 获取stuite_ticket异常
     */
    String WECHAT_SUITE_TICKET_IS_NULL = "120102";

    /**
     * 获取permanent异常
     */
    String WECHAT_PERMANENT_IS_NULL = "120103";

    /**
     * 企业未授权
     */
    String WECHAT_ISV_COMMPANY_NOT_EXISTS = "120104";

    /**
     * 获取access_token异常
     */
    String WECHAT_ISV_ACCESS_TOKEN_IS_NULL = "120105";

    /**
     * isv企业不存在
     */
    String WECHAT_ISV_COMPANY_UNDEFINED = "120106";

    /**
     * 企业非可对接状态
     */
    String WECHAT_ISV_COMPANY_STATE_CLOSE = "120107";

    /**
     * 企业微信异常
     */
    String WECHAT_ISV_ERROR = "120108";

    /**
     * 获取企业微信isv全量部门数据异常
     */
    String WECHAT_ISV_CORP_DEPT_IS_NULL = "120109";

    /**
     * 获取企业微信isv人员数据异常
     */
    String WECHAT_ISV_CORP_EMPLOYEE_IS_NULL = "120110";

    /**
     * 获取企业jsapi_ticket异常
     */
    String WECHAT_ISV_ENTERPRISE_JSAPI_TICKET_IS_NULL = "120111";

    /**
     * 获取企业微信应用jsapi_ticket异常
     */
    String WECHAT_ISV_AGENT_JSAPI_TICKET_IS_NULL = "120112";

    /**
     * 企业微信isv推送消息失败
     */
    String WECHAT_ISV_SEND_MESSAGE_FAILED = "120113";


    /**
     * 企业微信isv uc创建企业异常
     */
    String WECHAT_ISV_CREATE_COMPANY_FAILED = "120114";

    /**
     * 授权登录失败
     */
    String WECHAT_ISV_LOGIN_FAILEG = "120115";

    /**
     * 企业微信isv获取发票失败
     */
    String WECHAT_ISV_GET_INVOICE_INFO_FAILED = "120116";


    /**
     * 企业微信isv更新发票状态失败
     */
    String WECHAT_ISV_UPDATE_INVOICE_STATUS_FAILED = "120117";

    /**
     * 企业微信isv文件上传失败 %s
     */
    String WECHAT_ISV_UPDATE_FILE_FAILED = "120118";

    /**
     * 企业微信isv通讯录转译失败 %s
     */
    String WECHAT_ISV_CONTACT_TRANSLATE_FAILED = "120119";

    /**
     * 企业微信isv获取转译结果失败 %s
     */
    String WECHAT_ISV_GET_TRANSLATE_RESULT_FAILED = "120120";

    /**
     * 上传文件重命名失败
     */
    String WECHAT_ISV_RANSLATE_FILE_RENAME_FAILED = "120121";


    /**
     * 充值金额不能小于0
     */
    String WECHAT_ISV_RECHARGE_PRICE_ERROR = "120122";

    /**
     * 获取订单失败
     */
    String WECHAT_ISV_GET_ORDER_FAILED = "120123";


    /**
     * 企业微信下单失败
     */
    String WECHAT_ISV_OPEN_PAY_FAILED = "120124";

    /**
     * 获取分贝订单状态失败
     */
    String WECHAT_ISV_GET_FBT_ORDER_FAILED = "120125";

    /**
     * 用户非管理员无法登录
     */
    String WECHAT_ISV_USER_NOT_ADMIN = "120126";

    /**
     * 参数异常
     */
    String WECHAT_ARGUMENT_INCORRECT = "120127";


}
