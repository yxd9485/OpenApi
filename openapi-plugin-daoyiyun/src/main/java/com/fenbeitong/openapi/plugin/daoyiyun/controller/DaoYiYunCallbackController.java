package com.fenbeitong.openapi.plugin.daoyiyun.controller;

import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunCallbackReqDTO;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunReqDTO;
import com.fenbeitong.openapi.plugin.daoyiyun.service.DaoYiYunCallbackService;
import com.fenbeitong.openapi.plugin.daoyiyun.util.DaoYiYunResponseUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回调接收
 *
 * @author lizhen
 */
@RestController
@Slf4j
@RequestMapping("/daoyiyun/callback")
public class DaoYiYunCallbackController {

    @Autowired
    private DaoYiYunCallbackService daoYiYunCallbackService;

    @RequestMapping("/recive")
    public Object checkFailedTask(@RequestBody DaoYiYunCallbackReqDTO daoYiYunCallbackReqDTO, DaoYiYunReqDTO daoYiYunReqDTO) {
        daoYiYunCallbackReqDTO.setApplicationId(daoYiYunReqDTO.getApplicationId());
        String revice = daoYiYunCallbackService.revice(daoYiYunCallbackReqDTO);
        return DaoYiYunResponseUtils.success(revice);
    }


}
