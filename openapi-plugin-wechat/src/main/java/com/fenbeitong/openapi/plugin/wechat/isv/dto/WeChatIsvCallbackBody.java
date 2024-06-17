package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 微信isv回调消息体
 * Created by log.chang on 2020/3/12.
 */
@Data
@XStreamAlias("xml")
public class WeChatIsvCallbackBody {

    @XStreamAlias("AgentID")
    private String agentID;
    @XStreamAlias("ToUserName")
    private String toUserName;
    @XStreamAlias("Encrypt")
    private String encrypt;

}
