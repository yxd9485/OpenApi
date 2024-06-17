package com.fenbeitong.openapi.plugin.dingtalk.isv.dao;

import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizData;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhen on 2020/07/14.
 */
@Component
public class OpenSyncBizDataDao extends OpenApiBaseDao<OpenSyncBizData> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<OpenSyncBizData> listOpenSyncBizData(Map<String, Object> condition, List<Integer> bizType) {
        Example example = new Example(OpenSyncBizData.class);
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
     *
     * @param condition
     * @return
     */
    public OpenSyncBizData getOpenSyncBizData(Map<String, Object> condition) {
        Example example = new Example(OpenSyncBizData.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 按bizType获取
     * @return
     */
    public String getSuiteTicket() {
        Map<String, Object> condition = new HashMap<>();
        condition.put("bizType", OpenSyncBizDataType.DINGTALK_ISV_SUITE_TICKET.getKey());
        OpenSyncBizData openSyncBizData = getOpenSyncBizData(condition);
        if (openSyncBizData == null || StringUtils.isBlank(openSyncBizData.getBizData())) {
            return null;
        }
        String bizData = openSyncBizData.getBizData();
        Map<String, Object> data = JsonUtils.toObj(bizData, Map.class);
        String suiteTicket = StringUtils.obj2str(data.get("suiteTicket"));
        return suiteTicket;
    }

}
