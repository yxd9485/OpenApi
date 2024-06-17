package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.apply;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.*;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token.YunzhijiaTokenService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
@Slf4j
public class YunzhijiaRemoteApplyService {

    @Autowired
    YunzhijiaTokenService yunzhijiaTokenService;

    @Value("${yunzhijia.api-host}")
    private String yunzhijiaHost;
    @Autowired
    RestHttpUtils restHttpUtils;

    /**
     * 查询云之家审批单详情
     *
     * @param yunzhijiaAccessTokenReqDTO
     * @param formCodeId
     * @param formInstId
     * @return
     */
    public YunzhijiaApplyEventDTO getYunzhijiaRemoteApplyDetail(YunzhijiaAccessTokenReqDTO yunzhijiaAccessTokenReqDTO, String formCodeId, String formInstId) {
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaRemoteAccessToken = yunzhijiaTokenService.getYunzhijiaRemoteAccessToken(yunzhijiaAccessTokenReqDTO);
        if (yunzhijiaRemoteAccessToken.getErrorCode() == RespCode.SUCCESS) {
            String accessToken = yunzhijiaRemoteAccessToken.getData().getAccessToken();
            String url = yunzhijiaHost + "/gateway/workflow/form/thirdpart/viewFormInst?accessToken=" + accessToken;
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("formCodeId", formCodeId);
            paramMap.put("formInstId", formInstId);
            String paramStr = JsonUtils.toJson(paramMap);
            log.info("调用云之家获取审批单详情请求参数 :{}", paramStr);
            String result = restHttpUtils.postJson(url, paramStr);
            log.info("调用云之家获取审批单详情返回结果 :{}", result);
            YunzhijiaApplyEventDTO yunzhijiaApplyEventDTO = JsonUtils.toObj(result, new TypeReference<YunzhijiaApplyEventDTO>() {
            });
//        YunzhijiaApplyEventDTO yunzhijiaApplyEventDTO = JsonUtils.toObj(result, YunzhijiaApplyEventDTO.class);
            log.info("调用云之家获取审批单详情返回转换结果 :{}", JsonUtils.toJson(yunzhijiaApplyEventDTO));
            return yunzhijiaApplyEventDTO;
        }
        return null;
    }


    public YunzhijiaApplyRespDTO createYunzhijiaRemoteApply(YunzhijiaAccessTokenReqDTO yunzhijiaAccessTokenReqDTO, Map form, String formCodeId, String createor) {
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaRemoteAccessToken = yunzhijiaTokenService.getYunzhijiaRemoteAccessToken(yunzhijiaAccessTokenReqDTO);
        if (yunzhijiaRemoteAccessToken.getErrorCode() == RespCode.SUCCESS) {
            String accessToken = yunzhijiaRemoteAccessToken.getData().getAccessToken();
            String url = yunzhijiaHost + "/gateway/workflow/form/thirdpart/createInst?accessToken=" + accessToken;
            Map paramMap = Maps.newHashMap();
            paramMap.put("formCodeId", formCodeId);
            paramMap.put("creator", createor);
            paramMap.put("widgetValue", form);
            String paramStr = JsonUtils.toJsonSnake(paramMap);
            log.info("创建云之家审批单请求参数 :{}", paramStr);
            String result = restHttpUtils.postJson(url, paramStr);
            log.info("创建云之家返回结果 :{}", result);
            YunzhijiaApplyRespDTO yunzhijiaApplyRespDTO = JsonUtils.toObj(result, new TypeReference<YunzhijiaApplyRespDTO>() {
            });
//        YunzhijiaApplyEventDTO yunzhijiaApplyEventDTO = JsonUtils.toObj(result, YunzhijiaApplyEventDTO.class);
            log.info("调用云之家获取审批单详情返回转换结果 :{}", JsonUtils.toJson(yunzhijiaApplyRespDTO));
            return yunzhijiaApplyRespDTO;
        }
        return null;
    }
}
