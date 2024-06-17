package com.fenbeitong.openapi.plugin.demo.dto;

import com.fenbeitong.openapi.plugin.util.xml.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * <p>Title: DetailsXmlEntity</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/12 3:57 PM
 */
@Data
@XStreamAlias("Details")
public class DetailsXmlEntity {

    @XStreamCDATA
    @XStreamAlias("Speech")
    private String speech;

    @XStreamAlias("SpStatus")
    private Integer spStatus;

    @XStreamAlias("SpTime")
    private Long spTime;
}
