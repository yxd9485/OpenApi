package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 微信isv command回调
 */
@Data
@XStreamAlias("xml")
public class WeChatIsvCommandCallbackBody {

    @XStreamAlias("InfoType")
    private String infoType;

}
