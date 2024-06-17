package com.fenbeitong.openapi.plugin.func.dao;

import com.fenbeitong.openapi.plugin.func.entity.OpenAltmanOrderConfig;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by xiaowei on 2020/05/26.
 */
@Component
public class OpenAltmanOrderConfigDao extends OpenApiBaseDao<OpenAltmanOrderConfig> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenAltmanOrderConfig> listOpenAltmanOrderConfig(Map<String, Object> condition) {
        Example example = new Example(OpenAltmanOrderConfig.class);
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
    public OpenAltmanOrderConfig getOpenAltmanOrderConfig(Map<String, Object> condition) {
        Example example = new Example(OpenAltmanOrderConfig.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
