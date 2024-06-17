package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.fenbeipay.api.model.dto.vouchers.resp.VouchersOperationFlowRespRPCDTO;

import java.util.Map;

/**
 * <p>Title: IVoucherBillExtListener</p>
 * <p>Description: 分贝券流水账单扩展监听</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/11/29 2:29 PM
 */
public interface IVoucherBillExtListener {

    /**
     * 设置分贝券流水三方信息
     *
     * @param companyId
     * @param flowRespDto
     * @param thirdInfo
     */
    void setBillExt(String companyId, VouchersOperationFlowRespRPCDTO flowRespDto, Map<String, Object> thirdInfo);
}
