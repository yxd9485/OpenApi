package com.fenbeitong.openapi.plugin.welink.isv.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvOrder;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2020/07/01.
 */
@Component
public class WeLinkIsvOrderDao extends OpenApiBaseDao<WeLinkIsvOrder> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<WeLinkIsvOrder> listWeLinkIsvOrder(Map<String, Object> condition) {
        Example example = new Example(WeLinkIsvOrder.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return listByExample(example);
    }

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public WeLinkIsvOrder getWeLinkIsvOrder(Map<String, Object> condition) {
        Example example = new Example(WeLinkIsvOrder.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
