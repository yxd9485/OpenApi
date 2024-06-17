package com.fenbeitong.openapi.plugin.rpc.api.func.service;

import com.fenbeitong.openapi.plugin.rpc.api.func.model.CompanyBillExtInfoReqDTO;

import java.util.Map;

/**
 * <p>Title: ICompanyBillExtService</p>
 * <p>Description: 公司账单扩展服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/28 3:39 PM
 */
public interface ICompanyBillExtService {

    /**
     * 获取公司账单扩展字段
     *
     * @param req
     * @return
     */
    Map<String, Object> getExtInfo(CompanyBillExtInfoReqDTO req);
}
