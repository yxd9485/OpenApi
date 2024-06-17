package com.fenbeitong.openapi.plugin.beisen.common.dao;

import com.fenbeitong.openapi.plugin.beisen.common.entity.BeisenCorp;
import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: BeisenCorpDao<p>
 * <p>Description: 北森公司dao<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/9/13 21:28
 */
@Component
public class BeisenCorpDao extends OpenApiBaseDao<BeisenCorp> {

    /**
     * 使用map条件查询
     *
     * @param condition 查询条件
     * @return BeisenCorp 北森配置表
     */
    public BeisenCorp getBeisenConfig(Map<String, Object> condition) {
        Example example = new Example(BeisenCorp.class);
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
    public BeisenCorp getByCompanyId(String companyId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        return getBeisenConfig(condition);
    }
}
