package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.openapi.plugin.rpc.api.func.model.CompanyBillExtInfoReqDTO;
import com.fenbeitong.openapi.plugin.rpc.api.func.service.ICompanyBillExtService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>Title: DubboCompanyBillExtServiceImpl</p>
 * <p>Description: 公司账单扩展字段服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsyØØØ
 * @date 2020/5/28 4:04 PM
 */
@SuppressWarnings("unchecked")
@Slf4j
@Component
@DubboService(timeout = 15000)
public class DubboCompanyBillExtServiceImpl implements ICompanyBillExtService {

    @Autowired
    private IFuncCompanyBillExtService funcCompanyBillExtService;

    @Override
    public Map<String, Object> getExtInfo(CompanyBillExtInfoReqDTO req) {
        return funcCompanyBillExtService.getOrderThirdInfo(req);
    }
}
