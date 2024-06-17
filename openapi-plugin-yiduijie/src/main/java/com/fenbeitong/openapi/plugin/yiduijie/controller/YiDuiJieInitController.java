package com.fenbeitong.openapi.plugin.yiduijie.controller;

import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResponseUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResultEntity;
import com.fenbeitong.openapi.plugin.yiduijie.model.app.CreateAppReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.app.GetAppListRespDTO;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieInitService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>Title: YiDuiJieInitController</p>
 * <p>Description: 易对接初始化</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 3:43 PM
 */
@RestController
@RequestMapping("/yiduijie/init")
@Api(tags = "易对接初始化", description = "易对接初始化")
public class YiDuiJieInitController {

    @Autowired
    private IYiDuiJieInitService yiDuijieInitService;

    /**
     * @return 易对接财务应用列表
     */
    @RequestMapping("/listApp")
    @ApiOperation(value = "1、应用列表", notes = "应用列表", httpMethod = "GET", position = 1, response = List.class)
    public List<GetAppListRespDTO> listApp() {
        return yiDuijieInitService.listApp();
    }

    /**
     * @return 创建应用
     */
    @RequestMapping("/createApp")
    @ApiOperation(value = "2、创建应用", notes = "创建应用", httpMethod = "POST", position = 2, response = YiDuiJieResultEntity.class)
    public Object createApp(@RequestBody CreateAppReqDTO createAppReq) {
        yiDuijieInitService.createApp(createAppReq);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }
}
