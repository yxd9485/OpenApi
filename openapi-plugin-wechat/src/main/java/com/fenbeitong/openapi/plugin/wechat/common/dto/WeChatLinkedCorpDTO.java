package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: WeChatLinkedCorpDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/1/14 4:04 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatLinkedCorpDTO {

    @JsonProperty("errcode")
    public Integer errcode;

    @JsonProperty("errmsg")
    public String errmsg;

    @JsonProperty("userids")
    public List<String> userids;

    @JsonProperty("department_ids")
    public List<String> departmentIds;
}
