package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: YiDuiJieQueryClientResp</p>
 * <p>Description: 易对接查询客户端响应</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 5:34 PM
 */
@Data
public class YiDuiJieQueryClientResp {

    @JsonProperty("body")
    private YiDuiJieClient client;

    private String message;

    private Integer status;

    public boolean success() {
        return status != null && status == 0;
    }
}
