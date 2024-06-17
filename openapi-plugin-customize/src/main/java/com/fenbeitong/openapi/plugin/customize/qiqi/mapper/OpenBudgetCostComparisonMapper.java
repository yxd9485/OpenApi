package com.fenbeitong.openapi.plugin.customize.qiqi.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.customize.qiqi.entity.OpenBudgetCostComparison;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName OpenBudgetCostComparisonMapper
 * @Description 预算费用对照
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/6
 **/
@Mapper
@Component
public interface OpenBudgetCostComparisonMapper extends OpenApiBaseMapper<OpenBudgetCostComparison> {
}
