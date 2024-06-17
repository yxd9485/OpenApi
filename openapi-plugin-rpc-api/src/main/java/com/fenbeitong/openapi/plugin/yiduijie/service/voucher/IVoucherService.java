package com.fenbeitong.openapi.plugin.yiduijie.service.voucher;

import com.fenbeitong.openapi.plugin.yiduijie.model.voucher.CreateVoucherByApplyReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.voucher.CreateVoucherByBillReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.voucher.CreateVoucherReqDTO;

/**
 * <p>Title: IVoucherService</p>
 * <p>Description: 凭证服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 2:57 PM
 */
public interface IVoucherService {

    /**
     * 生成凭证
     *
     * @param req
     */
    void createVoucher(CreateVoucherReqDTO req);

    /**
     * 根据审批单生成凭证
     *
     * @param req
     */
    void createVoucherByApply(CreateVoucherByApplyReqDTO req);

    /**
     * 预览审批单生成凭证
     *
     * @param req
     * @return 预览信息
     */
    String previewCreateVoucherByApply(CreateVoucherByApplyReqDTO req);


    /**
     * 根据账单生成凭证
     *
     * @param req
     */
    void createVoucherByBill(CreateVoucherByBillReqDTO req);

}
