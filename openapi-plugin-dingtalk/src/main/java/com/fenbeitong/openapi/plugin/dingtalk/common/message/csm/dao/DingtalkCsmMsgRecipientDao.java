package com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.dao;

import com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.entity.DingtalkCsmMsgRecipient;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2020/11/12.
 */
@Component
public class DingtalkCsmMsgRecipientDao extends OpenApiBaseDao<DingtalkCsmMsgRecipient> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<DingtalkCsmMsgRecipient> listDingtalkCsmMsgRecipient(Map<String, Object> condition) {
        Example example = new Example(DingtalkCsmMsgRecipient.class);
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
    public DingtalkCsmMsgRecipient getDingtalkCsmMsgRecipient(Map<String, Object> condition) {
        Example example = new Example(DingtalkCsmMsgRecipient.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
