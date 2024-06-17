package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-07-10 17:15:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingtalkIsvCallbackSuiteTicketDTO {

    private String SuiteKey;

    private String EventType;

    private Integer TimeStamp;

    private String SuiteTicket;

}