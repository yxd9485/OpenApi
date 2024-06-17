package com.fenbeitong.openapi.plugin.dingtalk.isv.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.entity.DingtalkApproveTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by xiaohai on 2021/08/04.
 */
@Component
@Mapper
public interface DingtalkApproveTaskMapper extends OpenApiBaseMapper<DingtalkApproveTask> {

}
