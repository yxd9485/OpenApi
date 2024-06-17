package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finhub.framework.web.vo.BaseResult;
import lombok.Data;

/**
 * <p>Title: BeisenResultEntity</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/08/03 4:53 PM
 */
@Data
public class BeisenResultEntity<T> extends BaseResult {

    @JsonProperty("request_id")
    public String requestId;

    public Integer code;

    public String msg;

    public String scrollId;

    public Boolean isLastData;

    public Integer total;

    public T data;
}
