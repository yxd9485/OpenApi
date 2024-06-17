package com.fenbeitong.openapi.plugin.feishu.eia.controller;

import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeishuApplyReqDTO;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeishuEiaPushApplyService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.IntranetApplyMultiTripDetailDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 审批反向同步
 *
 * @author yan.pb
 * @date 2021/2/19
 */
@Controller
@RequestMapping("/feishu/pushData")
public class FeishuEiaPushApplyController {

    @Autowired
    FeishuEiaPushApplyService feishuEiaPushApplyService;

    @RequestMapping("/tripApply")
    @ResponseBody
    public Object pushTripApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        return feishuEiaPushApplyService.pushApply( requestBody , FeiShuServiceTypeConstant.TRIP  , ProcessTypeConstant.TRIP_REVERSE );
    }

    @RequestMapping("/taxiApply")
    @ResponseBody
    public Object pushTaxiApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        return feishuEiaPushApplyService.pushApply( requestBody , FeiShuServiceTypeConstant.TAXI ,ProcessTypeConstant.CAR_REVERSE);
    }

    @RequestMapping("/orderApply")
    @ResponseBody
    public Object pushOrderApply(@RequestBody String request) {
       return feishuEiaPushApplyService.pushApply(request , FeiShuServiceTypeConstant.ORDER , ProcessTypeConstant.ORDER_REVERSE);
    }

    @RequestMapping("/orderApplyChange")
    @ResponseBody
    public Object pushOrderChangeApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        return  feishuEiaPushApplyService.pushApply(requestBody , FeiShuServiceTypeConstant.ORDER_CHANGE ,ProcessTypeConstant.CHANGE_REVERSE);
    }

    @RequestMapping("/orderApplyRefund")
    @ResponseBody
    public Object pushOrderRefundApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        return feishuEiaPushApplyService.pushApply(requestBody , FeiShuServiceTypeConstant.ORDER_REFUND  , ProcessTypeConstant.REFUND_REVERSE);
    }

    @RequestMapping("/mallApply")
    @ResponseBody
    public Object pushMallReverseApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        return feishuEiaPushApplyService.pushApply(requestBody, FeiShuServiceTypeConstant.MALL , ProcessTypeConstant.MALL_REVERSE);
    }

    @RequestMapping("/dinnerApply")
    @ResponseBody
    public Object pushDinnerReverseApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        return feishuEiaPushApplyService.pushApply(requestBody, FeiShuServiceTypeConstant.DINNER , ProcessTypeConstant.DINNER_REVERSE);
    }

    /**
     * 非行程审批单同步
     * @param apply
     * @return
     * */
    @RequestMapping("/multiTripApply/{companyId}")
    @ResponseBody
    public Object pushMultiTripApply(@RequestBody IntranetApplyMultiTripDetailDTO apply , @PathVariable("companyId") String companyId)  {
        FeishuApplyReqDTO reqDTO = FeishuApplyReqDTO.builder().applyId(apply.getApplyId())
            .applyType(FeiShuServiceTypeConstant.MULTI_TRIP)
            .processType(ProcessTypeConstant.MULTI_TRIP_REVERSE )
            .thirdEmployeeId(apply.getThirdEmployeeId())
            .reqObj(JsonUtils.toJson(apply))
            .companyId(companyId).build();
        return feishuEiaPushApplyService.pushApply(reqDTO);

    }
}
