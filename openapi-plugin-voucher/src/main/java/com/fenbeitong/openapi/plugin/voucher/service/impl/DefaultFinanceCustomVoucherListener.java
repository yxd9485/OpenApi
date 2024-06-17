package com.fenbeitong.openapi.plugin.voucher.service.impl;

import com.fenbeitong.openapi.plugin.voucher.dto.FinanceCustomVoucherCreateReqDto;
import com.fenbeitong.openapi.plugin.voucher.entity.FinanceBusinessData;
import com.fenbeitong.openapi.plugin.voucher.entity.OpenVoucherDraft;
import com.fenbeitong.openapi.plugin.voucher.service.IFinanceCustomVoucherListener;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>Title: DefaultFinanceCustomVoucherListener</p>
 * <p>Description: 默认自定义</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/27 2:37 PM
 */
@Primary
@Component
public class DefaultFinanceCustomVoucherListener implements IFinanceCustomVoucherListener {

    @Override
    public void createVoucherItem(FinanceBusinessData businessData, Map<String, Object> srcData, OpenVoucherDraft openVoucherDraft) {

    }

    @Override
    public void afterVoucherCreated(FinanceCustomVoucherCreateReqDto reqDto) {

    }
}
