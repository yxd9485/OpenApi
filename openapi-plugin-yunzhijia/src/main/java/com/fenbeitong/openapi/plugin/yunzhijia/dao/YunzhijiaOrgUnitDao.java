package com.fenbeitong.openapi.plugin.yunzhijia.dao;


import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaOrgUnit;
import com.fenbeitong.openapi.plugin.yunzhijia.mapper.YunzhijiaOrgUnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by hanshuqi on 2020/03/24.
 */
@Component
public class YunzhijiaOrgUnitDao extends OpenApiBaseDao<YunzhijiaOrgUnit> {

    @Autowired
    YunzhijiaOrgUnitMapper yunzhijiaOrgUnitMapper;

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<YunzhijiaOrgUnit> listYunzhijiaOrgUnit(Map<String, Object> condition) {
        Example example = new Example(YunzhijiaOrgUnit.class);
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
    public YunzhijiaOrgUnit getYunzhijiaOrgUnit(Map<String, Object> condition) {
        Example example = new Example(YunzhijiaOrgUnit.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
