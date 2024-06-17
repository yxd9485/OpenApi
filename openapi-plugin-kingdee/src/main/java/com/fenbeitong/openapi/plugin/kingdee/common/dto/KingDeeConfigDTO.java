package com.fenbeitong.openapi.plugin.kingdee.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: ProjectListDTo</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-08-26 14:56
 */
@Data
public class KingDeeConfigDTO {

    @JsonProperty("appId")
    public String appId;

    @JsonProperty("appSecret")
    public String appSecret;

    @JsonProperty("tokenUrl")
    public String tokenUrl;

    @JsonProperty("iteamUrl")
    public String iteamUrl;

}
