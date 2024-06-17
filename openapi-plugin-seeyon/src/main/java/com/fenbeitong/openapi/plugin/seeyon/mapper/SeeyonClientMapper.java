package com.fenbeitong.openapi.plugin.seeyon.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface SeeyonClientMapper extends OpenApiBaseMapper<SeeyonClient> {
}
