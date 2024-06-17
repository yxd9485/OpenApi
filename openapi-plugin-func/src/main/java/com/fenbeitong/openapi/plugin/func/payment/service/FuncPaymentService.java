package com.fenbeitong.openapi.plugin.func.payment.service;

import com.fenbeitong.bank.api.model.PaymentDetailDTO;
import com.fenbeitong.bank.api.model.PaymentOrderDetailDTO;
import com.fenbeitong.openapi.plugin.func.payment.dto.FuncSupplierListPageDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.PaymentCreateResDataDTO;

import java.util.List;

/**
 * 对公付款业务
 *
 * @author ctl
 * @date 2022/3/8
 */
public interface FuncPaymentService {

    /**
     * 创建对公付款申请单
     *
     * @param companyId
     * @param data
     * @return
     */
    PaymentCreateResDataDTO createPaymentApply(String companyId, String data);

    /**
     * 根据付款单id+公司id查询付款结果
     *
     * @param paymentId
     * @param companyId
     * @return
     */
    PaymentDetailDTO getPaymentResultByPaymentId(String paymentId, String companyId);

    /**
     * 根据申请单id查询付款结果
     *
     * @param applyId
     * @return
     */
    PaymentDetailDTO getPaymentResultByApplyId(String applyId);

    /**
     * 根据申请单id查询电子回单列表
     *
     * @param paymentId
     * @return
     */
    List<PaymentOrderDetailDTO> getElectronicListByPaymentId(String paymentId);

    /**
     * 查询供应商列表 分页
     *
     * @param companyId
     * @param data
     * @return
     */
    FuncSupplierListPageDTO listSuppliersByPage(String companyId, String data);
}
