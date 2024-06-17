package com.fenbeitong.openapi.plugin.func.company.controller;

import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.company.service.IFuncCompanyBillExtService;
import com.fenbeitong.openapi.plugin.func.company.service.OpenBillExtInfoStatusServiceImpl;
import com.fenbeitong.openapi.plugin.rpc.api.func.model.CompanyBillExtInfoReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>Title: FuncCompanyBillExtController</p>
 * <p>Description: 公司账单三方id</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/1/15 8:50 PM
 */
@RestController
@RequestMapping("/func/company/bill/ext")
public class FuncCompanyBillExtController {

    @Autowired
    private IFuncCompanyBillExtService companyBillExtService;

    @Autowired
    private OpenBillExtInfoStatusServiceImpl openBillExtInfoStatusService;

    @RequestMapping("/getOrderThirdInfo")
    public Object getOrderThirdInfo(@RequestBody CompanyBillExtInfoReqDTO req) {
        Map thirdInfoMap = companyBillExtService.getOrderThirdInfo(req);
        return FuncResponseUtils.success(thirdInfoMap);
    }

    @RequestMapping("/checkFields")
    public Object checkFields() {
        Object res = openBillExtInfoStatusService.noticeCheckMsg();
        return FuncResponseUtils.success(res);
    }
}
