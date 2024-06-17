package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fenbeitong.openapi.plugin.util.xml.XmlUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信isv回调参数
 * Created by log.chang on 2020/3/10.
 */
@Data
@XStreamAlias("xml")
public class WeChatIsvDataCallbackDecryptBody {

    @XStreamAlias("AgentID")
    private Integer agentID;
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
    @XStreamAlias("EventKey")
    private String eventKey;


    public static void main(String[] args) {
        String xml = "<xml><ToUserName><![CDATA[ww557cec61d4919573]]></ToUserName><FromUserName><![CDATA[qy0138c78ca829b99bb3e2ec3981]]></FromUserName><CreateTime>1583828421</CreateTime><MsgType><![CDATA[event]]></MsgType><AgentID>1000025</AgentID><Event><![CDATA[enter_agent]]></Event><EventKey><![CDATA[]]></EventKey></xml>";
        WeChatIsvDataCallbackDecryptBody w = (WeChatIsvDataCallbackDecryptBody)XmlUtil.xml2Object(xml, WeChatIsvDataCallbackDecryptBody.class);
        List<WeChatIsvDataCallbackDecryptBody> list = new ArrayList<>();
        list.add(w);

        System.err.println(XmlUtil.xml2Object(xml, WeChatIsvDataCallbackDecryptBody.class));
    }

}
