package com.fenbeitong.openapi.plugin.seeyon.dao;

import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonExtInfo;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by hanshuqi on 2020/05/12.
 */
@Component
public class SeeyonExtInfoDao extends OpenApiBaseDao<SeeyonExtInfo> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<SeeyonExtInfo> listSeeyonExtInfo(Map<String, Object> condition) {
        Example example = new Example(SeeyonExtInfo.class);
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
    public SeeyonExtInfo getSeeyonExtInfo(Map<String, Object> condition) {
        Example example = new Example(SeeyonExtInfo.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
