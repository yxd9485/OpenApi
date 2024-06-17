package com.fenbeitong.openapi.plugin.demo.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: SpRecordXmlEntity</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/12 3:52 PM
 */
@Data
public class SpRecordXmlEntity {

    @XStreamAlias("SpNo")
    private String spNo;

    @XStreamAlias("SpStatus")
    private Integer spStatus;

    @XStreamImplicit
    List<DetailsXmlEntity> detailsList;
}
