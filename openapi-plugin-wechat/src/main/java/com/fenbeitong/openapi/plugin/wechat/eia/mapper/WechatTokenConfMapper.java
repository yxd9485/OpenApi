package com.fenbeitong.openapi.plugin.wechat.eia.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.WechatTokenConf;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by huangsiyuan on 2020/02/26.
 */
@Component
@Mapper
public interface WechatTokenConfMapper extends OpenApiBaseMapper<WechatTokenConf> {

}
