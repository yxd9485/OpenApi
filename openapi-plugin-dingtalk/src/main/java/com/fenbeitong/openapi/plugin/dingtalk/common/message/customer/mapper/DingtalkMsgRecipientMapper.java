package com.fenbeitong.openapi.plugin.dingtalk.common.message.customer.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.customer.entity.DingtalkMsgRecipient;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by lizhen on 2020/11/13.
 */
@Component
@Mapper
public interface DingtalkMsgRecipientMapper extends OpenApiBaseMapper<DingtalkMsgRecipient> {

}
