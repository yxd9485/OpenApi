package com.fenbeitong.openapi.plugin.customize.wawj.service.impl;

import com.fenbeitong.fenbeipay.api.model.dto.vouchers.resp.VouchersOperationFlowRespRPCDTO;
import com.fenbeitong.openapi.plugin.func.company.service.IVoucherBillExtListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>Title: WawjVoucherBillExtListener</p>
 * <p>Description: 我爱我家分贝券流水扩展字段</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/11/29 2:34 PM
 */
@Service
public class WawjVoucherBillExtListener implements IVoucherBillExtListener {

    @Autowired
    private WawjThirdInfoServiceImpl wawjSecondLevelService;

    @Override
    public void setBillExt(String companyId, VouchersOperationFlowRespRPCDTO flowRespDto, Map<String, Object> thirdInfo) {
        wawjSecondLevelService.setThirdInfo(companyId, thirdInfo);
    }
}
