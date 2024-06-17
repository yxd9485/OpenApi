package com.fenbeitong.openapi.plugin.func.company.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: CoePsEbsCompRelationsDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/9/24 5:00 PM
 */
@Data
public class CoePsEbsCompRelationsDTO {

    @JsonProperty("relation_id")
    private String relationId;

    @JsonProperty("ps_comp_code")
    private String psCompCode;

    @JsonProperty("ps_location_code")
    private String psLocationCode;

    @JsonProperty("ebs_comp_code")
    private String ebsCompCode;

    @JsonProperty("enabled_flag")
    private String enabledFlag;

    @JsonProperty("enable_start_date")
    private String enableStartDate;

    @JsonProperty("enable_end_date")
    private String enableEndDate;

    @JsonProperty("creation_date")
    private String creationDate;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("last_update_date")
    private String lastUpdateDate;

    @JsonProperty("last_updated_by")
    private String lastUpdatedBy;

    private String attribute1;

    private String attribute2;

    private String attribute3;

    private String attribute4;

    private String attribute5;

    private String attribute6;

    private String attribute7;

    private String attribute8;

    private String attribute9;

    private String attribute10;

    @JsonProperty("interface_date")
    private String interfaceDate;

    @JsonProperty("curr_flag")
    private String currFlag;
}
