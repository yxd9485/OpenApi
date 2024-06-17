package com.fenbeitong.openapi.plugin.seeyon.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonExtInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by hanshuqi on 2020/05/12.
 */
@Component
@Mapper
public interface SeeyonExtInfoMapper extends OpenApiBaseMapper<SeeyonExtInfo> {

}
