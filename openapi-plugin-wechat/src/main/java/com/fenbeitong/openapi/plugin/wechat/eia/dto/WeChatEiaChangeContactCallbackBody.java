package com.fenbeitong.openapi.plugin.wechat.eia.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 微信isv通讯录修改回调
 */
@Data
@XStreamAlias("xml")
public class WeChatEiaChangeContactCallbackBody {


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
    @XStreamAlias("ChangeType")
    private String changeType;
    @XStreamAlias("UserID")
    private String userId;
    @XStreamAlias("Id")
    private String id;


}
