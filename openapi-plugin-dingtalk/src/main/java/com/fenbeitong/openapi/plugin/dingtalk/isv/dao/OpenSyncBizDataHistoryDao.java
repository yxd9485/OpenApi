package com.fenbeitong.openapi.plugin.dingtalk.isv.dao;

import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataHistory;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2020/07/15.
 */
@Component
public class OpenSyncBizDataHistoryDao extends OpenApiBaseDao<OpenSyncBizDataHistory> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenSyncBizDataHistory> listOpenSyncBizDataHistory(Map<String, Object> condition) {
        Example example = new Example(OpenSyncBizDataHistory.class);
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
    public OpenSyncBizDataHistory getOpenSyncBizDataHistory(Map<String, Object> condition) {
        Example example = new Example(OpenSyncBizDataHistory.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
