package com.fenbeitong.openapi.plugin.customize.qiqi.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.customize.qiqi.entity.OpenBudgetCostComparison;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @ClassName OpenBudgetCostComparisonDao
 * @Description 预算费用对照关系
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/6
 **/
@Slf4j
@Component
public class OpenBudgetCostComparisonDao extends OpenApiBaseDao<OpenBudgetCostComparison> {

    /**
     * 根据companyId和costId查询预算费用对照关系
     * @param companyId 公司id
     * @param costId 费用id
     * @return OpenBudgetCostComparison 预算费用关系
     */
    public OpenBudgetCostComparison getByCompanyIdAndCostId(String companyId, String costId) {
        Example example = new Example(OpenBudgetCostComparison.class);
        example.createCriteria().andEqualTo("companyId", companyId).andEqualTo("costId", costId);
        return getByExample(example);
    }

    /**
     * 根据公司id 数据来源 查询预算费用关系集合
     *
     * @param companyId 公司id
     * @param openType 来源类型
     * @return List<OpenBudgetCostComparison> 预算费用关系集合
     */
    public List<OpenBudgetCostComparison> listOpenBudgetCostComparison(String companyId, Integer openType) {
        Example example = new Example(OpenBudgetCostComparison.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("companyId", companyId)
            .andEqualTo("openType", openType);
        return listByExample(example);
    }

    /**
     * 根据公司id + 费用id + openType更新
     *
     * @param comparison 预算费用关系数据
     * @param companyId 公司id
     * @param costId 费用id
     * @param openType 来源类型
     */
    public void updateByCompanyIdAndCostIdAndOpenType(OpenBudgetCostComparison comparison, String companyId, String costId, Integer openType) {
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(costId) || ObjectUtils.isEmpty(openType)) {
            return;
        }
        Example example = new Example(OpenBudgetCostComparison.class);
        example.createCriteria()
            .andEqualTo("companyId", companyId)
            .andEqualTo("costId", costId)
            .andEqualTo("openType", openType);
        comparison.setUpdateTime(new Date());
        updateByExample(comparison, example);
    }

    /**
     * 根据公司id + 费用id + openType 删除 物理删除
     *
     * @param companyId 公司id
     * @param costId 费用id
     * @param openType 来源类型
     */
    public void deleteByCompanyIdAndCostIdAndOpenType(String companyId, String costId, Integer openType) {
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(costId) || ObjectUtils.isEmpty(openType)) {
            return;
        }
        Example example = new Example(OpenBudgetCostComparison.class);
        example.createCriteria()
            .andEqualTo("companyId", companyId)
            .andEqualTo("costId", costId)
            .andEqualTo("openType", openType);
        deleteByExample(example);
    }
}
