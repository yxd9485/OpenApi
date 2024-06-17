package com.fenbeitong.openapi.plugin.dingtalk.isv.constant;

/**
 * @author lizhen
 */
public interface DingtalkIsvConstant {

    /**
     * 发送消息
     */
    String SEND_MESSAGE_URL = "/open-apis/message/v4/send/";

    // 消息推送国内机票订单详情url
    String MESSAGE_URL_ORDER_AIR = "/dingTalkThirdLogin?corpId=%s&url=domesticAir/flightOrderDetail?order_id=%s";

    // 消息推送酒店订单详情url
    String MESSAGE_URL_ORDER_HOTEL = "/dingTalkThirdLogin?corpId=%s&url=hotel/order/detail?order_id=%s";

    // 消息推送火车订单详情url
    String MESSAGE_URL_ORDER_TRAIN = "/dingTalkThirdLogin?corpId=%s&url=train/orderDetail?order_id=%s";

    // 消息推送用车订单详情url
    String MESSAGE_URL_ORDER_TAXI = "/dingTalkThirdLogin?corpId=%s&url=car/orderDetail?order_id=%s";

    // 订单列表页url
    String MESSAGE_URL_ORDER = "/dingTalkThirdLogin?corpId=%s&url=order";

    // 消息推送申请单详情url
    String MESSAGE_URL_APPLICATION_DETAIL = "/dingTalkThirdLogin?DINGTALK_ISV_APP_HOMEcorpId=%s&url=application/detail?apply_id=%s&type=%s";

    // 消息推送差旅审批详情url
    String MESSAGE_URL_APPLICATION_TRIP = "/dingTalkThirdLogin?corpId=%s&url=application/trip/detail?apply_id=%s&type=%s";

    // 消息推送用车审批详情url
    String MESSAGE_URL_APPLICATION_TAXI = "/dingTalkThirdLogin?corpId=%s&url=application/taxi/detail?apply_id=%s&type=%s";

    // 消息类型-审批
    String MSG_TYPE_APPLY = "apply";

    // 消息类型-订单
    String MSG_TYPE_ORDER = "order";

    //登录页
    String DINGTALK_ISV_APP_HOME = "/dingTalkThirdLogin?corpId=%s&";

    //登录页
    String DINGTALK_EIA_APP_HOME = "/dingTalkLogin?corpId=%s&";

    String TRYOUT_TYPE_ENTERPRISE = "enterprise_tryout";

}
