package com.fenbeitong.openapi.plugin.customize.zhiou.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.customize.zhiou.entity.CustomizeBeisenCorp;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BeisenCorpDao
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/4
 **/
@Component
public class CustomizeBeisenCorpDao extends OpenApiBaseDao<CustomizeBeisenCorp> {
    /**
     * 使用map条件查询
     *
     * @param condition 查询条件
     * @return BeisenCorp 北森配置表
     */
    public CustomizeBeisenCorp getBeisenConfig(Map<String, Object> condition) {
        Example example = new Example(CustomizeBeisenCorp.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 根据公司id查询蓝凌配置
     * @param companyId 公司id
     * @return BeisenCorp 北森配置表
     */
    public CustomizeBeisenCorp getByCompanyId(String companyId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        return getBeisenConfig(condition);
    }
}
