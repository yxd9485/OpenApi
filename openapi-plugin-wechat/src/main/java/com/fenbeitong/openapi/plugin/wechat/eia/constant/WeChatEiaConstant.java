package com.fenbeitong.openapi.plugin.wechat.eia.constant;

/**
 * 企业微信ISV常量
 * Created by log.chang on 2020/3/2.
 */
public interface WeChatEiaConstant {

    // 企业微信EIA消息推送国内机票订单详情url
    String WECHAT_EIA_MESSAGE_URL_ORDER_AIR = "/weChatSelfLogin?corpId=%s&url=domesticAir/flightOrderDetail?order_id=%s";

    // 企业微信EIA消息推送酒店订单详情url
    String WECHAT_EIA_MESSAGE_URL_ORDER_HOTEL = "/weChatSelfLogin?corpId=%s&url=hotel/order/detail?order_id=%s";

    // 企业微信EIA消息推送火车订单详情url
    String WECHAT_EIA_MESSAGE_URL_ORDER_TRAIN = "/weChatSelfLogin?corpId=%s&url=train/orderDetail?order_id=%s";

    // 企业微信EIA消息推送用车订单详情url
    String WECHAT_EIA_MESSAGE_URL_ORDER_TAXI = "/weChatSelfLogin?corpId=%s&url=car/orderDetail?order_id=%s";

    // 企业微信EIA消息推送申请单详情url
    String WECHAT_EIA_MESSAGE_URL_APPLICATION_DETAIL = "/weChatSelfLogin?corpId=%s&url=application/detail?apply_id=%s";

    // 企业微信EIA消息推送差旅审批详情url
    String WECHAT_EIA_MESSAGE_URL_APPLICATION_TRIP = "/weChatSelfLogin?corpId=%s&url=application/trip/detail?apply_id=%s";

    // 企业微信EIA消息推送用车审批详情url
    String WECHAT_EIA_MESSAGE_URL_APPLICATION_TAXI = "/weChatSelfLogin?corpId=%s&url=application/taxi/detail?apply_id=%s";

    public static final String MESSAGE_URL_ORDER = "/weChatSelfLogin?corpId=%s&url=order";

    /**
     * 企业微信详情免登uri
     */
    public static final String WECHAT_EIA_APP_HOME = "/weChatSelfLogin?corpId=%s&";
    /**
     * 企业微信根据敏感信息（手机号）免登uri
     */
    public static final String WECHAT_EIA_SENSITIVE_APP_HOME = "/weChatSelfLogin?corpId=%s&agentid=%s&sensitiveFlag=%s&";


    /**
     * 根部门标识
     */
    long DEPARTMENT_ROOT = 1;
}
