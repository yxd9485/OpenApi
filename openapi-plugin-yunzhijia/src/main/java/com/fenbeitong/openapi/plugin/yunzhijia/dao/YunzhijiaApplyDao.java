package com.fenbeitong.openapi.plugin.yunzhijia.dao;


import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaApply;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by hanshuqi on 2020/03/27.
 */
@Component
public class YunzhijiaApplyDao extends OpenApiBaseDao<YunzhijiaApply> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<YunzhijiaApply> listYunzhijiaApply(Map<String, Object> condition) {
        Example example = new Example(YunzhijiaApply.class);
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
    public YunzhijiaApply getYunzhijiaApply(Map<String, Object> condition) {
        Example example = new Example(YunzhijiaApply.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
