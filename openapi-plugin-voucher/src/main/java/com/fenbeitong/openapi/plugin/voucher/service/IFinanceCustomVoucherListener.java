package com.fenbeitong.openapi.plugin.voucher.service;

import com.fenbeitong.openapi.plugin.voucher.dto.FinanceCustomVoucherCreateReqDto;
import com.fenbeitong.openapi.plugin.voucher.entity.FinanceBusinessData;
import com.fenbeitong.openapi.plugin.voucher.entity.OpenVoucherDraft;

import java.util.Map;

/**
 * <p>Title: IFinanceCustomVoucherListener</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/27 2:35 PM
 */
public interface IFinanceCustomVoucherListener {

    /**
     * 生成凭证分录
     *  @param businessData
     * @param srcData
     * @param openVoucherDraft
     */
    void createVoucherItem(FinanceBusinessData businessData, Map<String, Object> srcData, OpenVoucherDraft openVoucherDraft);

    /**
     * 凭证分录生成后 一般重写 分录合并逻辑
     *
     * @param reqDto
     */
    void afterVoucherCreated(FinanceCustomVoucherCreateReqDto reqDto);
}
