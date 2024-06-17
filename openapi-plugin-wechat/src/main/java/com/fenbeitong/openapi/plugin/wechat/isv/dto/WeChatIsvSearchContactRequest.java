package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by lizhen on 2020/9/9.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvSearchContactRequest {

    @JsonProperty("auth_corpid")
    private String authCorpid;

    @JsonProperty("query_word")
    private String queryWord;

    private Integer agentid;

    private Integer offset;

    private Integer limit;

    /**
     * 查询类型 1：查询用户，返回用户userid列表 2：查询部门，返回部门id列表。
     */
    @JsonProperty("query_type")
    private Integer queryType;
}
