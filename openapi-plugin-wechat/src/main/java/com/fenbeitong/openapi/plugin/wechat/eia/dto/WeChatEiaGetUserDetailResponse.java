package com.fenbeitong.openapi.plugin.wechat.eia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: WeChatEiaGetUserDetailResponse<p>
 * <p>Description: 微信嵌入版获取用户敏感信息<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/7/23 13:49
 */
@Data
public class WeChatEiaGetUserDetailResponse {
    /**
     * 返回码
     */
    @JsonProperty("errcode")
    private Integer errCode;
    /**
     * 对返回码的文本描述内容
     */
    @JsonProperty("errmsg")
    private String errMsg;
    /**
     * 成员UserID
     */
    @JsonProperty("userid")
    private String userId;
    /**
     * 性别。0表示未定义，1表示男性，2表示女性。仅在用户同意snsapi_privateinfo授权时返回真实值，否则返回0.
     */
    @JsonProperty("gender")
    private String gender;
    /**
     * 头像url。仅在用户同意snsapi_privateinfo授权时返回
     */
    @JsonProperty("avatar")
    private String avatar;
    /**
     * 员工个人二维码（扫描可添加为外部联系人），仅在用户同意snsapi_privateinfo授权时返回
     */
    @JsonProperty("qr_code")
    private String qrCode;
    /**
     * 手机，仅在用户同意snsapi_privateinfo授权时返回，第三方应用不可获取
     */
    @JsonProperty("mobile")
    private String mobile;
    /**
     * 邮箱，仅在用户同意snsapi_privateinfo授权时返回，第三方应用不可获取
     */
    @JsonProperty("email")
    private String email;
    /**
     * 企业邮箱，仅在用户同意snsapi_privateinfo授权时返回，第三方应用不可获取
     */
    @JsonProperty("biz_mail")
    private String bizMail;
    /**
     * 仅在用户同意snsapi_privateinfo授权时返回，第三方应用不可获取
     */
    @JsonProperty("address")
    private String address;


}
