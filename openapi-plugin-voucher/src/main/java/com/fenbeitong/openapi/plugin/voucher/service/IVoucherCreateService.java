package com.fenbeitong.openapi.plugin.voucher.service;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: IVoucherCreateService</p>
 * <p>Description: 凭证生成服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/23 8:36 PM
 */
public interface IVoucherCreateService {


    /**
     * 加载凭证业务数据
     *
     * @param companyId   公司id
     * @param voucherType 1:报销单;2:账单;3:对公付款
     * @param batchId     批次号
     * @return 凭证业务数据
     */
    List<Map<String, Object>> listBusinessData(String companyId, int voucherType, String batchId);

    /**
     * 生成凭证
     *
     * @param companyId   公司id
     * @param operatorId  操作人id
     * @param operator    操作人
     * @param voucherType 1:报销单;2:账单;3:对公付款
     * @param batchId     批次号
     * @param callBackUrl 回调地址
     */
    void createVoucher(String companyId, String operatorId, String operator, int voucherType, String batchId, String callBackUrl);

    /**
     * 根据源数据生成凭证
     *
     * @param companyId   公司id
     * @param operatorId  操作人id
     * @param operator    操作人
     * @param voucherType 1:报销单;2:账单;3:对公付款
     * @param batchId     批次号
     * @param srcList     业务数据
     * @param callBackUrl 回调地址
     */
    void createVoucherBySrc(String companyId, String operatorId, String operator, int voucherType, String batchId, List<Map<String, Object>> srcList, String callBackUrl);

    /**
     * 导出excel
     *
     * @param batchId
     * @param excelConfigId
     * @return excel url
     */
    String exportExcel(String batchId, Long excelConfigId);
}
