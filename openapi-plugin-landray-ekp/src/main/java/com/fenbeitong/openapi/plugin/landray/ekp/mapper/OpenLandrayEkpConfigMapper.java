package com.fenbeitong.openapi.plugin.landray.ekp.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.landray.ekp.entity.OpenLandrayEkpConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 *
 * @author lizhen
 * @date 2021/01/27
 */
@Component
@Mapper
public interface OpenLandrayEkpConfigMapper extends OpenApiBaseMapper<OpenLandrayEkpConfig> {

}
