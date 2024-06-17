package com.fenbeitong.openapi.plugin.yiduijie.controller;

import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResponseUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResultEntity;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieNotifyMsgResultReq;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieInteractService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: YiDuiJieInteractController</p>
 * <p>Description: 易对接交互控制</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 4:07 PM
 */
@RestController
@RequestMapping("/yiduijie/interact")
@Api(tags = "易对接数据交互", description = "易对接数据交互")
public class YiDuiJieInteractController {

    @Autowired
    private IYiDuiJieInteractService interactService;

    /**
     * 查询业务数据
     *
     * @param batchId 批次号
     * @return 业务数据
     */
    @RequestMapping("/queryBusinessData/{batchId}")
    @ApiOperation(value = "1、查询业务数据", notes = "查询业务数据", httpMethod = "GET", position = 1, response = YiDuiJieResultEntity.class)
    public Object queryBusinessData(@PathVariable("batchId") String batchId) {
        Object result = interactService.queryBusinessData(batchId);
        return YiDuiJieResponseUtils.success(result);
    }

    /**
     * 通知消息处理结果
     *
     * @return 科目列表
     */
    @RequestMapping("/notifyMsgResult")
    @ApiOperation(value = "2、通知消息处理结果", notes = "通知消息处理结果", httpMethod = "POST", position = 2, response = YiDuiJieResultEntity.class)
    public Object notifyMsgResult(@RequestBody YiDuiJieNotifyMsgResultReq notifyMsgResultReq) {
        interactService.notifyMsgResult(notifyMsgResultReq);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }
}
