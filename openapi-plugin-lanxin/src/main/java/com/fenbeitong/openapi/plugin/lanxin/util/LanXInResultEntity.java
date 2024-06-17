package com.fenbeitong.openapi.plugin.lanxin.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finhub.framework.web.vo.BaseResult;
import lombok.Data;

/**
 * <p>Title: LanXInResultEntlty</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/6 4:23 下午
 */
@Data
public class LanXInResultEntity<T> extends BaseResult {


    @JsonProperty("request_id")
    private String requestId;

    private Integer code;

    private String msg;

    private T data;
}
