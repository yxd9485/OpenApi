package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 *
 * @author lizhen
 * @date 2020/6/2
 */
@Data
public class FeiShuUserBatchGetRespDTO {

    private Integer code;

    private String msg;

    private UserBatchGetRespData data;

    @Data
    public static class UserBatchGetRespData {

        @JsonProperty("user_infos")
        private List<FeiShuUserInfoDTO> userInfos;

    }

}
