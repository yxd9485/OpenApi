package com.fenbeitong.openapi.plugin.seeyon.controller;

import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResponseUtils;
import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResultEntity;
import com.fenbeitong.openapi.plugin.seeyon.dto.WorkRecordData;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonWorkRecordService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/seeyon/push")
public class SeeyonWorkrecordController {

    @Autowired
    SeeyonWorkRecordService seeyonWorkRecordService;

    /**
     * 处理待办消息
     * @param workRecordData
     * @return
     */
    @RequestMapping("/thirdpartyPending")
    @ResponseBody
    public SeeyonResultEntity getSeeyonAccessToken( @RequestBody WorkRecordData workRecordData) {
        seeyonWorkRecordService.syncWorkRecord( workRecordData );
        return SeeyonResponseUtils.success(Maps.newHashMap());
    }
}
