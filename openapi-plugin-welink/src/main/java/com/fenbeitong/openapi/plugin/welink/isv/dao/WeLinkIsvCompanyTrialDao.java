package com.fenbeitong.openapi.plugin.welink.isv.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvCompanyTrial;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2020/04/14.
 */
@Component
public class WeLinkIsvCompanyTrialDao extends OpenApiBaseDao<WeLinkIsvCompanyTrial> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<WeLinkIsvCompanyTrial> listWelinkIsvCompanyTrial(Map<String, Object> condition) {
        Example example = new Example(WeLinkIsvCompanyTrial.class);
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
    public WeLinkIsvCompanyTrial getWelinkIsvCompanyTrial(Map<String, Object> condition) {
        Example example = new Example(WeLinkIsvCompanyTrial.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    public WeLinkIsvCompanyTrial getWelinkIsvCompanyTrialByCorpId(String corpId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("corpId", corpId);
        return getWelinkIsvCompanyTrial(condition);
    }

    public WeLinkIsvCompanyTrial getWelinkIsvCompanyTrialByCompanyId(String companyId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        return getWelinkIsvCompanyTrial(condition);
    }


}
