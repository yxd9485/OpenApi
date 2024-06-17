package com.fenbeitong.openapi.plugin.welink.isv.dto;

import lombok.Data;

/**
 * Created by lizhen on 2020/4/28.
 */
@Data
public class WeLinkIsvCallbackRefreshInstanceDTO extends WeLinkIsvCallbackMarketCorpBaseDTO {
    private String instanceId;

    private String productId;

    /**
     * 过期时间。
     * 格式：yyyyMMddHHmmss
     */
    private String expireTime;

    private String trialToFormal;

    private String orderId;

}
