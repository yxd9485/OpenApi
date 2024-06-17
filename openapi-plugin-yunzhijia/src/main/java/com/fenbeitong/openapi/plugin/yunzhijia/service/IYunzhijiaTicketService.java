package com.fenbeitong.openapi.plugin.yunzhijia.service;

import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.YunzhijiaTicketDto;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.YunzhijiaTicketRespDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.YunzhijiaTicketSelfReqDto;

/**
 * @Auther zhang.peng
 * @Date 2021/4/26
 */
public interface IYunzhijiaTicketService {

    YunzhijiaTicketRespDTO analysisTicket(String corpId, String appId, String ticket);

}
