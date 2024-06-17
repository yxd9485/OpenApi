package com.fenbeitong.openapi.plugin.wechat.eia.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.WeChatTokenKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by dave.hansins on 19/12/13.
 */
@Component
@Mapper
public interface WeChatTokenKeyMapper extends OpenApiBaseMapper<WeChatTokenKey>{

}
