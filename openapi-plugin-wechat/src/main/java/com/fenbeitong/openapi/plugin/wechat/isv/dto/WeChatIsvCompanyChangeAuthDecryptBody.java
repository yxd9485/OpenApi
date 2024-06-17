package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 企业授权变更请求参数
 * Created by log.chang on 2020/3/12.
 */
@Data
@XStreamAlias("xml")
public class WeChatIsvCompanyChangeAuthDecryptBody {

    // 第三方应用的SuiteId
    @XStreamAlias("SuiteId")
    private String suiteId;
    @XStreamAlias("InfoType")
    private String infoType;
    // 时间戳
    @XStreamAlias("TimeStamp")
    private Long timeStamp;
    // 授权方的corpid
    @XStreamAlias("AuthCorpId")
    private String authCorpId;


}
