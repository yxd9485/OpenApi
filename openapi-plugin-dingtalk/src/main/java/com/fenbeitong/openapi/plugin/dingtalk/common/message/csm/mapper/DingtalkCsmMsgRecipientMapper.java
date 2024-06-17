package com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.entity.DingtalkCsmMsgRecipient;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by lizhen on 2020/11/12.
 */
@Component
@Mapper
public interface DingtalkCsmMsgRecipientMapper extends OpenApiBaseMapper<DingtalkCsmMsgRecipient> {

}
