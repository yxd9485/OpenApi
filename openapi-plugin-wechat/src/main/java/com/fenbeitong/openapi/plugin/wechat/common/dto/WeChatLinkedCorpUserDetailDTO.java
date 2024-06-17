package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author helu
 * @date 2022/6/29 下午7:37
 * 微信互联企业查询企业人员详情信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatLinkedCorpUserDetailDTO {

    @JsonProperty("errcode")
    public Integer errcode;

    @JsonProperty("errmsg")
    public String errmsg;

    @JsonProperty("user_info")
    public UserInfo userInfo;

    @Data
    public static class UserInfo {
        @JsonProperty("userid")
        public String userid;

        @JsonProperty("name")
        public String name;

        @JsonProperty("department")
        public List<String> department;

        @JsonProperty("mobile")
        public String mobile;

        @JsonProperty("telephone")
        public String telephone;

        @JsonProperty("email")
        public String email;

        @JsonProperty("position")
        public String position;

        @JsonProperty("corpid")
        public String corpid;

        @JsonProperty("extattr")
        public Extattr extattr;
    }


    @Data
    public static class Extattr {

        @JsonProperty("attrs")
        public List<Attrs> attrs;
    }
    @Data
    public static class Attrs {
        @JsonProperty("name")
        public String name;

        @JsonProperty("value")
        public String value;

        @JsonProperty("type")
        public Integer type;

        @JsonProperty("text")
        public Text text;

        @JsonProperty("web")
        public Web web;

    }

    @Data
    public static class Text {
        @JsonProperty("value")
        public String value;


    }

    @Data
    public static class Web {
        @JsonProperty("url")
        public String url;

        @JsonProperty("title")
        public String title;

    }


}
