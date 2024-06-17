package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * create on 2020-09-09 14:46:46
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvSearchContactResponse {

    private Integer errcode;

    private String errmsg;

    @JsonProperty("is_last")
    private boolean isLast;

    @JsonProperty("query_result")
    private QueryResult queryResult;

    @Data
    public static class QueryResult {

        private User user;

        private Party party;
    }

    @Data
    public static class User {

        private List<String> userid;

//        @JsonProperty("open_userid")
//        private List<String> openUserid;

    }

    @Data
    public static class Party {

        @JsonProperty("department_id")
        private List<String> departmentId;

    }

}