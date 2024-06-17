package com.fenbeitong.openapi.plugin.customize.qiqi.service;

import com.fenbeitong.openapi.plugin.customize.qiqi.common.QiqiResultEntity;
import com.fenbeitong.openapi.plugin.customize.qiqi.dto.ComparisonErrorRespDTO;
import com.fenbeitong.openapi.plugin.customize.qiqi.dto.QiqiReqDTO;
import com.fenbeitong.openapi.plugin.customize.qiqi.entity.OpenBudgetCostComparison;

import java.util.List;

/**
 * @ClassName OpenBudgetCostComparison
 * @Description 同步预算费用对照服务
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/7
 **/
public interface IOpenBudgetCostComparisonService {
    /**
     * 全量同步预算费用对照
     * @param companyId 公司id
     * @param openType  来源类型
     * @param budgetCostComparisonList 预算费用对照集合
     */
    List<ComparisonErrorRespDTO> syncAll(String companyId, int openType, List<OpenBudgetCostComparison> budgetCostComparisonList);

    /**
     * 企企自定义档案及费用全量同步
     *
     * @param companyId 公司id
     * @throws Exception
     */
    void syncAllArchiveAndCost(String companyId) throws Exception;

    /**
     * 企企查询接口
     * @param qiqiReqDto 企企接口参数封装
     * @return QiqiResultEntity 企企返回结果
     */
    QiqiResultEntity qiqiPostList(QiqiReqDTO qiqiReqDto);
}
