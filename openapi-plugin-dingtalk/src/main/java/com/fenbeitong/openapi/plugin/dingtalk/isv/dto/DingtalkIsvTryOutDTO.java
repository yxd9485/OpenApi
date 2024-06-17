package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * create on 2021-03-16 13:52:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingtalkIsvTryOutDTO {

    private String fromDate;

    private String buyerUnionId;

    private Integer suiteId;

    private String corpId;

    private String endDate;

    private String syncAction;

    private Integer appId;

    private String tryoutType;

    private String goodsCode;

    private String userid;

    private String syncSeq;

}