package com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2021-04-30 15:17:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaTicketRespDTO {

    private String appid;

    private String xtid;

    private String oid;

    private String eid;

    private String username;

    private String userid;

    private String uid;

    private String tid;

    private String jobNo;

    private String networkid;

    private String deviceId;

    private String openid;

    private String ticket;

}