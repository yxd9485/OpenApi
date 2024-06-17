package com.fenbeitong.openapi.plugin.landray.ekp.dao;

import com.fenbeitong.openapi.plugin.landray.ekp.entity.OpenLandrayEkpConfig;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lizhen
 * @date 2021/01/27
 */
@Component
public class OpenLandrayEkpConfigDao extends OpenApiBaseDao<OpenLandrayEkpConfig> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<OpenLandrayEkpConfig> listOpenLandrayEkpConfig(Map<String, Object> condition) {
        Example example = new Example(OpenLandrayEkpConfig.class);
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
    public OpenLandrayEkpConfig getOpenLandrayEkpConfig(Map<String, Object> condition) {
        Example example = new Example(OpenLandrayEkpConfig.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    public OpenLandrayEkpConfig getOpenLandrayEkpConfigByCompanyId(String companyId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        return getOpenLandrayEkpConfig(condition);
    }
}
