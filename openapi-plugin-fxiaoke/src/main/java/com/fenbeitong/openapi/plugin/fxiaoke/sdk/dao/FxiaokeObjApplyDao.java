package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao;


import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeObjApply;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by hanshuqi on 2020/07/05.
 */
@Component
public class FxiaokeObjApplyDao extends OpenApiBaseDao<FxiaokeObjApply> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<FxiaokeObjApply> listFxiaokeObjApply(Map<String, Object> condition) {
        Example example = new Example(FxiaokeObjApply.class);
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
    public FxiaokeObjApply getFxiaokeObjApply(Map<String, Object> condition) {
        Example example = new Example(FxiaokeObjApply.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
