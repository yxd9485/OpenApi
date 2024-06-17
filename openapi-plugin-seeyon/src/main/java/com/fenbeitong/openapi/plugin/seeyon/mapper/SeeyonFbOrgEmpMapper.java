package com.fenbeitong.openapi.plugin.seeyon.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgEmployee;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface SeeyonFbOrgEmpMapper extends OpenApiBaseMapper<SeeyonFbOrgEmp> {
}
