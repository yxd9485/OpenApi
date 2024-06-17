package com.fenbeitong.openapi.plugin.yunzhijia.controller;

import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaResourceLevelConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyEventDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaApply;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.apply.YunzhijiaApplyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/yunzhijia/apply")
public class YunzhijiaApplyController {

    @Autowired
    YunzhijiaApplyServiceImpl yunzhijiaApplyService;

    @RequestMapping("/detail")
    @ResponseBody
    public Object getYunzhijiaApplyDetail(String corpId,String formCodeId,String formInsId) {
        YunzhijiaApply yunzhijiaApplyByCorpId = yunzhijiaApplyService.getYunzhijiaApplyByCorpId(corpId);
        String agentId = yunzhijiaApplyByCorpId.getAgentId();
        String agentSecret = yunzhijiaApplyByCorpId.getAgentSecret();
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
                .eid(corpId)
                .appId(agentId)
                .secret(agentSecret)
                .timestamp(System.currentTimeMillis())
                .scope(YunzhijiaResourceLevelConstant.TEAM)
                .build();
        YunzhijiaApplyEventDTO yunzhijiaApplyDetail = yunzhijiaApplyService.getYunzhijiaApplyDetail(build, formCodeId, formInsId);
        return YunzhijiaResponseUtils.success(yunzhijiaApplyDetail.getData());
    }
}
