package com.fenbeitong.openapi.plugin.kingdee.support.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangpeng on 2021/06/04.
 */
@Component
public class OpenKingdeeUrlConfigDao extends OpenApiBaseDao<OpenKingdeeUrlConfig> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenKingdeeUrlConfig> listOpenKingdeeUrlConfig(Map<String, Object> condition) {
        Example example = new Example(OpenKingdeeUrlConfig.class);
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
    public OpenKingdeeUrlConfig getOpenKingdeeUrlConfig(Map<String, Object> condition) {
        Example example = new Example(OpenKingdeeUrlConfig.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 根据公司id查询配置信息
     */
    public OpenKingdeeUrlConfig getByCompanyId(String companyId) {
        Example example = new Example(OpenKingdeeUrlConfig.class);
        example.createCriteria().andEqualTo("companyId", companyId);
        return getByExample(example);
    }

}
