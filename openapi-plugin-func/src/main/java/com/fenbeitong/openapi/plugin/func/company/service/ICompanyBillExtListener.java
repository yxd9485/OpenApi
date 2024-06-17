package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoTransformDTO;

import java.util.Map;

/**
 * <p>Title: ICompanyBillExtListener</p>
 * <p>Description: 公司账单扩展字段扩展监听</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/26 2:23 PM
 */
public interface ICompanyBillExtListener {

    /**
     * 设置账单扩展字段
     *
     * @param companyId  公司id
     * @param srcData    源数据
     * @param resultData 目标数据
     */
    void setBillExt(String companyId, Map<String, Object> srcData, Map<String, Object> resultData, FuncBillExtInfoTransformDTO transformDto);
}
