package com.fenbeitong.openapi.plugin.voucher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>Title: FinanceBaseRespDto</p>
 * <p>Description: 财务响应基础类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 11:38 AM
 */
public class FinanceBaseRespDto {

    @JsonProperty("request_id")
    protected String requestId;

    protected Integer code;

    protected Integer type;

    protected String msg;

    public String getRequestId() {
        return requestId;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public boolean success() {
        return code != null && code == 0;
    }
}
