package com.fenbeitong.openapi.plugin.landray.ekp.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.landray.ekp.service.impl.LandrayConfigInfoUtil;
import com.fenbeitong.openapi.plugin.landray.ekp.service.impl.LandrayFormService;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.revert.apply.constant.ServiceTypeConstant;
import com.fenbeitong.openapi.plugin.support.revert.apply.service.ICommonPushApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 蓝凌反向审批同步
 * @Auther zhang.peng
 * @Date 2021/8/4
 */
@RestController
@RequestMapping("/landary/ekp/pushData")
public class LandrayEkpPushApplyController {

    @Autowired
    private ICommonPushApplyService landaryEkpPushApplyService;

    @Autowired
    private LandrayConfigInfoUtil landrayConfigInfoUtil;

    @Autowired
    private LandrayFormService landrayFormService;

    @RequestMapping("/orderApply")
    @ResponseBody
    public Object orderApply( HttpServletRequest request ) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        FenbeitongApproveDto fenbeitongApproveDto = landaryEkpPushApplyService.getApproveDto(requestBody);
        String url = landrayConfigInfoUtil.getUrl(fenbeitongApproveDto);
        boolean b = landaryEkpPushApplyService.pushCommonApply(requestBody, ServiceTypeConstant.ORDER, url, landrayFormService );
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建蓝凌超规订单审批失败");
        }
    }

    @RequestMapping("/mallApply")
    @ResponseBody
    public Object mallApply( HttpServletRequest request ) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        FenbeitongApproveDto fenbeitongApproveDto = landaryEkpPushApplyService.getApproveDto(requestBody);
        String url = landrayConfigInfoUtil.getUrl(fenbeitongApproveDto);
        boolean b = landaryEkpPushApplyService.pushCommonApply(requestBody, ServiceTypeConstant.MALL, url, landrayFormService);
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建蓝凌采购审批失败");
        }
    }

    @RequestMapping("/carApply")
    @ResponseBody
    public Object carApply( HttpServletRequest request ) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        FenbeitongApproveDto fenbeitongApproveDto = landaryEkpPushApplyService.getApproveDto(requestBody);
        String url = landrayConfigInfoUtil.getUrl(fenbeitongApproveDto);
        boolean b = landaryEkpPushApplyService.pushCommonApply(requestBody, ServiceTypeConstant.CAR, url, landrayFormService);
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建蓝凌用车审批失败");
        }
    }

    @RequestMapping("/dinnerApply")
    @ResponseBody
    public Object dinnerApply( HttpServletRequest request ) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        FenbeitongApproveDto fenbeitongApproveDto = landaryEkpPushApplyService.getApproveDto(requestBody);
        String url = landrayConfigInfoUtil.getUrl(fenbeitongApproveDto);
        boolean b = landaryEkpPushApplyService.pushCommonApply(requestBody, ServiceTypeConstant.DINNER, url, landrayFormService);
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建蓝凌用餐审批失败");
        }
    }

}
