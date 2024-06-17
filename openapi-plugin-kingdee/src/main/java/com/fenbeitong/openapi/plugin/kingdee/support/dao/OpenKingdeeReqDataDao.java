package com.fenbeitong.openapi.plugin.kingdee.support.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeReqData;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created by duhui on 2021/07/27.
 */
@Component
public class OpenKingdeeReqDataDao extends OpenApiBaseDao<OpenKingdeeReqData> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<OpenKingdeeReqData> listOpenKingdeeReqData(Map<String, Object> condition) {
        Example example = new Example(OpenKingdeeReqData.class);
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
    public OpenKingdeeReqData getOpenKingdeeReqData(Map<String, Object> condition) {
        Example example = new Example(OpenKingdeeReqData.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }


    public List<OpenKingdeeReqData> getReqDataConfig(String companyId, String moduleType) {
        Map<String, Object> condition = Maps.newHashMap();
        condition.put("companyId", companyId);
        condition.put("moduleType", moduleType);
        condition.put("status", 1);
        return listOpenKingdeeReqData(condition);
    }

}
