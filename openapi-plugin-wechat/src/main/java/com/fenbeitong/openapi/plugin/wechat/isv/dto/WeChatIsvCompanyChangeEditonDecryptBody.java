package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 付费版本变更通知
 */
@Data
@XStreamAlias("xml")
public class WeChatIsvCompanyChangeEditonDecryptBody {

    // 第三方应用的SuiteId
    @XStreamAlias("SuiteId")
    private String suiteId;
    // 购买方corpid
    @XStreamAlias("PaidCorpId")
    private String paidCorpId;
    // 固定为 change_editon
    @XStreamAlias("InfoType")
    private String infoType;
    // 时间戳
    @XStreamAlias("TimeStamp")
    private Long timeStamp;

}
