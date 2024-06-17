package com.fenbeitong.openapi.plugin.yunzhijia.controller;

import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenRespDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token.YunzhijiaTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/yunzhijia/token")
public class YunzhijiaAccessTokenController {

    @Autowired
    YunzhijiaTokenService yunzhijiaTokenService;

    /**
     * 获取云之家access_token
     *
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    public Object getAccessToken(@RequestBody YunzhijiaAccessTokenReqDTO yunzhijiaAccessTokenReqDTO) {
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessToken = yunzhijiaTokenService.getYunzhijiaRemoteAccessToken(yunzhijiaAccessTokenReqDTO);
        if (yunzhijiaAccessToken.getErrorCode() == RespCode.SUCCESS) {
            return YunzhijiaResponseUtils.success(yunzhijiaAccessToken.getData());
        }
        return YunzhijiaResponseUtils.error(Integer.valueOf(yunzhijiaAccessToken.getErrorCode()),yunzhijiaAccessToken.getError());
    }


    /**
     * 刷新access_token
     *
     * @param yunzhijiaAccessTokenReqDTO
     * @return
     */
    @RequestMapping("/refresh")
    @ResponseBody
    public Object refreshAccessToken(@RequestBody YunzhijiaAccessTokenReqDTO yunzhijiaAccessTokenReqDTO) {
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessToken = yunzhijiaTokenService.refreshYunzhijiaRemoteAccessToken(yunzhijiaAccessTokenReqDTO);
        if(yunzhijiaAccessToken.getErrorCode() == RespCode.SUCCESS){
            return  YunzhijiaResponseUtils.success(yunzhijiaAccessToken.getData());
        }
        return YunzhijiaResponseUtils.error(yunzhijiaAccessToken.getErrorCode(),yunzhijiaAccessToken.getError());
    }
}
