package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service;


import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.dto.KingdeePayableDTO;

/**
 * @author ctl
 * @date 2021/7/2
 */
public interface PayableService {

    /**
     * 账单生成应付单
     *
     * @param companyId
     * @param billNo
     * @param kingDeeCompanyFieldName 劳动合同公司主体编码 字段名
     * @param kingDeeDeptFieldName 金蝶部门编码 字段名
     */
    void convertPayable(String companyId, String billNo, String kingDeeCompanyFieldName, String kingDeeDeptFieldName);

    /**
     * 推送应付单
     *
     * @param data
     * @param companyId
     */
    Object pushPayable(KingdeePayableDTO data, String companyId);
}
