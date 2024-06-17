package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: YiDuiJieQueryUserResp</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 5:56 PM
 */
@Data
public class YiDuiJieQueryUserResp {

    @JsonProperty("body")
    private YiDuiJieUser user;

    private String message;

    private Integer status;

    public boolean success() {
        return status != null && status == 0;
    }
}
