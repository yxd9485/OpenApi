package com.fenbeitong.openapi.plugin.feishu.common.dao;

import com.fenbeitong.openapi.plugin.feishu.common.entity.TbAttendanceFeishu;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**   
 * Created by zhangpeng on 2021/09/26.
 */
@Component
public class TbAttendanceFeishuDao extends OpenApiBaseDao<TbAttendanceFeishu> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<TbAttendanceFeishu> listTbAttendanceFeishu(Map<String, Object> condition) {
        Example example = new Example(TbAttendanceFeishu.class);
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
    public TbAttendanceFeishu getTbAttendanceFeishu(Map<String, Object> condition) {
        Example example = new Example(TbAttendanceFeishu.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

}
