package com.fenbeitong.openapi.plugin.rpc.api.func.service;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: IVoucherBillExtService</p>
 * <p>Description: 分贝券账单扩展字段</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/13 4:01 PM
 */
public interface IVoucherBillExtService {

    /**
     * 查询发放记录对应的三方信息
     *
     * @param companyId          公司id
     * @param vouchersTaskIdList 发放任务id
     * @return 以发放任务明细id为key  三方信息为value的键值对
     */
    Map<String, Map<String, Object>> getVoucherTaskExtInfo(String companyId, List<String> vouchersTaskIdList);

    /**
     * 查询消费流水对应的三方信息
     *
     * @param companyId         公司id
     * @param voucherFlowIdList 消费流水id
     * @return 以消费流水id为key  三方信息为value的键值对
     */
    Map<String, Map<String, Object>> getVoucherFlowExtInfo(String companyId, List<String> voucherFlowIdList);
}
