package com.fenbeitong.openapi.plugin.demo.dto;

import com.fenbeitong.openapi.plugin.util.xml.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * <p>Title: ApprovalInfoXmlEntity</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/12 3:48 PM
 */
@Data
public class ApprovalInfoXmlEntity {

    @XStreamCDATA
    @XStreamAlias("SpNo")
    private String spNo;

    @XStreamCDATA
    @XStreamAlias("SpName")
    private String spName;

    @XStreamAlias("SpRecord")
    private SpRecordXmlEntity spRecord;


}
