package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-11-25 17:0:57
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingtalkIsvMarketOrderDTO {

    private Long orderId;

    private String syncAction;

    private String itemCode;

    private Long maxOfPeople;

    private String itemName;

    private Long payFee;

    private Long serviceStopTime;

    private String suiteKey;

    private String goodsName;

    private Long minOfPeople;

    private Long suiteId;

    private String corpId;

    private String goodsCode;

    private Long paidtime;

    private String syncSeq;

    private String companyId;

    private String mainCorpId;//主企业id

}