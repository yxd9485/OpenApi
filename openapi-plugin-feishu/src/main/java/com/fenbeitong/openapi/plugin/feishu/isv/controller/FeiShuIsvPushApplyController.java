package com.fenbeitong.openapi.plugin.feishu.isv.controller;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuPushMessageRespDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.service.*;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.util.StringUtil;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * 飞书三方应用订单审批推送
 * @author lizhen
 * @date 2020/12/2
 */
@Controller
@RequestMapping("/feishu/isv/pushData")
public class FeiShuIsvPushApplyController {
    @Autowired
    private FeiShuIsvPushApplyService feiShuIsvPushApplyService;

    @Autowired
    private FeiShuIsvPictureService feiShuIsvPictureService;


    @Autowired
    private FeiShuIsvMessageService feiShuIsvMessageService;

    @Autowired
    private FeiShuIsvPushApplyMsgService pushApplyMsgService;


    @RequestMapping("/orderApply")
    @ResponseBody
    public Object pushOrderApply(@RequestBody String request) throws ParseException {
        boolean success = feiShuIsvPushApplyService.pushApply(request);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建飞书三方应用订单审批失败");
        }
    }


    /**
     * 上传图片
     * @param request
     * @return
     * @throws ParseException
     */
    @RequestMapping("/uploadPic")
    @ResponseBody
    public String uploadPic(@RequestBody String request) {
        return feiShuIsvPictureService.uploadPic(request);
    }

    /**
     * 给指定用户发送消息
     * @param request
     * @return
     * @throws ParseException
     */
    @RequestMapping("/sendMessageForAppointUser")
    @ResponseBody
    @Async
    public Object sendMessageForAppointUser(@RequestBody String request) {
        if(StringUtil.isNotEmpty(request)) {
            FeiShuPushMessageRespDTO feiShuPushMessageDataRespDTO = JSONObject.parseObject(request, FeiShuPushMessageRespDTO.class);
            String result = feiShuIsvMessageService.sendMessageForAppointUser(feiShuPushMessageDataRespDTO,new ArrayList<>());
            if ("success".equals(result)) {
                return OpenapiResponseUtils.success(Maps.newHashMap());
            }else if("repeat".equals(result)){
                return OpenapiResponseUtils.error(-1, "给指定用户发送消息重复点击");
            }
        }
        return OpenapiResponseUtils.error(-1, "给指定用户发送消息失败");
    }

    /**
     * 定时任务给指定用户发送消息
     * @return
     * @throws ParseException
     */
    @RequestMapping("task/sendMessageForAppointUser")
    @ResponseBody
    @Async
    public void sendTaskMessageForAppointUser() {
        feiShuIsvMessageService.sendTaskMessageForAppointUser();
    }

    /**
     * 给推送失败的用户重新推送
     * @return
     * @throws ParseException
     */
    @RequestMapping("/sendForFailUser")
    @ResponseBody
    @Async
    public Object sendForFailUser(@RequestBody String request) {
        String result =feiShuIsvMessageService.sendForFailUser(request);
        if ("success".equals(result)) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else if("repeat".equals(result)){
            return OpenapiResponseUtils.error(-1, "给推送失败的用户重新推送重复点击");
        } else {
            return OpenapiResponseUtils.error(-1, "给推送失败的用户重新推送失败");
        }
    }

    /**
     * 行程审批单同步
     * @param apply
     * @return
     * */
    @RequestMapping("/tripApply/{companyId}")
    @ResponseBody
    public Object pushMultiTripApply(@RequestBody ApplyTripDTO apply  , @PathVariable("companyId") String companyId)  {
        boolean success = pushApplyMsgService.pushTripApply(apply , companyId);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建飞书三方应用行程审批失败");
        }
    }

    /**
     * 非行程审批单同步
     * @param apply
     * @return
     * */
    @RequestMapping("/multiTripApply/{companyId}")
    @ResponseBody
    public Object pushMultiTripApply(@RequestBody IntranetApplyMultiTripDetailDTO apply , @PathVariable("companyId") String companyId)  {
        boolean success = pushApplyMsgService.pushMultiTripApply(apply , companyId);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建飞书三方应用订单审批失败");
        }
    }

    /**
     * 用餐审批单同步
     *
     * @param applyDetail
     * @return
     */
    @RequestMapping("/dinnerApply/{companyId}")
    @ResponseBody
    public Object pushDinnerApply(@RequestBody DinnerApplyDetailDTO applyDetail, @PathVariable("companyId") String companyId) {
        boolean success = pushApplyMsgService.pushDinnerApply(applyDetail, companyId);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建飞书三方应用订单审批失败");
        }
    }

    /**
     * 采购审批单同步
     * @param apply
     * @return
     * */
    @RequestMapping("/purchaseApply/{companyId}")
    @ResponseBody
    public Object pushPurchaseApply(@RequestBody MallApplyDTO apply, @PathVariable("companyId") String companyId)  {
        boolean success = pushApplyMsgService.pushPurchaseApply(apply , companyId);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建飞书采购审批单失败");
        }
    }

    /**
     * 外卖审批单同步
     * @param applyTakeAwayNoticeDTO
     * @return
     * */
    @RequestMapping("/takeawayApply/{companyId}")
    @ResponseBody
    public Object pushTakeawayApply(@RequestBody ApplyTakeAwayNoticeDTO applyTakeAwayNoticeDTO , @PathVariable("companyId") String companyId)  {
        boolean success = pushApplyMsgService.pushTakeawayApply(applyTakeAwayNoticeDTO , companyId);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建飞书外卖审批单失败");
        }
    }

    /**
     * 分贝券审批单同步
     * @param fbCouponApplyDetail
     * @return
     * */
    @RequestMapping("/fbCounponApply/{companyId}")
    @ResponseBody
    public Object pushFbCounponApply(@RequestBody FBCouponApplyDetailDTO fbCouponApplyDetail , @PathVariable("companyId") String companyId )  {
        boolean success = pushApplyMsgService.pushFbCounponApply( fbCouponApplyDetail , companyId );
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建分贝券审批单失败");
        }
    }

    /**
     * 里程补贴审批单同步
     * @param mileageSubsidyNotice
     * @return
     * */
    @RequestMapping("/mileageApply/{companyId}")
    @ResponseBody
    public Object pushMileageApply(@RequestBody MileageSubsidyNoticeDTO mileageSubsidyNotice , @PathVariable("companyId") String companyId )  {
        boolean success = pushApplyMsgService.pushMileageApply( mileageSubsidyNotice , companyId );
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建里程补贴审批单失败");
        }
    }

    /**
     * 虚拟卡额度审批单同步
     * @param virtualCardAmountDetailDTO
     * @return
     * */
    @RequestMapping("/virtualCardAmountApply/{companyId}")
    @ResponseBody
    public Object pushVirtualCardAmountApply(@RequestBody VirtualCardAmountDetailDTO virtualCardAmountDetailDTO , @PathVariable("companyId") String companyId )  {
        boolean success = pushApplyMsgService.pushVirtualCardApply( virtualCardAmountDetailDTO , companyId , ProcessTypeConstant.VIRTUAL_CARD_REVERSE);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建虚拟卡额度审批单失败");
        }
    }

    /**
     * 备用金审批单同步
     * @param virtualCardAmountDetailDTO
     * @return
     * */
    @RequestMapping("/virtualCardPretty/{companyId}")
    @ResponseBody
    public Object pushVirtualCardPrettyApply(@RequestBody VirtualCardAmountDetailDTO virtualCardAmountDetailDTO , @PathVariable("companyId") String companyId )  {
        boolean success = pushApplyMsgService.pushVirtualCardApply( virtualCardAmountDetailDTO , companyId , ProcessTypeConstant.VIRTUAL_CARD_PRETTY);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建虚拟卡额度审批单失败");
        }
    }

    /**
     * 对公付款审批单同步
     * @param paymentApplyDetailDTO
     * @return
     * */
    @RequestMapping("/paymentApply/{companyId}")
    @ResponseBody
    public Object pushPaymentApply(@RequestBody PaymentApplyDetailDTO paymentApplyDetailDTO , @PathVariable("companyId") String companyId )  {
        boolean success = pushApplyMsgService.pushPaymentApply( paymentApplyDetailDTO , companyId );
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建对公付款审批单失败");
        }
    }

    /**
     * 用车审批反向审批
     * @param carApplyDetailDTO 用车信息
     * @return
     * */
    @RequestMapping("/taxiApply/{companyId}")
    @ResponseBody
    public Object pushTaxiApply(@RequestBody IntranetApplyCarDTO carApplyDetailDTO , @PathVariable("companyId") String companyId )  {
        boolean success = pushApplyMsgService.pushCarApply( carApplyDetailDTO , companyId );
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建飞书用车审批单失败");
        }
    }

}
