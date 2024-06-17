package com.fenbeitong.openapi.plugin.customize.dashenlin.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.customize.dashenlin.entity.OpenDashenlinEmployeeCostAttribution;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2021/04/08.
 */
@Component
public class OpenDashenlinEmployeeCostAttributionDao extends OpenApiBaseDao<OpenDashenlinEmployeeCostAttribution> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenDashenlinEmployeeCostAttribution> listOpenDashenlinEmployeeCostAttribution(Map<String, Object> condition) {
        Example example = new Example(OpenDashenlinEmployeeCostAttribution.class);
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
    public OpenDashenlinEmployeeCostAttribution getOpenDashenlinEmployeeCostAttribution(Map<String, Object> condition) {
        Example example = new Example(OpenDashenlinEmployeeCostAttribution.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }


    public OpenDashenlinEmployeeCostAttribution getOpenDashenlinEmployeeCostAttribution(String thirdEmployeeId) {
        if (StringUtils.isBlank(thirdEmployeeId)) {
            return null;
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("thirdEmployeeId", thirdEmployeeId);
        return getOpenDashenlinEmployeeCostAttribution(condition);
    }
}
