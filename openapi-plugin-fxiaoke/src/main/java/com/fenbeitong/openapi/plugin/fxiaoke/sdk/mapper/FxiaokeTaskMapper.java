package com.fenbeitong.openapi.plugin.fxiaoke.sdk.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeTask;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by hanshuqi on 2020/07/01.
 */
@Component
@Mapper
public interface FxiaokeTaskMapper extends OpenApiBaseMapper<FxiaokeTask> {

    int countFxiaokeNeedProcessedTask();

    List<FxiaokeTask> getFxiaokeNeedProcessedTaskList(@Param("limit") int limit);

    void insert2HistoryById(@Param("id") Long id);
}
