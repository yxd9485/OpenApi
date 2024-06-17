package com.fenbeitong.openapi.plugin.func.bank.service;

import com.fenbeitong.bank.api.model.PaymentDetailDTO;
import com.fenbeitong.openapi.plugin.func.bank.dto.IBankPaymentDetailVO;

import java.util.List;

/**
 * 对公付款
 *
 * @author ctl
 * @date 2021/11/10
 */
public interface OpenBankPaymentService {

    /**
     * 根据付款单id查询详情
     *
     * @param companyId
     * @param paymentId
     * @return
     */
    IBankPaymentDetailVO bankPaymentDetailById(String companyId, String paymentId);

    /**
     * 根据付款单id集合查询详情集合
     * @param companyId
     * @param paymentIds
     * @return
     */
    List<IBankPaymentDetailVO> bankPaymentDetailListByIds(String companyId, List<String> paymentIds);
}
