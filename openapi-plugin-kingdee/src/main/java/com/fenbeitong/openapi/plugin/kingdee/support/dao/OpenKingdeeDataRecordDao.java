package com.fenbeitong.openapi.plugin.kingdee.support.dao;


import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeDataRecord;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**   
 * Created by duhui on 2022/01/14.
 */
@Component
public class OpenKingdeeDataRecordDao extends OpenApiBaseDao<OpenKingdeeDataRecord> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenKingdeeDataRecord> listOpenKingdeeDataRecord(Map<String, Object> condition) {
        Example example = new Example(OpenKingdeeDataRecord.class);
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
    public OpenKingdeeDataRecord getOpenKingdeeDataRecord(Map<String, Object> condition) {
        Example example = new Example(OpenKingdeeDataRecord.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
