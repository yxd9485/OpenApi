package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 获取企业授权信息返回参数
 * create on 2020-03-19 14:10:5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthInfoResponse {

    private Integer errcode;

    private String errmsg;

    @JsonProperty("dealer_corp_info")
    private DealerCorpInfo dealerCorpInfo;

    @JsonProperty("auth_corp_info")
    private AuthCorpInfo authCorpInfo;

    @JsonProperty("edition_info")
    private EditionInfo editionInfo;

    @JsonProperty("auth_info")
    private AuthInfo authInfo;

    @Data
    public static class DealerCorpInfo {

        private String corpid;

        @JsonProperty("corp_name")
        private String corpName;

    }

    @Data
    public static class AuthCorpInfo {

        private String corpid;

        @JsonProperty("corp_name")
        private String corpName;

        @JsonProperty("corp_type")
        private String corpType;

        @JsonProperty("corp_square_logo_url")
        private String corpSquareLogoUrl;

        @JsonProperty("corp_user_max")
        private Integer corpUserMax;

        @JsonProperty("corp_agent_max")
        private Integer corpAgentMax;

        @JsonProperty("corp_full_name")
        private String corpFullName;

        @JsonProperty("verified_end_time")
        private Long verifiedEndTime;

        @JsonProperty("subject_type")
        private Integer subjectType;

        @JsonProperty("corp_wxqrcode")
        private String corpWxqrcode;

        @JsonProperty("corp_scale")
        private String corpScale;

        @JsonProperty("corp_industry")
        private String corpIndustry;

        @JsonProperty("corp_sub_industry")
        private String corpSubIndustry;

        private String location;

    }

    /**
     * create on 2020-03-19 14:10:5
     */
    @Data
    public static class Privilege {

        private Integer level;

        @JsonProperty("allow_party")
        private List<Integer> allowParty;

        @JsonProperty("allow_user")
        private List<String> allowUser;

        @JsonProperty("allow_tag")
        private List<Integer> allowTag;

        @JsonProperty("extra_party")
        private List<Integer> extraParty;

        @JsonProperty("extra_user")
        private List<String> extraUser;

        @JsonProperty("extra_tag")
        private List<Integer> extraTag;

    }

    @Data
    public static class Agent {

        private Long agentid;

        private String name;

        @JsonProperty("round_logo_url")
        private String roundLogoUrl;

        @JsonProperty("square_logo_url")
        private String squareLogoUrl;

        private Long appid;

        private Privilege privilege;


        /**
         * 付费版
         */
        @JsonProperty("edition_id")
        private String editionId;

        @JsonProperty("edition_name")
        private String editionName;

        @JsonProperty("app_status")
        private Integer appStatus;

        @JsonProperty("user_limit")
        private Integer userLimit;

        @JsonProperty("expired_time")
        private Integer expiredTime;


    }
    @Data
    public static class AuthInfo {

        private List<Agent> agent;

    }

    @Data
    public static class EditionInfo {
        private List<EditionAgent> agent;
    }


    @Data
    public static class EditionAgent {

        private Long agentid;

        @JsonProperty("edition_id")
        private String editionId;

        @JsonProperty("edition_name")
        private String editionName;

        @JsonProperty("app_status")
        private Integer appStatus;

        @JsonProperty("user_limit")
        private Long userLimit;

        @JsonProperty("expired_time")
        private Long expiredTime;

        @JsonProperty("is_virtual_version")
        private boolean isVirtualVersion;

    }


}