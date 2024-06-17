package com.fenbeitong.openapi.plugin.yiduijie.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finhub.framework.web.vo.BaseResult;
import lombok.Data;

/**
 * <p>Title: YiDuiJieResultEntity</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/03/10 15:08 PM
 */
@Data
public class YiDuiJieResultEntity<T> extends BaseResult {

    @JsonProperty("request_id")
    private String requestId;

    private Integer code;

    private String msg;

    private T data;
}
