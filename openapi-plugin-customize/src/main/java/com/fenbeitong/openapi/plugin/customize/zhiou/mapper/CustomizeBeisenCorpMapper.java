package com.fenbeitong.openapi.plugin.customize.zhiou.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.customize.zhiou.entity.CustomizeBeisenCorp;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName BeisenCorpMapper
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/4
 **/
@Mapper
@Component
public interface CustomizeBeisenCorpMapper extends OpenApiBaseMapper<CustomizeBeisenCorp> {
}
