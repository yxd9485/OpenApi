package com.fenbeitong.openapi.plugin.customize.ziyouwuxian.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.customize.ziyouwuxian.entity.OpenBillSumZywx;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaowei on 2020/10/29.
 */
@Component
public class OpenBillSumZywxDao extends OpenApiBaseDao<OpenBillSumZywx> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<OpenBillSumZywx> listThBillSumZywjx(Map<String, Object> condition) {
        Example example = new Example(OpenBillSumZywx.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return listByExample(example);
    }

    public List<OpenBillSumZywx> list(String billNo, String compnayId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("billNo", billNo);
        condition.put("companyId", compnayId);
        return listThBillSumZywjx(condition);
    }

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public OpenBillSumZywx getThBillSumZywjx(Map<String, Object> condition) {
        Example example = new Example(OpenBillSumZywx.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
