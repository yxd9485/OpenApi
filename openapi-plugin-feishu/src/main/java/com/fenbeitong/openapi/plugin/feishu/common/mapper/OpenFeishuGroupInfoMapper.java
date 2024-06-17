package com.fenbeitong.openapi.plugin.feishu.common.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.feishu.common.entity.OpenFeishuGroupInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by zhangpeng on 2021/09/25.
 */
@Component
@Mapper
public interface OpenFeishuGroupInfoMapper extends OpenApiBaseMapper<OpenFeishuGroupInfo> {

}
