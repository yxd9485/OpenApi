package com.fenbeitong.openapi.plugin.func.bank.service.impl;

import com.fenbeitong.bank.api.model.PaymentDetailDTO;
import com.fenbeitong.bank.api.service.IBankPaymentService;
import com.fenbeitong.openapi.plugin.func.bank.dto.IBankPaymentDetailVO;
import com.fenbeitong.openapi.plugin.func.bank.service.OpenBankPaymentService;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对公付款
 *
 * @author ctl
 * @date 2021/11/10
 */
@Service
@ServiceAspect
public class OpenBankPaymentServiceImpl implements OpenBankPaymentService {

    @DubboReference(version = "",check = false)
    private IBankPaymentService iBankPaymentService;

    @Override
    public IBankPaymentDetailVO bankPaymentDetailById(String companyId, String paymentId) {
        PaymentDetailDTO paymentDetailDTO = iBankPaymentService.webPaymentSearchDetail(paymentId, companyId);
        IBankPaymentDetailVO result = new IBankPaymentDetailVO();
        if (paymentDetailDTO != null) {
            BeanUtils.copyProperties(paymentDetailDTO, result);
        }
        filterApplyReason(result);
        return result;
    }

    @Override
    public List<IBankPaymentDetailVO> bankPaymentDetailListByIds(String companyId, List<String> paymentIds) {
        List<PaymentDetailDTO> paymentDetailListByIds = iBankPaymentService.getPaymentDetailListByIds(companyId, paymentIds);
        List<IBankPaymentDetailVO> result = new ArrayList<>();
        if (!ObjectUtils.isEmpty(paymentDetailListByIds)) {
            for (PaymentDetailDTO paymentDetailListById : paymentDetailListByIds) {
                IBankPaymentDetailVO iBankPaymentDetailVO = new IBankPaymentDetailVO();
                BeanUtils.copyProperties(paymentDetailListById, iBankPaymentDetailVO);
                filterApplyReason(iBankPaymentDetailVO);
                result.add(iBankPaymentDetailVO);
            }
        }
        return result;
    }

    /**
     * 支付把applyReason和applyReasonDesc用";"拼接到一个字段中了
     * 这里用再拆分成两个字段
     * 如果原始字段中就存在";"则不处理
     *
     * @param iBankPaymentDetailVO
     */
    private void filterApplyReason(IBankPaymentDetailVO iBankPaymentDetailVO) {
        if (iBankPaymentDetailVO != null) {
            String applyReason = iBankPaymentDetailVO.getApplyReason();
            if (!StringUtils.isBlank(applyReason)) {
                if (applyReason.contains(";")) {
                    String[] split = applyReason.split(";");
                    // 如果原始字段
                    if (!ObjectUtils.isEmpty(split) && split.length == 2) {
                        iBankPaymentDetailVO.setApplyReason((StringUtils.isBlank(split[0]) || "null".equals(split[0])) ? "" : split[0]);
                        iBankPaymentDetailVO.setApplyReasonDesc((StringUtils.isBlank(split[1]) || "null".equals(split[1])) ? "" : split[1]);
                    }
                }
            }
        }
    }

}
