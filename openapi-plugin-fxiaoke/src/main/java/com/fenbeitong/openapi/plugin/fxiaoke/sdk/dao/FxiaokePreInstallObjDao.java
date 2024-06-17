package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao;


import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokePreInstallObj;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by hanshuqi on 2020/09/18.
 */
@Component
public class FxiaokePreInstallObjDao extends OpenApiBaseDao<FxiaokePreInstallObj> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<FxiaokePreInstallObj> listFxiaokePreInstallObj(Map<String, Object> condition) {
        Example example = new Example(FxiaokePreInstallObj.class);
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
    public FxiaokePreInstallObj getFxiaokePreInstallObj(Map<String, Object> condition) {
        Example example = new Example(FxiaokePreInstallObj.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
