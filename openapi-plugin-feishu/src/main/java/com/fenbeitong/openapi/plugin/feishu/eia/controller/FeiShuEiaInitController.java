package com.fenbeitong.openapi.plugin.feishu.eia.controller;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResultEntity;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuRespDTO;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaApprovalService;
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
@RequestMapping("/feishu/eia/init")
public class FeiShuEiaInitController {

    @Autowired
    private FeiShuEiaApprovalService feiShuEiaApprovalService;

    @RequestMapping("/subscribeApproval")
    @ResponseBody
    public Object subscribeApproval(@RequestParam(required = true) String corpId, @RequestParam(required = true) String approvalCode) throws Exception {
        FeiShuRespDTO feiShuRespDTO = feiShuEiaApprovalService.subscribeApproval(approvalCode, corpId);
        FeiShuResultEntity feiShuResultEntity = new FeiShuResultEntity();
        feiShuResultEntity.setCode(feiShuRespDTO.getCode());
        feiShuResultEntity.setMsg(feiShuRespDTO.getMsg());
        return feiShuResultEntity;
    }

}
