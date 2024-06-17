package com.fenbeitong.openapi.plugin.welink.isv.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by lizhen on 2020/07/01.
 */
@Component
@Mapper
public interface WeLinkIsvOrderMapper extends OpenApiBaseMapper<WeLinkIsvOrder> {

}
