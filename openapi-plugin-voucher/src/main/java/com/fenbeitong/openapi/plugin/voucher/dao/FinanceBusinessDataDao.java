package com.fenbeitong.openapi.plugin.voucher.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.voucher.entity.FinanceBusinessData;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created by huangsiyuan on 2021/09/26.
 */
@Component
public class FinanceBusinessDataDao extends OpenApiBaseDao<FinanceBusinessData> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<FinanceBusinessData> listFinanceBusinessData(Map<String, Object> condition) {
        Example example = new Example(FinanceBusinessData.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return listByExample(example);
    }

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public FinanceBusinessData getFinanceBusinessData(Map<String, Object> condition) {
        Example example = new Example(FinanceBusinessData.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
