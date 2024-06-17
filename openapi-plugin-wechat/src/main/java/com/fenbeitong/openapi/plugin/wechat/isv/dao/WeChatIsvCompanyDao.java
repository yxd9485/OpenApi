package com.fenbeitong.openapi.plugin.wechat.isv.dao;

import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2020/03/20.
 */
@Component
public class WeChatIsvCompanyDao extends OpenApiBaseDao<WeChatIsvCompany> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<WeChatIsvCompany> listQywxIsvCompany(Map<String, Object> condition) {
        Example example = new Example(WeChatIsvCompany.class);
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
    public WeChatIsvCompany getQywxIsvCompany(Map<String, Object> condition) {
        Example example = new Example(WeChatIsvCompany.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    public WeChatIsvCompany getByCorpId(String corpId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("corpId", corpId);
        return getQywxIsvCompany(condition);
    }

    public WeChatIsvCompany getByCompanyId(String companyId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        return getQywxIsvCompany(condition);
    }

}
