package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuRedisKeyConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuTicketRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * 飞书获取签名ticket
 *
 * @author xiaohai
 * @date 2021/11/17
 */
@ServiceAspect
@Service
@Slf4j
public abstract class AbstractFeiShuJsapiService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取FeiShuHttpUtils
     *
     * @return
     */
    protected abstract AbstractFeiShuHttpUtils getFeiShuHttpUtils();

    //获取票据信息
    public String getJsapiTicket(String corpId) {
        //redis里缓存ticket (飞书返回ticket有效时长为2小时，当前ticket有效期小于半个小时时会返回新的ticket)
        String jsapiTicketKey = MessageFormat.format(FeiShuRedisKeyConstant.FEISHU_JSAPI_TICKET, corpId);
        String jsapiTicket = (String) redisTemplate.opsForValue().get( jsapiTicketKey );
        if (!StringUtils.isBlank( jsapiTicket )) {
            return jsapiTicket;
        }
        String url = feishuHost + FeiShuConstant.JSAPI_TICKET;
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, null, corpId);
        FeiShuTicketRespDTO feiShuTicketRespDTO = JsonUtils.toObj(res, FeiShuTicketRespDTO.class);
        if (feiShuTicketRespDTO == null || 0 != feiShuTicketRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_APP_TICKET_IS_NULL);
        }
        jsapiTicket = feiShuTicketRespDTO.getData().getTicket();
        // 缓存redis （缓存时间为7000秒）
        log.info("【feishu isv】 jsapiTicket,key={},value={}", jsapiTicketKey , jsapiTicket);
        redisTemplate.opsForValue().set( jsapiTicketKey , jsapiTicket );
        redisTemplate.expire( jsapiTicketKey , 7000 , TimeUnit.SECONDS);
        return jsapiTicket;
    }
}
