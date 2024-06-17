package com.fenbeitong.openapi.plugin.welink.isv.constant;

/**
 * welink isv常量
 */
public interface WeLinkIsvConstant {

    /**CORP_AUTH_EVENT
     * 企业授权事件
     */
    String CORP_AUTH_EVENT = "corpAuth";

    /**
     * 企业解除授权事件
     */
    String CORP_CANCEL_AUTH_EVENT = "corpCancelAuth";

    /**
     * 人员信息更改
     */
    String CORP_EDIT_USER_EVENT = "corpEditUser";

    /**
     * 人员信息删除
     */
    String CORP_DEL_USER_EVENT = "corpDelUser";

    /**
     * 部门信息更改
     */
    String CORP_EDIT_DEPT_EVENT = "corpEditDept";

    /**
     * 部门信息删除
     */
    String CORP_DEL_DEPT_EVENT = "corpDelDept";

    /**
     * 商城新购
     */
    String CORP_ACTIVITY_NEW_INSTANCE = "newInstance";

    /**
     * 商品续费
     */
    String CORP_ACTIVITY_REFRESH_INSTANCE = "refreshInstance";

    /**
     * 商品过期
     */
    String CORP_ACTIVITY_EXPIRE_INSTANCE = "expireInstance";

    /**
     * 商品资源释放
     */
    String CORP_ACTIVITY_RELEASE_INSTANCE = "releaseInstance";

    /**
     * 企业微信供应商后台登录跳转地址
     */
    String WEB_AUTH_SKIP_URL = "/auth/login?token=";
    /**
     * 获取access_token
     */
    String ACCESS_TOKEN_URL = "/api/auth/v2/tickets";

    /**
     * 获取企业详细信息
     */
    String GET_TENANTS_URL = "/api/tenant/v1/tenants";

    /**
     * auth_code换access_token
     */
    String AUTH_CODE_TO_ACCESS_TOKEN_URL = "/api/oauth2/v1/token";

    /**
     * access_token查是否为管理员
     */
    String ACCESS_TOKEN_TO_IS_ADMIN = "/api/weopen/v1/isadmin";

    /**
     * access_token换user
     */
    String ACCESS_TOKEN_TO_USER_URL = "/api/auth/v1/userid";

    /**
     * 免登授权码查询用户userId
     */
    String CODE_TO_USER_URL = "/api/auth/v2/userid";

    /**
     * 查询用户基本信息
     */
    String USER_SIMPLE_URL = "/api/contact/v1/users/simple";

    /**
     * 后台免登回调url
     */
    String CALLBACK_BUSINESS_URL = "/welink/isv/callback/business";

    /**
     * 查询子部门信息
     */
    String DEPARTMENTS_LIST_URL = "/api/contact/v2/departments/list";

    /**
     * 查询部门人员信息列表
     */
    String USERS_LIST_URL = "/api/contact/v2/user/users";

    /**
     * 获取用户邮箱信息
     */
    String URERS_EMAIL_URL = "/api/contact/v1/users/email";

    /**
     * 查询部门详情
     */
    String DEPARTMENTS_URL = "/api/contact/v1/departments/{deptCode}";

    /**
     * 公众号消息接口
     */
    String SEND_MESSAGE_URL = "/api/messages/v3/send";


    // 消息推送国内机票订单详情url
    String MESSAGE_URL_ORDER_AIR = "/weLinkLogin?url=domesticAir/flightOrderDetail?order_id=";

    // 消息推送酒店订单详情url
    String MESSAGE_URL_ORDER_HOTEL = "/weLinkLogin?url=hotel/order/detail?order_id=";

    // 消息推送火车订单详情url
    String MESSAGE_URL_ORDER_TRAIN = "/weLinkLogin?url=train/orderDetail?order_id=";

    // 消息推送用车订单详情url
    String MESSAGE_URL_ORDER_TAXI = "/weLinkLogin?url=car/orderDetail?order_id=";

    // 订单列表页url
    String MESSAGE_URL_ORDER = "/weLinkLogin?url=order";

    // 消息推送申请单详情url
    String MESSAGE_URL_APPLICATION_DETAIL = "/weLinkLogin?url=application/detail?apply_id=";

    // 消息推送差旅审批详情url
    String MESSAGE_URL_APPLICATION_TRIP = "/weLinkLogin?url=application/trip/detail?apply_id=";

    // 消息推送用车审批详情url
    String MESSAGE_URL_APPLICATION_TAXI = "/weLinkLogin?url=application/taxi/detail?apply_id=";

    // 消息类型-审批
    String MSG_TYPE_APPLY = "apply";

    // 消息类型-订单
    String MSG_TYPE_ORDER = "order";

    String WELINK_ISV_APP_HOME = "/weLinkLogin?";
}
