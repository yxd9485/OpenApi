package com.fenbeitong.openapi.plugin.yunzhijia.constant;

/**
 * 云之家免登跳转地址
 * @Auther zhang.peng
 * @Date 2021/7/29
 */
public class YunzhijiaUrlConstant {

    // 登录页
    public static final String YUNZHIJIA_EIA_APP_HOME = "/yunofhomeLogin?corpId=%s&appid=%s&url=%s";

    // 查询企业是否订阅公共号
    public static final String IS_COMPANY_SUBSCRIBE_OR_NOT = "https://yunzhijia.com/pubacc/api/pubssb";

    // 设置企业订阅公共号
    public static final String COMPANY_SUBSCRIBE_PUBLIC_NOTICE = "https://yunzhijia.com/pubacc/api/pubssb";
}
