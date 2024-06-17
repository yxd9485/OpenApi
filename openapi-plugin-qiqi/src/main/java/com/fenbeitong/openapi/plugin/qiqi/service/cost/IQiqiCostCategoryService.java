package com.fenbeitong.openapi.plugin.qiqi.service.cost;

import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCostReqDTO;
import com.fenbeitong.openapi.plugin.support.budget.dto.AddCostCategoryReqDTO;

import java.util.List;

/**
 * @ClassName IQiqiCostService
 * @Description 企企同步费用类别数据
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/28
 **/
public interface IQiqiCostCategoryService {
    /**
     * 全量拉取费用类别数据
     * @param companyId
     * @throws Exception
     */
    void syncQiqiCostCategory(String companyId) throws Exception;

    /**
     * 费用类别叶子节点字段转换
     * @param leafList
     * @param costInfos
     * @return
     * @throws Exception
     */
    List<AddCostCategoryReqDTO> costAllLeafConvert(List<QiqiCostReqDTO> leafList, List<QiqiCostReqDTO> costInfos);
}
