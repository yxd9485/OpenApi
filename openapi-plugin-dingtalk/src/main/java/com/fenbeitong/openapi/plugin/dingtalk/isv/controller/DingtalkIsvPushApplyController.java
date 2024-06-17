package com.fenbeitong.openapi.plugin.dingtalk.isv.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.reverse.DingtalkIntranetPushApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.reverse.DingtalkPushApplyService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * @Description 反向审批
 * @Author duhui
 * @Date 2021-04-06
 **/
@Controller
@RequestMapping("/dingtalk/isv/pushData/")
public class DingtalkIsvPushApplyController {
    @Autowired
    DingtalkIntranetPushApplyService dingtalkIntranetPushApplyService;

    @Autowired
    private DingtalkPushApplyService dingtalkPushApplyService;

    @RequestMapping("/orderApply")
    @ResponseBody
    public Object pushOrderApply(@RequestBody String request) throws ParseException {
        boolean success = dingtalkIntranetPushApplyService.pushApply(request, OpenType.DINGTALK_ISV.getType());
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉订单审批失败");
        }
    }

    @RequestMapping("/tripApply")
    @ResponseBody
    public Object pushTripApply(HttpServletRequest request) throws ParseException {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = dingtalkIntranetPushApplyService.pushTripApply(requestBody, OpenType.DINGTALK_ISV.getType());
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉差旅审批失败");
        }
    }

    @RequestMapping("/taxiApply")
    @ResponseBody
    public Object pushTaxiApply(@RequestBody IntranetApplyCarDTO intranetApplyCarDTO) {
        boolean b = dingtalkIntranetPushApplyService.pushApply(intranetApplyCarDTO, OpenType.DINGTALK_ISV.getType(),ProcessTypeConstant.CAR_REVERSE);
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉用车审批失败");
        }
    }

    @RequestMapping("/dinnerApply")
    @ResponseBody
    public Object pushDinnerApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = dingtalkIntranetPushApplyService.pushDinnerApply(requestBody, OpenType.DINGTALK_ISV.getType());
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉用餐审批失败");
        }
    }

    @RequestMapping("/mallApply")
    @ResponseBody
    public Object pushMallApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = dingtalkIntranetPushApplyService.pushMallApply(requestBody, OpenType.DINGTALK_ISV.getType());
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建钉钉采购审批失败");
        }
    }

    @RequestMapping("/orderApplyRefund")
    @ResponseBody
    public Object pushOrderApplyRefund(@RequestBody String request) {
        boolean success = dingtalkIntranetPushApplyService.pushApplyRefund(request, OpenType.DINGTALK_ISV.getType());
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉订单退订审批失败");
        }
    }

    @RequestMapping("/orderApplyChange")
    @ResponseBody
    public Object orderApplyChange(@RequestBody String request) throws ParseException {
        boolean success = dingtalkIntranetPushApplyService.pushApplyChange(request, OpenType.DINGTALK_ISV.getType());
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉订单改签审批失败");
        }
    }

    @RequestMapping("/multiTripApply/{companyId}")
    @ResponseBody
    public Object multiTripApply(@RequestBody IntranetApplyMultiTripDetailDTO multiTripDetailDTO, @PathVariable("companyId") String companyId) {
        multiTripDetailDTO.setCompanyId(companyId);
        boolean success = dingtalkIntranetPushApplyService.pushApply(multiTripDetailDTO, OpenType.DINGTALK_ISV.getType(),ProcessTypeConstant.MULTI_TRIP_REVERSE);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉审批失败非行程审批失败");
        }
    }

    /**
     * 外卖
     * @param applyTakeAwayNoticeDTO
     * @return
     */
    @RequestMapping("/takeawayApply/{companyId}")
    @ResponseBody
    public Object takeawayApply(@RequestBody ApplyTakeAwayNoticeDTO applyTakeAwayNoticeDTO , @PathVariable("companyId") String companyId) {
        boolean success = dingtalkPushApplyService.pushTakeawayApply( applyTakeAwayNoticeDTO , companyId , OpenType.DINGTALK_ISV.getType());
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建钉钉审批失败外卖审批失败");
        }
    }

    /**
     * 采购
     * @param apply
     * @return
     */
    @RequestMapping("/purchaseApply/{companyId}")
    @ResponseBody
    public Object purchaseApply(@RequestBody MallApplyDTO apply, @PathVariable("companyId") String companyId) {
        boolean success = dingtalkPushApplyService.pushMallApply(apply, companyId ,OpenType.DINGTALK_ISV.getType());
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建钉钉审批失败外卖审批失败");
        }
    }

    /**
     * 分贝券
     * @param fbCouponApply
     * @return
     */
    @RequestMapping("/fbCouponApply/{companyId}")
    @ResponseBody
    public Object fbCouponApply(@RequestBody FBCouponApplyDetailDTO fbCouponApply , @PathVariable("companyId") String companyId) {
        boolean success = dingtalkPushApplyService.pushFbCouponApply(fbCouponApply, companyId , OpenType.DINGTALK_ISV.getType());
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "钉钉创建分贝券审批单失败");
        }
    }

    /**
     * 里程补贴
     * @param mileageSubsidyNotice
     * @return
     */
    @RequestMapping("/mileageApply/{companyId}")
    @ResponseBody
    public Object mileageApply(@RequestBody MileageSubsidyNoticeDTO mileageSubsidyNotice , @PathVariable("companyId") String companyId) {
        boolean success = dingtalkPushApplyService.pushMileageApply(mileageSubsidyNotice, companyId ,OpenType.DINGTALK_ISV.getType());
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "钉钉创建里程补贴审批单失败");
        }
    }

    /**
     * 虚拟卡额度
     * @param virtualCardAmountDetailDTO
     * @return
     */
    @RequestMapping("/virtualCardAmountApply/{companyId}")
    @ResponseBody
    public Object virtualCardAmountApply(@RequestBody VirtualCardAmountDetailDTO virtualCardAmountDetailDTO , @PathVariable("companyId") String companyId) {
        boolean success = dingtalkPushApplyService.pushVirtualCardAmountApply(virtualCardAmountDetailDTO , companyId , OpenType.DINGTALK_ISV.getType() , ProcessTypeConstant.VIRTUAL_CARD_REVERSE);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "钉钉创建虚拟卡额度审批单失败");
        }
    }

    /**
     * 备用金
     * @param virtualCardAmountDetailDTO
     * @return
     */
    @RequestMapping("/virtualCardPrettyApply/{companyId}")
    @ResponseBody
    public Object virtualCardPrettyApply(@RequestBody VirtualCardAmountDetailDTO virtualCardAmountDetailDTO , @PathVariable("companyId") String companyId) {
        boolean success = dingtalkPushApplyService.pushVirtualCardAmountApply(virtualCardAmountDetailDTO , companyId , OpenType.DINGTALK_ISV.getType() , ProcessTypeConstant.VIRTUAL_CARD_PRETTY);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "钉钉创建虚拟卡额度审批单失败");
        }
    }

    /**
     * 对公付款
     * @param paymentApplyDetailDTO
     * @return
     */
    @RequestMapping("/paymentApply/{companyId}")
    @ResponseBody
    public Object paymentApply(@RequestBody PaymentApplyDetailDTO paymentApplyDetailDTO , @PathVariable("companyId") String companyId) {
        boolean success = dingtalkPushApplyService.pushPaymentApply(paymentApplyDetailDTO , companyId , OpenType.DINGTALK_ISV.getType() , ProcessTypeConstant.PAYMENT_APPLY);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "钉钉创建虚拟卡额度审批单失败");
        }
    }
}
