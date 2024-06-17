package com.fenbeitong.openapi.plugin.dingtalk.isv.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 *
 * @author lizhen
 * @date 2020/07/14
 */
@Component
public class OpenSyncBizDataMediumDao extends OpenApiBaseDao<OpenSyncBizDataMedium> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenSyncBizDataMedium> listOpenSyncBizDataMedium(Map<String, Object> condition, List<Integer> bizType) {
        Example example = new Example(OpenSyncBizDataMedium.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        criteria.andLessThanOrEqualTo("nextExecute", new Date());
        criteria.andIn("bizType", bizType);
        return listByExample(example);
    }

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public OpenSyncBizDataMedium getOpenSyncBizDataMedium(Map<String, Object> condition) {
        Example example = new Example(OpenSyncBizDataMedium.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
