package com.fenbeitong.openapi.plugin.voucher.service;

import com.fenbeitong.openapi.plugin.voucher.dto.CustomizeVoucherDTO;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceCustomVoucherCreateReqDto;

/**
 * <p>Title: IFinanceCustomVoucherService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/24 10:29 AM
 */
public interface IFinanceCustomVoucherService {


    void createVoucherByPublicBill(String voucherId, FinanceCustomVoucherCreateReqDto reqDto);

    CustomizeVoucherDTO getVoucherDetail(String voucherId);
}
