package com.fenbeitong.openapi.plugin.customize.zhiou.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.customize.zhiou.entity.OpenLandrayEkpConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName LandrayEkpConfigMapper
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/31
 **/
@Mapper
@Component
public interface LandrayEkpConfigMapper extends OpenApiBaseMapper<OpenLandrayEkpConfig> {
}
