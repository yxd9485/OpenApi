package com.fenbeitong.openapi.plugin.voucher.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.voucher.entity.CustomizeVoucherMapping;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created by huangsiyuan on 2021/09/30.
 */
@Component
public class CustomizeVoucherMappingDao extends OpenApiBaseDao<CustomizeVoucherMapping> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<CustomizeVoucherMapping> listCustomizeVoucherMapping(Map<String, Object> condition) {
        Example example = new Example(CustomizeVoucherMapping.class);
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
    public CustomizeVoucherMapping getCustomizeVoucherMapping(Map<String, Object> condition) {
        Example example = new Example(CustomizeVoucherMapping.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
