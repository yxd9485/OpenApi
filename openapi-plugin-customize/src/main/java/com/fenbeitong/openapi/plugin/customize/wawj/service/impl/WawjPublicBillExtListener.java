package com.fenbeitong.openapi.plugin.customize.wawj.service.impl;

import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoTransformDTO;
import com.fenbeitong.openapi.plugin.func.company.service.ICompanyBillExtListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * <p>Title: WawPublicBillExtListener</p>
 * <p>Description: 我爱我家商务消费账单扩展字段</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/11/29 2:15 PM
 */
@Service
public class WawjPublicBillExtListener implements ICompanyBillExtListener {

    @Autowired
    private WawjThirdInfoServiceImpl wawjSecondLevelService;

    @Override
    public void setBillExt(String companyId, Map<String, Object> srcData, Map<String, Object> resultData, FuncBillExtInfoTransformDTO transformDto) {
        wawjSecondLevelService.setThirdInfo(companyId, resultData);
    }
}
