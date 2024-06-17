package com.fenbeitong.openapi.plugin.ecology.v8.standard.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenWorkflowFormInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 泛微工作流表单配置信息
 * Created by zhangpeng on 2021/06/04.
 */
@Component
@Mapper
public interface OpenWorkflowFormInfoMapper extends OpenApiBaseMapper<OpenWorkflowFormInfo> {

}
