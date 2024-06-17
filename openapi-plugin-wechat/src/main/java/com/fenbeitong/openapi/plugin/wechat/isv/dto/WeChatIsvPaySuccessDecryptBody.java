package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 订单支付
 */
@Data
@XStreamAlias("xml")
public class WeChatIsvPaySuccessDecryptBody {

    // 第三方应用的SuiteId
    @XStreamAlias("SuiteId")
    private String suiteId;
    @XStreamAlias("BuyerCorpId")
    private String buyerCorpId;
    @XStreamAlias("InfoType")
    private String infoType;
    @XStreamAlias("TimeStamp")
    private Long timeStamp;
    @XStreamAlias("OrderId")
    private String orderId;

}
