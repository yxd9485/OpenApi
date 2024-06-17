package com.fenbeitong.openapi.plugin.definition.controller;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.support.callback.dto.PushLogListResDto;
import com.fenbeitong.openapi.plugin.support.callback.service.CallbackThirdSupportService;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: CallbackThirdDefinitionController</p>
 * <p>Description: 推送数据到第三方</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/4 10:51 AM
 */
@RestController
@RequestMapping("/definitions/callback/third")
@Api(value = "推送数据到第三方", tags = "推送数据到第三方", description = "推送数据到第三方")
public class CallbackThirdDefinitionController {

    @Autowired
    private CallbackThirdSupportService callbackThirdSupportService;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @RequestMapping("/pushData")
    @ApiOperation(value = "推送数据到第三方", notes = "推送数据到第三方", httpMethod = "POST")
    public Object pushData() {
        callbackThirdSupportService.pushData(1);
        return DefinitionResultDTO.success(Maps.newHashMap());
    }

    @RequestMapping("/listPushLog/{mainId}")
    @ApiOperation(value = "查询推送日志", notes = "查询推送日志", httpMethod = "POST")
    public Object listPushLog(@PathVariable String mainId) {
        PushLogListResDto resDto = businessDataPushService.listPushLog(mainId);
        return DefinitionResultDTO.success(resDto);
    }
}
