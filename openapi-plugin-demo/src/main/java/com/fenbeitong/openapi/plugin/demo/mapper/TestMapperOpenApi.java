package com.fenbeitong.openapi.plugin.demo.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.demo.entity.Test;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by log.chang on 2019/12/5.
 */
@Component
@Mapper
public interface TestMapperOpenApi extends OpenApiBaseMapper<Test> {
}
