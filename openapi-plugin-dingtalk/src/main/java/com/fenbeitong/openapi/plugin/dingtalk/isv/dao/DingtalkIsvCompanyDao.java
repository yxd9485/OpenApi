package com.fenbeitong.openapi.plugin.dingtalk.isv.dao;

import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2020/07/13.
 */
@Component
public class DingtalkIsvCompanyDao extends OpenApiBaseDao<DingtalkIsvCompany> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<DingtalkIsvCompany> listDingtalkIsvCompany(Map<String, Object> condition) {
        Example example = new Example(DingtalkIsvCompany.class);
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
    public DingtalkIsvCompany getDingtalkIsvCompany(Map<String, Object> condition) {
        Example example = new Example(DingtalkIsvCompany.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }


    /**
     * corpId查DingtalkIsvCompany
     *
     * @param corpId
     * @return
     */
    public DingtalkIsvCompany getDingtalkIsvCompanyByCorpId(String corpId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("corpId", corpId);
        return getDingtalkIsvCompany(condition);
    }

    /**
     * companyId查DingtalkIsvCompany
     *
     * @param companyId
     * @return
     */
    public DingtalkIsvCompany getDingtalkIsvCompanyByCompanyId(String companyId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        return getDingtalkIsvCompany(condition);
    }

    public DingtalkIsvCompany getDingtalkIsvCompanyByCompanyIdAndName(String companyId, String companyName) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        condition.put("companyName", companyName);
        return getDingtalkIsvCompany(condition);
    }

}
