package com.fenbeitong.openapi.plugin.yiduijie.controller;

import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResponseUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResultEntity;
import com.fenbeitong.openapi.plugin.yiduijie.model.voucher.CreateVoucherByApplyReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.voucher.CreateVoucherReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.service.voucher.IVoucherService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: YiDuiJieVoucherController</p>
 * <p>Description: 易对接凭证</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/18 12:20 PM
 */
@RestController
@RequestMapping("/yiduijie/voucher")
@Api(tags = "易对接凭证", description = "易对接凭证")
public class YiDuiJieVoucherController {

    @Autowired
    @Qualifier("springVoucherService")
    private IVoucherService voucherService;

    /**
     * 生成凭证
     *
     * @return 科目列表
     */
    @RequestMapping("/createVoucherByApply")
    @ApiOperation(value = "1、核销申请单生成凭证", notes = "生成凭证", httpMethod = "POST", position = 1, response = YiDuiJieResultEntity.class)
    public Object createVoucherByApply(@RequestBody CreateVoucherByApplyReqDTO req) {
        voucherService.createVoucherByApply(req);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 生成凭证
     *
     * @return 科目列表
     */
    @RequestMapping("/previewCreateVoucherByApply")
    @ApiOperation(value = "2、核销申请单凭证预览", notes = "凭证预览", httpMethod = "POST", position = 2, response = YiDuiJieResultEntity.class)
    public Object previewCreateVoucherByApply(@RequestBody CreateVoucherByApplyReqDTO req) {
        String result = voucherService.previewCreateVoucherByApply(req);
        return YiDuiJieResponseUtils.success(result);
    }

    /**
     * 生成凭证
     *
     * @return 科目列表
     */
    @RequestMapping("/createVoucher")
    public Object createVoucher(@RequestBody CreateVoucherReqDTO req) {
        voucherService.createVoucher(req);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }
}
