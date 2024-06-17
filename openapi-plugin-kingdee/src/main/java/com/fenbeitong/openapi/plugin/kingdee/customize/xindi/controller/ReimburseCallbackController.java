package com.fenbeitong.openapi.plugin.kingdee.customize.xindi.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.service.FuncAirOrderServiceImpl;
import com.fenbeitong.openapi.plugin.kingdee.customize.xindi.service.impl.ReimburseCallbackService;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.usercenter.api.service.company.ICompanyNewInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: LiShiBillCallbackController</p>
 * <p>Description: 报销单数据回传</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/9/09
 */
@RestController
@RequestMapping("/customize/reimburse")
@Slf4j
public class ReimburseCallbackController {

    @Autowired
    private ReimburseCallbackService reimburseCallbackService;

    @Autowired
    private FuncAirOrderServiceImpl funcAirOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;
    @DubboReference(check = false)
    private ICompanyNewInfoService companyNewInfoService;

    @RequestMapping("/push/{companyId}")
    public Object callback(@PathVariable("companyId") String companyId, @RequestBody String data) {
        if (reimburseCallbackService.pushBillData(companyId, data)){
            return FuncResponseUtils.success("success");
        }else {
            return FuncResponseUtils.error(-1,"failure");
        }
    }

}
