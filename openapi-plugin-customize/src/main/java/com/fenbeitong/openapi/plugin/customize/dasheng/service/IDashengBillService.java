package com.fenbeitong.openapi.plugin.customize.dasheng.service;

import com.fenbeitong.openapi.plugin.customize.dasheng.dto.OpenEbsBillDetailDto;

import java.util.Map;

/**
 * <p>Title: IDashengBillService</p>
 * <p>Description: 51talk账单推送服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/19 5:00 PM
 */
public interface IDashengBillService {

    /**
     * 保存并推送账单
     *
     * @param companyId 公司id
     * @param billNo    账单编号
     */
    void saveAndPushData(String companyId, String billNo);

    /**
     * 保存定制账单
     *
     * @param companyId 公司id
     * @param billNo    账单编号
     * @return 账单 年月
     */
    Map<String, Object> saveBillData(String companyId, String billNo);

    /**
     * 更新定制账单
     *
     * @param openEbsBillDetailDto 大生账单数据
     */
    void updateBillData(OpenEbsBillDetailDto openEbsBillDetailDto);

    /**
     * 推送定制账单
     *
     * @param companyId 公司id
     * @param billNo    账单编号
     * @param year      年
     * @param month     月
     */
    void pushBill(String companyId, String billNo, int year, int month);

}
