package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: WawjBaoXiaoPushReqDTO</p>
 * <p>Description: 我爱我家数据推送</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/19 3:55 PM
 */
@Data
public class WawjBaoXiaoPushReqDTO {

    @JsonProperty("company_id")
    private String companyId;

    @JsonProperty("batch_id_list")
    private List<String> batchIdList;
}
