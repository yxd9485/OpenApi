package com.fenbeitong.openapi.plugin.zhongxin.isv.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finhub.framework.web.vo.BaseResult;
import lombok.Data;

/**
 * <p>Title: WechatResultEntity</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/10 4:53 PM
 */
@Data
public class ZhongxinResultEntity<T> extends BaseResult {

    @JsonProperty("request_id")
    private String requestId;

    private Integer code;

    private String msg;

    private T data;
}
