package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

/**
 * 缓存相关常量类
 *
 * @author zhaokechun
 * @date 2018/11/27 10:10
 */
public interface CacheConstant {

    /**
     * 钉钉token key,dingtalk:token:corpid
     */
    String DINGTALK_TOKEN_KEY = "dingtalk:token:{0}";

    /**
     * 钉钉token过期时间 - 100分钟
     */
    int DINGTALK_TOKEN_EXPIRED = 100;

    /**
     * 企业信息-按照钉钉企业ID存储,dingtalk:corp:corpid
     */
    String DINGTALK_CORP_KEY = "dingtalk:corp:{0}";

    /**
     * 企业信息-按照分贝通企业ID存储,dingtalk:company:companyid
     */
    String DINGTALK_COMPANY_KEY = "dingtalk:company:{0}";

    /**
     * 企业信息过期时间 - 120分钟
     */
    int DINGTALK_CORP_EXPIRED = 120;

    /**
     * OPEN API token, openapi:token:companyid
     */
    String OPENAPI_TOKEN_KEY = "openapi:token:{0}";

    /**
     * OPEN API token过期时间, 120分钟
     */
    int OPENAPI_TOKEN_EXPIRED = 120;
    /**
     * 企业微信人员过期时间 分钟
     */
    int QYWX_USER_EXPIRED = 20;

    /**
     * 城市列表
     */
    String CITY_LIST = "city:list";

    /**
     * 城市列表过期时间
     */
    int CITY_LIST_EXPIRED = 480;

    /**
     * 路由信息表
     */
    String DINGTALK_ROUTE_KEY = "dingtalk:route:{0}";

    /**
     * 路由过期时间，15分钟
     */
    int DINGTALK_ROUTE_EXPIRED = 15;


    String QYWXAPI_TOKEN_KEY = "qywxapi:token:{0}";

    /**
     * 加班发券部门
     */
    String DINGTALK_ATTENDANCE_GRANT_VOUCHER_ORGID = "openapi_plugin.grant_voucher_orgid:{0}";

    /**
     * suite_ticket
     */
    String DINGTALK_ISV_SUITE_TICKET = "dingtalk_isv_suite_ticket";

    Object DINGTALK_EIA_SUITE_TICKET = "dingtalk_eia_suite_ticket";

    /**
     * 第三方应用凭
     */
    String DINGTALK_ISV_SUITE_ACCESS_TOKEN = "dingtalk_isv_suite_access_token";

    /**
     * corp_access_token
     */
    String DINGTALK_ISV_CORP_ACCESS_TOKEN = "dingtalk_isv_corp_access_token:{0}";

    String DINGTALK_EIA_CORP_ACCESS_TOKEN = "dingtalk_eia_corp_access_token:{0}";

    /**
     * 免登secret
     */
    String DINGTALK_ISV_SSO_SECRET = "dingtalk_isv_sso_secret";
}
