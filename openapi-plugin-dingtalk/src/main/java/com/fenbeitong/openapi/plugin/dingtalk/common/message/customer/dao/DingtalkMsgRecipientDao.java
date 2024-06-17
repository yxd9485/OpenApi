package com.fenbeitong.openapi.plugin.dingtalk.common.message.customer.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.customer.entity.DingtalkMsgRecipient;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2020/11/13.
 */
@Component
public class DingtalkMsgRecipientDao extends OpenApiBaseDao<DingtalkMsgRecipient> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<DingtalkMsgRecipient> listDingtalkMsgRecipient(Map<String, Object> condition) {
        Example example = new Example(DingtalkMsgRecipient.class);
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
    public DingtalkMsgRecipient getDingtalkMsgRecipient(Map<String, Object> condition) {
        Example example = new Example(DingtalkMsgRecipient.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
