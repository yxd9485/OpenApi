package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbei.billpay.api.payment.ThirdCustomPublicPaymentFacade;
import com.fenbei.billpay.api.payment.dto.custom.CustomPublicPaymentDetailResDTO;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.payment.dto.BankPaymentEventV3;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 付款单置为无效通知
 *
 * @author machao
 * @date 2022/9/26
 */
@Component
@Slf4j
public class BankPaymentEventV3Handler extends EventHandler<BankPaymentEventV3> {

    @DubboReference(check = false)
    private ThirdCustomPublicPaymentFacade thirdCustomPublicPaymentFacade;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private UcCompanyServiceImpl ucCompanyService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Override
    public boolean process(BankPaymentEventV3 event, Object... args) {
        String companyId = event.getCompanyId();
        String paymentId = event.getPaymentId();
        String thirdPaymentId = event.getThirdPaymentId();
        if (StringUtils.isNotBlank(paymentId) || StringUtils.isNotBlank(thirdPaymentId)) {
            try {
                // 查付款单详情
                CustomPublicPaymentDetailResDTO paymentDetailDTO = thirdCustomPublicPaymentFacade.queryPaymentByPaymentId(companyId, getToken(companyId), paymentId);
                log.info("付款单详情:{}", JsonUtils.toJson(paymentDetailDTO));
                if (paymentDetailDTO != null) {
                    // 入库
                    ThirdCallbackRecord record = new ThirdCallbackRecord();
                    record.setType(128);
                    record.setTypeName("付款单置为无效通知");
                    // 用付款单id存到order_id的字段中
                    record.setOrderId(event.getPaymentId());
                    record.setCompanyId(companyId);
                    record.setOrderStatus(paymentDetailDTO.getPayment().getStatus());
                    record.setCompanyName(ucCompanyService.getCompanyName(companyId));
                    record.setCallbackType(CallbackType.PAYMENT_STATE_CHANGE_NOTIFY.getType());
                    record.setCallbackData(JsonUtils.toJson(paymentDetailDTO));
                    callbackRecordDao.saveSelective(record);
                    businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
                }
            } catch (Exception e) {
                log.warn("对公付款结果通知处理失败", e);
            }
        } else {
            log.info("当前接收到的付款单置为无效通知不属于三方对接,无需处理,event:{}", JsonUtils.toJson(event));
        }
        return true;
    }

    private String getToken(String companyId) {
        return userCenterService.getUcSuperAdminToken(companyId);
    }
}
