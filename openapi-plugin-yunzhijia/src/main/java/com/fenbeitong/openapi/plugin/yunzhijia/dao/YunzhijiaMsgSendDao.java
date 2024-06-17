package com.fenbeitong.openapi.plugin.yunzhijia.dao;

import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaMsgSend;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by hanshuqi on 2020/04/07.
 */
@Component
public class YunzhijiaMsgSendDao extends OpenApiBaseDao<YunzhijiaMsgSend> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<YunzhijiaMsgSend> listYunzhijiaMsgSend(Map<String, Object> condition) {
        Example example = new Example(YunzhijiaMsgSend.class);
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
    public YunzhijiaMsgSend getYunzhijiaMsgSend(Map<String, Object> condition) {
        Example example = new Example(YunzhijiaMsgSend.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    public YunzhijiaMsgSend getMsgSendByCorpId(String corpId){
        Example example = new Example(YunzhijiaMsgSend.class);
        example.createCriteria().andEqualTo("corpId", corpId).andEqualTo("type", 1);
        return getByExample(example);
    }

}
