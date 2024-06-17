package com.fenbeitong.openapi.plugin.yunzhijia.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaMsgSend;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by hanshuqi on 2020/04/07.
 */
@Component
@Mapper
public interface YunzhijiaMsgSendMapper extends OpenApiBaseMapper<YunzhijiaMsgSend> {

}
