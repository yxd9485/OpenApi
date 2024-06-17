package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 企业后台授权登录用户信息请求参数
 * Created by log.chang on 2020/3/23.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeChatIsvLoginInfoResponse {

    @JsonProperty("errcode")
    private Integer errCode;
    @JsonProperty("errmsg")
    private String errMsg;
    @JsonProperty("usertype")
    private Integer userType;

    @JsonProperty("user_info")
    private LoginInfoUserInfo userInfo;
    @JsonProperty("corp_info")
    private LoginInfoCorpInfo corpInfo;
    @JsonProperty("agent")
    private List<LoginInfoAgent> agentList;
    @JsonProperty("auth_info")
    private LoginInfoAuthInfo authInfo;

    @Data
    public static class LoginInfoUserInfo {
        @JsonProperty("userid")
        private String userId;
        private String name;
        private String avatar;
    }

    @Data
    public static class LoginInfoCorpInfo {
        @JsonProperty("corpid")
        private String corpId;
    }

    @Data
    public static class LoginInfoAgent {
        @JsonProperty("agentid")
        private Integer agentId;
        @JsonProperty("auth_type")
        private Integer authType;
    }

    @Data
    public static class LoginInfoAuthInfo {
        @JsonProperty("department")
        private List<LoginInfoAuthInfoDepartment> departmentList;
    }

    @Data
    public static class LoginInfoAuthInfoDepartment {
        private String id;
        private String writable;
    }

}
