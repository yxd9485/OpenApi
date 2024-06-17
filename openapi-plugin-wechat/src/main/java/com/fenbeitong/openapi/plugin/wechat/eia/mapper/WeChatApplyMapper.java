package com.fenbeitong.openapi.plugin.wechat.eia.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.WeChatApply;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by dave.hansins on 19/12/16.
 */
@Component
@Mapper
public interface WeChatApplyMapper  extends OpenApiBaseMapper<WeChatApply> {
}
