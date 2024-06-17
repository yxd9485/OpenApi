package com.fenbeitong.openapi.plugin.dingtalk.yida.dao;

import com.fenbeitong.openapi.plugin.dingtalk.yida.entity.DingtalkYidaCorpApp;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhen on 2021/09/06.
 */
@Component
public class DingtalkYidaCorpAppDao extends OpenApiBaseDao<DingtalkYidaCorpApp> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<DingtalkYidaCorpApp> listDingtalkYidaCorpApp(Map<String, Object> condition) {
        Example example = new Example(DingtalkYidaCorpApp.class);
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
    public DingtalkYidaCorpApp getDingtalkYidaCorpApp(Map<String, Object> condition) {
        Example example = new Example(DingtalkYidaCorpApp.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }


    public DingtalkYidaCorpApp getDingtalkYidaCorpAppByCorpId(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return null;
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("corpId", corpId);
        return getDingtalkYidaCorpApp(condition);
    }

    public DingtalkYidaCorpApp getDingtalkYidaCorpAppByCompanyId(String companyId) {
        if (StringUtils.isBlank(companyId)) {
            return null;
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        return getDingtalkYidaCorpApp(condition);
    }
}
