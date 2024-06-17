package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 飞书企业信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuCompanyInfoRespDTO {

    private Integer code;

    private String msg;

    private CompanyInfo data;

    @Data
    public static class CompanyInfo {

        @JsonProperty("tenant")
        private Tenant tenant;

    }

    @Data
    public static class Tenant {

        @JsonProperty("name")
        private String name;

        @JsonProperty("display_id")
        private String display_id;

        //个人版/团队版标志   0：团队版 2：个人版
        @JsonProperty("tenant_tag")
        private int tenant_tag;

        @JsonProperty("tenant_key")
        private String tenant_key;

        @JsonProperty("avatar")
        private Avatar avatar;

     }

     //企业头像
    @Data
    public static class Avatar {

        @JsonProperty("avatar_origin")
        private String avatar_origin;

        @JsonProperty("avatar_72")
        private String avatar_72;

        @JsonProperty("avatar_240")
        private String avatar_240;

        @JsonProperty("avatar_640")
        private String avatar_640;

    }

}