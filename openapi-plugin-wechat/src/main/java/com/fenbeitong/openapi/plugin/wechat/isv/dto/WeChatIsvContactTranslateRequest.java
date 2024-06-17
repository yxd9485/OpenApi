package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * create on 2020-09-22 16:44:1
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvContactTranslateRequest {

    @JsonProperty("auth_corpid")
    private String authCorpid;

    @JsonProperty("media_id_list")
    private List<String> mediaIdList;

    @JsonProperty("output_file_name")
    private String outputFileName;


}