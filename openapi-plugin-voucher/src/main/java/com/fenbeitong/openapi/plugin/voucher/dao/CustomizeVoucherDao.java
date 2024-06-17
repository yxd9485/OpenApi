package com.fenbeitong.openapi.plugin.voucher.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.voucher.entity.CustomizeVoucher;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created by huangsiyuan on 2021/09/28.
 */
@Component
public class CustomizeVoucherDao extends OpenApiBaseDao<CustomizeVoucher> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<CustomizeVoucher> listCustomizeVoucher(Map<String, Object> condition) {
        Example example = new Example(CustomizeVoucher.class);
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
    public CustomizeVoucher getCustomizeVoucher(Map<String, Object> condition) {
        Example example = new Example(CustomizeVoucher.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
