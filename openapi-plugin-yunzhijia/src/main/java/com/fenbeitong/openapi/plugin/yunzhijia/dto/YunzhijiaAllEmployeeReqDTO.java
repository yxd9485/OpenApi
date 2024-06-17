package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaAllEmployeeReqDTO {
    private String eid;
    private int begin;
    private int count;


}
