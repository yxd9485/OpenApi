package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao;


import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeTask;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.mapper.FxiaokeTaskMapper;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by hanshuqi on 2020/07/01.
 */
@Component
public class FxiaokeTaskDao extends OpenApiBaseDao<FxiaokeTask> {

    @Autowired
    FxiaokeTaskMapper fxiaokeTaskMapper;
    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<FxiaokeTask> listFxiaokeTask(Map<String, Object> condition) {
        Example example = new Example(FxiaokeTask.class);
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
    public FxiaokeTask getFxiaokeTask(Map<String, Object> condition) {
        Example example = new Example(FxiaokeTask.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 根据条件过滤
     * @param corpId
     * @param dataId
     * @param taskType
     * @return
     */
    public List<FxiaokeTask> listFxiaokeUpdateOrAddTaskWithCondition(String corpId, String dataId, List<String> taskType) {
        List<FxiaokeTask> fxiaokeTasks =null;
        try{
            Example example = new Example(FxiaokeTask.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("corpId", corpId);
            criteria.andEqualTo("dataId", dataId);
            criteria.andIn("taskType", taskType);
            fxiaokeTasks = listByExample(example);

        }catch (Exception e){
           e.printStackTrace();
           return null;
        }
        return fxiaokeTasks;
    }

    public void insert2HistoryById(Long id){
        fxiaokeTaskMapper.insert2HistoryById(id);
    }

    public int countFxiaokeNeedProcessedTask() {
        return fxiaokeTaskMapper.countFxiaokeNeedProcessedTask();
    }

    public List<FxiaokeTask> getFxiaokeNeedProcessedTaskList(int limit){
        return fxiaokeTaskMapper.getFxiaokeNeedProcessedTaskList(limit);
    }


}
