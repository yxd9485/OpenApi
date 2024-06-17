package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户详情返回结果
 * @Auther xiaohai
 * @Date 2021/11/12
 */

@Data
public class FeiShuTicketRespDTO extends FeiShuRespDTO {

    private TicketResp data;

    @Data
    public static class TicketResp{

        @JsonProperty("expire_in")
        private int expireIn;

        @JsonProperty("ticket")
        private String ticket;

    }

}
