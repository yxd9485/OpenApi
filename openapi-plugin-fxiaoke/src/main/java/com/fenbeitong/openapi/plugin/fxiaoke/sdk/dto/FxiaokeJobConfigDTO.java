package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by hanshuqi on 2020/07/05.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeJobConfigDTO {
    String corpId;
    String companyId;
    String reqData;
    Long etlConfigId;
    String currentOpenUserId;

}
