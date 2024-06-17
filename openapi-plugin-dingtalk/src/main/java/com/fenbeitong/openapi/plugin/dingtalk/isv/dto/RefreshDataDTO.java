package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.Data;


@Data
public class RefreshDataDTO {

    private String corpId;

    private String userId;

    private String bizType;

    private String bizAsyncData;


}
