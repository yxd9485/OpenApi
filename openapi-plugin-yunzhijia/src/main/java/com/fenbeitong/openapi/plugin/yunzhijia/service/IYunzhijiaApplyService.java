package com.fenbeitong.openapi.plugin.yunzhijia.service;

import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyEventDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaApply;

public interface IYunzhijiaApplyService {

    YunzhijiaApply getYunzhijiaApplyByCorpId(String corpId);
    YunzhijiaApplyEventDTO getYunzhijiaApplyDetail(YunzhijiaAccessTokenReqDTO yunzhijiaAccessTokenReqDTO, String formCodeId, String formInstId);
}
