package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用户详情返回结果
 * @Auther zhang.peng
 * @Date 2021/9/13
 */

@Data
public class FeiShuUserDetailRespDTO extends FeiShuRespDTO {

    private UserDetailResp data;

    @Data
    public static class UserDetailResp{

        @JsonProperty("email_users")
        private Map<String, List<UserInfo>> emailUsers;

        @JsonProperty("mobile_users")
        private Map<String, List<UserInfo>> mobileUsers;

        @JsonProperty("emails_not_exist")
        private List<String> emailsNotExist;

        @JsonProperty("mobiles_not_exist")
        private List<String> mobilesNotExist;
    }

    @Data
    public static class UserInfo{
        @JsonProperty("open_id")
        private String openId;
        @JsonProperty("user_id")
        private String userId;
    }
}
