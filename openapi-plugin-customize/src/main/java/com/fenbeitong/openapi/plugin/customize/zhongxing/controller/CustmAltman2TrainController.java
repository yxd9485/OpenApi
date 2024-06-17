package com.fenbeitong.openapi.plugin.customize.zhongxing.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.zhongxing.service.IZhongXingAltmanConvertService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * @ClassName CustmAltman2TrainController
 * @Description 众行传媒万能订单转火车正向单处理（火车票代打印费）
 * @Author helu
 * @Date 2022/3/31 下午2:44
 * @Company www.fenbeitong.com
 **/
@RestController
@RequestMapping("/customize/zhongxing/order")
public class CustmAltman2TrainController {

    @Autowired
    private IZhongXingAltmanConvertService zhongXingAltmanConvertService;

    @RequestMapping("/dataConvert")
    public Object push(@RequestBody String data) throws Exception {
        zhongXingAltmanConvertService.zxCallbackDataConvert(data);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
