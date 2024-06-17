package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.payment.dto.BankPaymentEvent;
import com.fenbeitong.openapi.plugin.event.payment.dto.BankPaymentEventV2;
import com.fenbeitong.openapi.plugin.event.payment.dto.BankPaymentEventV2;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.callback.dto.PaymentRecordDTO;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.common.service.OpenIdTranService;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 对公付款结果处理
 *
 * @author ctl
 * @date 2022/8/18
 */
@Component
@Slf4j
public class BankPaymentEventV2Handler extends EventHandler<BankPaymentEventV2> {

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Autowired
    private UcCompanyServiceImpl ucCompanyService;

    @Autowired
    private OpenIdTranService openIdTranService;

    @FuncOrderCallBack(companyId = "companyId", type = 128, version = "2.0", orderId = "paymentId", status = "status", statusList = {80, 21, 84}, logicKeys = {"paymentId", "status"}, callbackType = 503)
    @Override
    public boolean process(BankPaymentEventV2 event, Object... args) {
        log.info("对公付款,event:{}",event);
        String applyId = event.getApplyId();
        String companyId = event.getCompanyId();
        String thirdPaymentId = event.getThirdPaymentId();

        try {
            PaymentRecordDTO paymentRecordDTO = new PaymentRecordDTO();
            paymentRecordDTO.setPaymentId(event.getPaymentId());
            paymentRecordDTO.setThirdPaymentId(thirdPaymentId);
            paymentRecordDTO.setPaymentState(event.getStatus());
            paymentRecordDTO.setReturnRemittance(event.getReexchangeFlag());
            paymentRecordDTO.setFailReason(event.getFailReason());
            paymentRecordDTO.setApplyId(event.getApplyId());
            paymentRecordDTO.setThirdApplyId(event.getApplyThirdId());
            paymentRecordDTO.setPayerId(event.getPayerId());
            paymentRecordDTO.setThirdPayerId(
                openIdTranService.fbIdToThirdId(companyId, event.getPayerId(), IdBusinessTypeEnums.EMPLOYEE.getKey())
            );
            paymentRecordDTO.setAmount(event.getTotalPrice());
            paymentRecordDTO.setPaymentTime(event.getCompleteTime());
            paymentRecordDTO.setCompanyId(companyId);

            log.info("付款单结果:{}", JsonUtils.toJson(paymentRecordDTO));
            // 入库
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(128);
            record.setTypeName("对公付款结果");
            // 用付款单id存到order_id的字段中
            record.setOrderId(event.getPaymentId());
            record.setOrderStatus(event.getStatus());
            record.setCompanyId(companyId);
            record.setCompanyName(ucCompanyService.getCompanyName(companyId));
            record.setApplyId(applyId);
            record.setCallbackType(CallbackType.PAYMENT_RESULT_NOTIFY.getType());
            record.setCallbackData(JsonUtils.toJson(paymentRecordDTO));
            callbackRecordDao.saveSelective(record);
            businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
        } catch (Exception e) {
            log.warn("对公付款结果通知处理失败", e);
        }
        return true;
    }
}
