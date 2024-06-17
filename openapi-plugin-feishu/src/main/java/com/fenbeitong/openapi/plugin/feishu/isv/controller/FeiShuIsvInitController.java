package com.fenbeitong.openapi.plugin.feishu.isv.controller;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResultEntity;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuRespDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lizhen on 2020/12/30.
 */
@RestController
@Slf4j
@RequestMapping("/feishu/isv/init")
public class FeiShuIsvInitController {

    @Autowired
    private FeiShuIsvApprovalService feiShuIsvApprovalService;

    @RequestMapping("/subscribeApproval")
    @ResponseBody
    public Object subscribeApproval(@RequestParam(required = true) String corpId, @RequestParam(required = true) String approvalCode) throws Exception {
        FeiShuRespDTO feiShuRespDTO = feiShuIsvApprovalService.subscribeApproval(approvalCode, corpId);
        FeiShuResultEntity feiShuResultEntity = new FeiShuResultEntity();
        feiShuResultEntity.setCode(feiShuRespDTO.getCode());
        feiShuResultEntity.setMsg(feiShuRespDTO.getMsg());
        return feiShuResultEntity;
    }

}
