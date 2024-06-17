package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 手机号或邮箱用户信息
 * @author xiaohai
 * @date 2022/09/09
 */
@Data
public class FeiShuUserContactRespDTO {

    @JsonProperty("code")
    private int code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private DataDTO data;

    @Data
    public static class DataDTO {
        @JsonProperty("user_list")
        private List<UserContactInfo> userList;
    }

    @Data
    public static class UserContactInfo {
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("mobile")
        private String mobile;
        @JsonProperty("email")
        private String email;
    }

}
