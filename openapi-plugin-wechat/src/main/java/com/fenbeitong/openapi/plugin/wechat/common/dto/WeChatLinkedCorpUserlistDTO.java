package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: WeChatLinkedCorpUserlistDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/1/14 4:35 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatLinkedCorpUserlistDTO {

    @JsonProperty("errcode")
    public Integer errcode;

    @JsonProperty("errmsg")
    public String errmsg;

    @JsonProperty("userlist")
    public List<userBean> userlist;

    @Data
    public static class userBean {
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
