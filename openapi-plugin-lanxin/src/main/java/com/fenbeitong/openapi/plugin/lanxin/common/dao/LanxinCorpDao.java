package com.fenbeitong.openapi.plugin.lanxin.common.dao;


import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.lanxin.common.entity.LanxinCorp;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by duhui on 2021/12/06.
 */
@Component
public class LanxinCorpDao extends OpenApiBaseDao<LanxinCorp> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<LanxinCorp> listLanxinCorp(Map<String, Object> condition) {
        Example example = new Example(LanxinCorp.class);
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
    public LanxinCorp getLanxinCorp(Map<String, Object> condition) {
        Example example = new Example(LanxinCorp.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    public LanxinCorp selectByAppId(String appId) {
        if (!StringUtils.isBlank(appId)) {
            Map<String, Object> condition = new HashMap<>();
            condition.put("appId", appId);
            condition.put("state", 1);
            return getLanxinCorp(condition);
        }
        return null;
    }

    public LanxinCorp selectByCompanyId(String companyId) {
        if (!StringUtils.isBlank(companyId)) {
            Map<String, Object> condition = new HashMap<>();
            condition.put("companyId", companyId);
            condition.put("state", 1);
            return getLanxinCorp(condition);
        }
        return null;
    }

}
