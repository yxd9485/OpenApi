package com.fenbeitong.openapi.plugin.demo.dto;

import com.fenbeitong.openapi.plugin.util.xml.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * <p>Title: ApproveXmlEntity</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/12 3:45 PM
 */
@Data
@XStreamAlias("xml")
public class ApproveXmlEntity {

    @XStreamAlias("ToUserName")
    private String toUserName;

    @XStreamCDATA
    @XStreamAlias("FromUserName")
    private String fromUserName;

    @XStreamAlias("CreateTime")
    private Long createTime;

    @XStreamAlias("ApprovalInfo")
    private ApprovalInfoXmlEntity approvalInfo;

}
