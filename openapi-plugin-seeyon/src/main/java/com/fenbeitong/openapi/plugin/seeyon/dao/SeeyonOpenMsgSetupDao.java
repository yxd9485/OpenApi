package com.fenbeitong.openapi.plugin.seeyon.dao;

import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOpenMsgSetup;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.seeyon.mapper.SeeyonOpenMsgSetupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created by hanshuqi on 2020/05/12.
 */
@Component
public class SeeyonOpenMsgSetupDao extends OpenApiBaseDao<SeeyonOpenMsgSetup> {
    @Autowired
    SeeyonOpenMsgSetupMapper seeyonOpenMsgSetupMapper;

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<SeeyonOpenMsgSetup> listOpenMsgSetup(Map<String, Object> condition) {
        Example example = new Example(SeeyonOpenMsgSetup.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return listByExample(example);
    }

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public SeeyonOpenMsgSetup getOpenMsgSetup(Map<String, Object> condition) {
        Example example = new Example(SeeyonOpenMsgSetup.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }


    public List<SeeyonOpenMsgSetup> seeyonOpenMsgSetupList(Map<Object, Object> paramMap) {
        List<SeeyonOpenMsgSetup> setupListWithDef = seeyonOpenMsgSetupMapper.getSetupListWithDef(paramMap);
        return setupListWithDef;
    }
}
