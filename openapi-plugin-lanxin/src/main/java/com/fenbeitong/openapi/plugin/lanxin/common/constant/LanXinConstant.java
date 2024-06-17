package com.fenbeitong.openapi.plugin.lanxin.common.constant;

/**
 * <p>Title: Concant</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/6 3:00 下午
 */
public class LanXinConstant {
    /**
     * 人员访问token
     */
    public static String USER_TOKEN_URL = "/v1/usertoken/create";
    /**
     * 应用访问token
     */
    public static String APPLY_TOKEN_URL = "/v1/apptoken/create";
    /**
     * 人员信息
     */
    public static String USER_INFO_URL = "/v1/users/fetch";
    /**
     * 发送应用消息
     */
    public static String SEND_MSG_URL = "/v1/messages/create";
    /**
     * 通过手机号获取staffId
     */
    public static String GET_STAFFID_BY_PHONE = "/v2/staffs/id_mapping/fetch";

    public static String SUCCESS = "0";

    /**
     * 登录页
     */
    public static String LANXIN_EIA_APP_HOME = "/LanXinLogin?corpId=%s&";


}
