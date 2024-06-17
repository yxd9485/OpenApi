package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.saasplus.api.model.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdApplyLogResDTO {
    //日志信息
    @JsonProperty("log")
    private String log;
    //时间
    @JsonProperty("time")
    private String time;
//    //接收人
//    private String receiver_id;
    @JsonProperty("action")
    private Integer action;
    //发送人
    @JsonProperty("sponsorId")
    private String sponsor_id;

}
