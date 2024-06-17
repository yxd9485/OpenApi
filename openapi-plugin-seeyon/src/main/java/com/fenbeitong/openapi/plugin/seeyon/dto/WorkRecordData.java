package com.fenbeitong.openapi.plugin.seeyon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.seeyon.entity.SuperModel;
import com.fenbeitong.openapi.plugin.seeyon.helper.JacksonHelper;
import com.fenbeitong.openapi.plugin.support.webhook.dto.WebHookData;
import com.fenbeitong.openapi.plugin.support.webhook.dto.WebHookOrderDTO;
import lombok.*;

/**
 *
 *
 * @author Ivan
 * @version 1.0 Create by Ivan on 2019/4/1 - 19:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkRecordData  {

    @JsonProperty("webhookOrder")
    private WebHookOrderDTO webhookOrder;
    @JsonProperty("record")
    private WorkRecordData.Record record;

    @Data
    static class Record {
        private Integer applyType;
        private String applyTypeName;
        private String applyId;
        private String companyId;
        private String companyName;
        private String applyName;
        private Integer callbackType;
    }

}
