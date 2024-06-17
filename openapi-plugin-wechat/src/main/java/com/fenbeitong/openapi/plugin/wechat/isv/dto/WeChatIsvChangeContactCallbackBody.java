package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 微信isv通讯录修改回调
 */
@Data
@XStreamAlias("xml")
public class WeChatIsvChangeContactCallbackBody {

    @XStreamAlias("SuiteId")
    private String suiteId;

    @XStreamAlias("AuthCorpId")
    private String authCorpId;

    @XStreamAlias("InfoType")
    private String infoType;

    @XStreamAlias("TimeStamp")
    private String timeStamp;

    @XStreamAlias("ChangeType")
    private String changeType;

    @XStreamAlias("UserID")
    private String userID;

    @XStreamAlias("Id")
    private String id;


}
