package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.ticket;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.YunzhijiaTicketReqDto;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.YunzhijiaTicketRespDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.exception.YunzhijiaException;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaTicketService;
import com.fenbeitong.openapi.plugin.yunzhijia.utils.YunzhijiaHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * @Auther zhang.peng
 * @Date 2021/4/26
 */
@Slf4j
@ServiceAspect
@Service
public class YunzhijiaTicketServiceImpl implements IYunzhijiaTicketService {

    @Value("${yunzhijia.api-host}")
    private String yunzhijiaHost;

    @Autowired
    private YunzhijiaHttpUtils yunzhijiaHttpUtils;

    @Override
    public YunzhijiaTicketRespDTO analysisTicket(String corpId, String appId, String ticket) {
        YunzhijiaTicketReqDto yunzhijiaTicketReqDto = new YunzhijiaTicketReqDto();
        yunzhijiaTicketReqDto.setAppid(appId);
        yunzhijiaTicketReqDto.setTicket(ticket);
        String url = yunzhijiaHost + "/gateway/ticket/user/acquirecontext";
        YunzhijiaResponse<YunzhijiaTicketRespDTO> yunzhijiaResponse = yunzhijiaHttpUtils.postWithAppAccessToken(url, JsonUtils.toJson(yunzhijiaTicketReqDto), corpId, YunzhijiaTicketRespDTO.class);
        if (yunzhijiaResponse.getErrorCode() != 0) {
            throw new YunzhijiaException(YunzhijiaResponseCode.YUNZHIJIA_LOGIN_FAILED, yunzhijiaResponse.getError());
        }
        return yunzhijiaResponse.getData();
    }

}
