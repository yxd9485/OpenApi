package com.fenbeitong.openapi.plugin.customize.power.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @author zhangjindong
 */
@Data
public class PowerCallBackDto {

    @JsonProperty("CostType")
    String CostType;

    @JsonProperty("HappenDate")
    String HappenDate;

    @JsonProperty("Amount")
    BigDecimal Amount;

    @JsonProperty("TaxAmount")
    BigDecimal TaxAmount;

    @JsonProperty("Type")
    int  Type;

    @JsonProperty("Mark")
    String Mark;

    @JsonProperty("CreateTime")
    String CreateTime;

    @JsonProperty("FBTOrderId")
    String FBTOrderId;

    String modedatacreatedate;

    String modedatacreatetime;

}
