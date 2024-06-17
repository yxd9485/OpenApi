package com.fenbeitong.openapi.plugin.definition.dto.plugin.corp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 邮件通知resp
 * @author lizhen
 */
@Data
@Builder
public class CompanyEmailConfigRespDTO {

    @JsonProperty("mail_notify")
    private Boolean mailNotify;
}
