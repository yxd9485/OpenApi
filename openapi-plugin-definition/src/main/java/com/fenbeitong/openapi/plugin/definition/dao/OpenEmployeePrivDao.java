package com.fenbeitong.openapi.plugin.definition.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.definition.entity.OpenEmployeePriv;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created by xiaowei on 2020/05/19.
 */
@Component
public class OpenEmployeePrivDao extends OpenApiBaseDao<OpenEmployeePriv> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenEmployeePriv> listOpenEmployeePriv(Map<String, Object> condition) {
        Example example = new Example(OpenEmployeePriv.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return listByExample(example);
    }

    /**
     * 使用map条件删除数据
     * @param condition
     * @return
     */
    public int deleteOpenEmployeePriv(Map<String, Object> condition) {
        Example example = new Example(OpenEmployeePriv.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            if (key.equals("companyId")) {
                criteria.andEqualTo(key, condition.get(key));
            } else if (key.equals("scenes")) {
                criteria.andIn("scene", (List)condition.get("scenes"));
            } else if (key.equals("roleTypes")) {
                criteria.andIn("roleType", (List)condition.get("roleTypes"));
            }
        }
        return deleteByExample(example);
    }

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public OpenEmployeePriv getOpenEmployeePriv(Map<String, Object> condition) {
        Example example = new Example(OpenEmployeePriv.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
