package com.fenbeitong.openapi.plugin.wechat.isv.constant;

/**
 * 企业微信ISV常量
 * Created by log.chang on 2020/3/2.
 */
public interface WeChatIsvConstant {

    /**
     * 企业微信供应商后台登录跳转地址
     */
    String WEB_AUTH_SKIP_URL ="/auth/login?token=";

    // 企业后台登录获取用户信息
    String GET_WEB_LOGIN_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/get_login_info?access_token=";
    // 获取企业微信isv用户通讯录信息
    String GET_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/getuserinfo3rd?suite_access_token={suite_access_token}&code={code}";

    //查询电子发票
    String GET_INVOICE_INFO_URL = "/cgi-bin/card/invoice/reimburse/getinvoiceinfo?access_token=";

    //更新电子发票
    String UPDATE_INVOICE_STATUS_URL = "/cgi-bin/card/invoice/reimburse/updateinvoicestatus?access_token=";

    // 企业微信isv消息推送国内机票订单详情url
    String WECHAT_ISV_MESSAGE_URL_ORDER_AIR = "/weChatLogin?url=domesticAir/flightOrderDetail?order_id=";

    // 企业微信isv消息推送酒店订单详情url
    String WECHAT_ISV_MESSAGE_URL_ORDER_HOTEL = "/weChatLogin?url=hotel/order/detail?order_id=";

    // 企业微信isv消息推送火车订单详情url
    String WECHAT_ISV_MESSAGE_URL_ORDER_TRAIN = "/weChatLogin?url=train/orderDetail?order_id=";

    // 企业微信isv消息推送用车订单详情url
    String WECHAT_ISV_MESSAGE_URL_ORDER_TAXI = "/weChatLogin?url=car/orderDetail?order_id=";

    // 企业微信isv消息推送申请单详情url
    String WECHAT_ISV_MESSAGE_URL_APPLICATION_DETAIL = "/weChatLogin?url=application/detail?apply_id=";

    // 企业微信isv消息推送差旅审批详情url
    String WECHAT_ISV_MESSAGE_URL_APPLICATION_TRIP = "/weChatLogin?url=application/trip/detail?apply_id=";

    // 企业微信isv消息推送用车审批详情url
    String WECHAT_ISV_MESSAGE_URL_APPLICATION_TAXI = "/weChatLogin?url=application/taxi/detail?apply_id=";

    String WECHAT_ISV_APP_HOME = "/weChatLogin?";

    String WECHAT_ISV_INSTALL_REDIRECT_UL = "/weixin/install/auth";

    String WECHAT_ISV_OPEN_PAY_REDIRECT_UL = "/account/weixinpay/result?order_id={0}";

    String PROVIDER_TOKEN_KEY ="PROVIDER-TOKEN-KEY";

    String WECHAT_ISV_USER_NAME = "企业微信用户";

    // 消息类型-审批
    String WECHAT_ISV_MSG_TYPE_APPLY = "apply";

    // 消息类型-订单
    String WECHAT_ISV_MSG_TYPE_ORDER = "order";

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
    String WECHAT_ISV_CREATE_USER = "wechat_isv_create_user";

    /**
     * 通讯录更改用户
     */
    String WECHAT_ISV_UPDATE_USER = "wechat_isv_update_user";

    /**
     * 通讯录用户离职
     */
    String WECHAT_ISV_DELETE_USER = "wechat_isv_delete_user";
    /**
     * 通讯录企业部门创建
     */
    String WECHAT_ISV_ORG_DEPT_CREATE = "wechat_isv_create_party";
    /**
     * 通讯录企业部门修改
     */
    String WECHAT_ISV_ORG_DEPT_MODIFY = "wechat_isv_update_party";
    /**
     * 通讯录企业部门删除
     */
    String WECHAT_ISV_ORG_DEPT_REMOVE = "wechat_isv_delete_party";
    /**
     * suite_ticket
     */
    String WECHAT_SUITE_TICKET = "suite_ticket";

    /**
     * 企业ISV授权
     */
    String WECHAT_CREATE_AUTH = "create_auth";

    /**
     * 企业ISV变更授权
     */
    String WECHAT_CHANGE_AUTH = "change_auth";

    /**
     * 取消授权
     */
    String WECHAT_CANCEL_AUTH = "cancel_auth";

    /**
     * 通讯录变更
     */
    String WECHAT_CHANGE_CONTACT = "change_contact";

    /**
     * 付费版本变更通知
     */
    String WECHAT_CHANGE_EDITON = "change_editon";

    /**
     * 支付成功通知
     */
    String WECHAT_PAY_FOR_APP_SUCCESS = "pay_for_app_success";

    /**
     * 付费版本变更通知
     */
    String WECHAT_OPENPAY_SUCCESS = "openpay_success";

    /**
     * 异步任务回调通知
     */
    String WECHAT_ISV_BATCH_JOB_RESULT = "batch_job_result";

    /**
     * job类型，通讯录转译
     */
    String WECHAT_ISV_JOB_TYPE_CONTACT_ID_TRANSLATE = "contact_id_translate";

    /**
     * 根部门标识
     */
    long DEPARTMENT_ROOT = 1;

}
