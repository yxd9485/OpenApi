package com.fenbeitong.openapi.plugin.dingtalk.isv.dao;

import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMediumHistory;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2020/07/15.
 */
@Component
public class OpenSyncBizDataMediumHistoryDao extends OpenApiBaseDao<OpenSyncBizDataMediumHistory> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenSyncBizDataMediumHistory> listOpenSyncBizDataMediumHistory(Map<String, Object> condition) {
        Example example = new Example(OpenSyncBizDataMediumHistory.class);
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
    public OpenSyncBizDataMediumHistory getOpenSyncBizDataMediumHistory(Map<String, Object> condition) {
        Example example = new Example(OpenSyncBizDataMediumHistory.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
