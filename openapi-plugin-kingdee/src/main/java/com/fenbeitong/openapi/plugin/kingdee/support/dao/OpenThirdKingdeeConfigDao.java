package com.fenbeitong.openapi.plugin.kingdee.support.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenThirdKingdeeConfig;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by ctl on 2021/07/01.
 */
@Component
public class OpenThirdKingdeeConfigDao extends OpenApiBaseDao<OpenThirdKingdeeConfig> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenThirdKingdeeConfig> listOpenThirdKingdeeConfig(Map<String, Object> condition) {
        Example example = new Example(OpenThirdKingdeeConfig.class);
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
    public OpenThirdKingdeeConfig getOpenThirdKingdeeConfig(Map<String, Object> condition) {
        Example example = new Example(OpenThirdKingdeeConfig.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
