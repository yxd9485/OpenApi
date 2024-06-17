package com.fenbeitong.openapi.plugin.definition.dao;

import com.fenbeitong.openapi.plugin.definition.entity.AttrSpec;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by xiaowei on 2020/05/19.
 */
@Component
public class AttrSpecDao extends OpenApiBaseDao<AttrSpec> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<AttrSpec> listAttrSpec(Map<String, Object> condition) {
        Example example = new Example(AttrSpec.class);
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
    public AttrSpec getAttrSpec(Map<String, Object> condition) {
        Example example = new Example(AttrSpec.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
