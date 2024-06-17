package com.fenbeitong.openapi.plugin.feishu.isv.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhen on 2020/06/01.
 */
@Component
public class FeishuIsvCompanyDao extends OpenApiBaseDao<FeishuIsvCompany> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<FeishuIsvCompany> listFeishuIsvCompany(Map<String, Object> condition) {
        Example example = new Example(FeishuIsvCompany.class);
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
    public FeishuIsvCompany getFeishuIsvCompany(Map<String, Object> condition) {
        Example example = new Example(FeishuIsvCompany.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }


    /**
     * corpId查FeishuIsvCompany
     *
     * @param corpId
     * @return
     */
    public FeishuIsvCompany getFeiShuIsvCompanyByCorpId(String corpId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("corpId", corpId);
        return getFeishuIsvCompany(condition);
    }

    /**
     * companyId查FeishuIsvCompany
     *
     * @param companyId
     * @return
     */
    public FeishuIsvCompany getFeiShuIsvCompanyByCompanyId(String companyId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        return getFeishuIsvCompany(condition);
    }


    /**
     * 获取所有的公司
     * @return
     */
    public List<FeishuIsvCompany> getFeiShuIsvAllCompany() {
        Map<String, Object> condition = new HashMap<>();
        condition.put("state", 1);
        return listFeishuIsvCompany(condition);
    }

    /**
     * 根据创建时间和公司id查询
     * @return
     */
    public List<FeishuIsvCompany> getFeiShuIsvByIdAndTime(List<String> companyIds,String createTimeBegin,String createTimeEnd) {
        Example example = new Example(FeishuIsvCompany.class);
        Example.Criteria criteria = example.createCriteria();
        if(CollectionUtil.isNotEmpty(companyIds)) {
            criteria.andIn("companyId", companyIds);
        }
        if(StringUtil.isNotEmpty(createTimeBegin)||StringUtil.isNotEmpty(createTimeEnd)) {
            criteria.andBetween("createTime", createTimeBegin,createTimeEnd);
        }
        return listByExample(example);
    }
}
