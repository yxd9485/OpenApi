package com.fenbeitong.openapi.plugin.customize.ziyouwuxian.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.customize.ziyouwuxian.entity.OpenBillSceneSumZywx;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaowei on 2020/10/29.
 */
@Component
public class OpenBillSceneSumZywxDao extends OpenApiBaseDao<OpenBillSceneSumZywx> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<OpenBillSceneSumZywx> listThBillSceneSumZywjx(Map<String, Object> condition) {
        Example example = new Example(OpenBillSceneSumZywx.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return listByExample(example);
    }

    public List<OpenBillSceneSumZywx> list(String billNo, String compnayId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("billNo", billNo);
        condition.put("companyId", compnayId);
        return listThBillSceneSumZywjx(condition);
    }

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public OpenBillSceneSumZywx getThBillSceneSumZywjx(Map<String, Object> condition) {
        Example example = new Example(OpenBillSceneSumZywx.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
