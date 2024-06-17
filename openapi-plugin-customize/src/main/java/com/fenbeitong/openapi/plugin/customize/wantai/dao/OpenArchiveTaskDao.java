package com.fenbeitong.openapi.plugin.customize.wantai.dao;

import com.fenbeitong.openapi.plugin.customize.wantai.entity.OpenArchiveTask;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2022/07/26.
 */
@Component
public class OpenArchiveTaskDao extends OpenApiBaseDao<OpenArchiveTask> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenArchiveTask> listOpenArchiveTask(Map<String, Object> condition) {
        Example example = new Example(OpenArchiveTask.class);
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
    public OpenArchiveTask getOpenArchiveTask(Map<String, Object> condition) {
        Example example = new Example(OpenArchiveTask.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    public OpenArchiveTask getByTaskId(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return null;
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("taskId", taskId);
        return getOpenArchiveTask(condition);
    }


    public List<OpenArchiveTask> listByStatus(Integer status, String sysCode) {
        if (null == status || StringUtils.isBlank(sysCode)) {
            return null;
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("status", status);
        condition.put("sysCode", sysCode);
        return listOpenArchiveTask(condition);
    }
}
