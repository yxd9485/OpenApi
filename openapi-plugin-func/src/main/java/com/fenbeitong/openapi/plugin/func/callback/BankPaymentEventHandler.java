package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.bank.api.model.PaymentDetailDTO;
import com.fenbeitong.bank.api.service.IBankPaymentService;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.payment.dto.BankPaymentEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 对公付款结果处理
 *
 * @author ctl
 * @date 2022/2/18
 */
@Component
@Slf4j
public class BankPaymentEventHandler extends EventHandler<BankPaymentEvent> {

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @DubboReference(check = false)
    private IBankPaymentService iBankPaymentService;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Autowired
    private UcCompanyServiceImpl ucCompanyService;

    @FuncOrderCallBack(companyId = "companyId", type = 128, version = "2.0", orderId = "paymentId", status = "status", statusList = {80}, logicKeys = {"paymentId", "status"}, ignoreExpress = "T(com.fenbeitong.openapi.plugin.util.StringUtils).isBlank(#data[applyThirdId])", callbackType = 502)
    @Override
    public boolean process(BankPaymentEvent event, Object... args) {
        // 只接收成功的 失败的可以直接重新发起支付 无需处理 退汇的可能是已经成功了 又被银行退回来 暂时无法处理
        String applyId = event.getApplyId();
        String applyThirdId = event.getApplyThirdId();
        String companyId = event.getCompanyId();
        // 三方申请单id不为空 和 申请单id都不为空 证明是三方发起的申请单
        if (!StringUtils.isBlank(applyId) && !StringUtils.isBlank(applyThirdId)) {
            try {
                // 查付款单详情
                PaymentDetailDTO paymentDetailDTO = iBankPaymentService.paymentDetailByApplyId(applyId);
                log.info("付款单详情:{}", JsonUtils.toJson(paymentDetailDTO));
                if (paymentDetailDTO != null) {
                    // 入库
                    ThirdCallbackRecord record = new ThirdCallbackRecord();
                    record.setType(128);
                    record.setTypeName("对公付款结果");
                    // 用付款单id存到order_id的字段中
                    record.setOrderId(event.getPaymentId());
                    record.setOrderStatus(event.getStatus());
                    record.setCompanyId(companyId);
                    record.setCompanyName(ucCompanyService.getCompanyName(companyId));
                    record.setContactName(paymentDetailDTO.getDuringApplyUserName());
                    record.setUserName(paymentDetailDTO.getDuringApplyUserName());
                    record.setApplyId(applyId);
                    record.setCallbackType(CallbackType.PAYMENT_RESULT_NOTIFY_V1.getType());
                    record.setCallbackData(JsonUtils.toJson(paymentDetailDTO));
                    callbackRecordDao.saveSelective(record);
                    businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
                }
            } catch (Exception e) {
                log.warn("对公付款结果通知处理失败", e);
            }
        } else {
            log.info("当前接收到的付款结果信息不属于三方对接,无需处理,event:{}", JsonUtils.toJson(event));
        }
        return true;
    }
}
