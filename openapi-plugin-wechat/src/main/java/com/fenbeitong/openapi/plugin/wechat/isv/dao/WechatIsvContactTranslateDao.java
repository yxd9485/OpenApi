package com.fenbeitong.openapi.plugin.wechat.isv.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WechatIsvContactTranslate;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2020/09/22.
 */
@Component
public class WechatIsvContactTranslateDao extends OpenApiBaseDao<WechatIsvContactTranslate> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<WechatIsvContactTranslate> listWechatIsvContactTranslate(Map<String, Object> condition) {
        Example example = new Example(WechatIsvContactTranslate.class);
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
    public WechatIsvContactTranslate getWechatIsvContactTranslate(Map<String, Object> condition) {
        Example example = new Example(WechatIsvContactTranslate.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    public WechatIsvContactTranslate getByTaskId(String ossTaskId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("taskId", ossTaskId);
        return getWechatIsvContactTranslate(condition);
    }

    public WechatIsvContactTranslate getJobId(String jobId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("jobId", jobId);
        return getWechatIsvContactTranslate(condition);
    }
}
