package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fenbeitong.openapi.plugin.util.xml.XmlUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 企业微信回调参数
 */
@Data
@XStreamAlias("xml")
public class WeChatDataCallbackDecryptBody {

    @XStreamAlias("FromUserName")
    private String fromUserName;
    @XStreamAlias("ToUserName")
    private String toUserName;
    @XStreamAlias("CreateTime")
    private Long createTime;
    @XStreamAlias("MsgType")
    private String msgType;
    @XStreamAlias("Event")
    private String event;
    @XStreamAlias("InfoType")
    private String infoType;


    public static void main(String[] args) {
        String xml = "<xml><ToUserName><![CDATA[ww557cec61d4919573]]></ToUserName><FromUserName><![CDATA[qy0138c78ca829b99bb3e2ec3981]]></FromUserName><CreateTime>1583828421</CreateTime><MsgType><![CDATA[event]]></MsgType><AgentID>1000025</AgentID><Event><![CDATA[enter_agent]]></Event><EventKey><![CDATA[]]></EventKey></xml>";
        System.err.println(XmlUtil.xml2Object(xml, WeChatDataCallbackDecryptBody.class));
    }

}
