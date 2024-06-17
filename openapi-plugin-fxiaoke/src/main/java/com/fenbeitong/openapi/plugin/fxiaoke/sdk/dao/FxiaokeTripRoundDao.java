package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao;


import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeTripRound;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by hanshuqi on 2020/07/06.
 */
@Component
public class FxiaokeTripRoundDao extends OpenApiBaseDao<FxiaokeTripRound> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<FxiaokeTripRound> listFxiaokeTripRound(Map<String, Object> condition) {
        Example example = new Example(FxiaokeTripRound.class);
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
    public FxiaokeTripRound getFxiaokeTripRound(Map<String, Object> condition) {
        Example example = new Example(FxiaokeTripRound.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
