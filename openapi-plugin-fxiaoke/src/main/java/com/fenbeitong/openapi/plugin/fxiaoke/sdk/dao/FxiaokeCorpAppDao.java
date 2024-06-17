package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao;


import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeCorpApp;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by hanshuqi on 2020/07/01.
 */
@Component
public class FxiaokeCorpAppDao extends OpenApiBaseDao<FxiaokeCorpApp> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<FxiaokeCorpApp> listFxiaokeCorpApp(Map<String, Object> condition) {
        Example example = new Example(FxiaokeCorpApp.class);
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
    public FxiaokeCorpApp getFxiaokeCorpApp(Map<String, Object> condition) {
        Example example = new Example(FxiaokeCorpApp.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
