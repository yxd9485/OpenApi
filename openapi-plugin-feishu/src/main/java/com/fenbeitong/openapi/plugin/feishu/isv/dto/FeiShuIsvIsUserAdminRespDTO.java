package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author lizhen
 * @date 2020/6/5
 */
@Data
public class FeiShuIsvIsUserAdminRespDTO {

    private Integer code;

    private String msg;

    private IsUserAdminData data;

    @Data
    public static class IsUserAdminData {

        @JsonProperty("is_app_admin")
        private boolean isAppAdmin;

    }
}