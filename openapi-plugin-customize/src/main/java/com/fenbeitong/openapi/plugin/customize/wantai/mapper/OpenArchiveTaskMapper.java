package com.fenbeitong.openapi.plugin.customize.wantai.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.customize.wantai.entity.OpenArchiveTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by lizhen on 2022/07/26.
 */
@Component
@Mapper
public interface OpenArchiveTaskMapper extends OpenApiBaseMapper<OpenArchiveTask> {

}
