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
public class FeiShuDepartmentListRespDTO {

    private Integer code;

    private String msg;

    private DepartmentUserDetailLis data;

    @Data
    public static class DepartmentUserDetailLis {

        @JsonProperty("has_more")
        private boolean hasMore;

        @JsonProperty("page_token")
        private String pageToken;

        @JsonProperty("user_list")
        private List<FeiShuUserInfoDTO> userInfos;

    }

}