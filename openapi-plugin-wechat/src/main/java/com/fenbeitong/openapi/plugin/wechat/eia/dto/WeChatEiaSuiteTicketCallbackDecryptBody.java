package com.fenbeitong.openapi.plugin.wechat.eia.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 指令回调解密参数
 * Created by log.chang on 2020/3/12.
 */
@Data
@XStreamAlias("xml")
public class WeChatEiaSuiteTicketCallbackDecryptBody {

    // 第三方应用的SuiteId
    @XStreamAlias("SuiteId")
    private String suiteId;
    // create_auth
    @XStreamAlias("InfoType")
    private String infoType;
    // 时间戳
    @XStreamAlias("TimeStamp")
    private Long timeStamp;
    // 授权的auth_code,最长为512字节。用于获取企业的永久授权码。5分钟内有效
    @XStreamAlias("SuiteTicket")
    private String suiteTicket;

}
