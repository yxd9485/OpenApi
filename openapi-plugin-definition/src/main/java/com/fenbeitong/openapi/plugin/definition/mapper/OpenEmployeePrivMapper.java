package com.fenbeitong.openapi.plugin.definition.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.definition.entity.OpenEmployeePriv;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by xiaowei on 2020/05/19.
 */
@Component
@Mapper
public interface OpenEmployeePrivMapper extends OpenApiBaseMapper<OpenEmployeePriv> {

}
