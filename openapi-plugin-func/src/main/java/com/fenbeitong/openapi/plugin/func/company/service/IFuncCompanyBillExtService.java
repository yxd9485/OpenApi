package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoTransformDTO;
import com.fenbeitong.openapi.plugin.rpc.api.func.model.CompanyBillExtInfoReqDTO;

import java.util.Map;

/**
 * <p>Title: IFuncCompanyBillExtService</p>
 * <p>Description: 账单三方字段服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/1/15 9:00 PM
 */
public interface IFuncCompanyBillExtService {

    /**
     * 获取订单三方字段
     *
     * @param req 订单参数
     * @return 三方信息
     */
    Map<String, Object> getOrderThirdInfo(CompanyBillExtInfoReqDTO req);

    /**
     * 获取扩展信息
     * @param companyId
     * @param srcData
     * @param transformDto
     * @return
     */
    Map<String, Object> getExtInfo(String companyId, Map<String, Object> srcData, FuncBillExtInfoTransformDTO transformDto);
}
