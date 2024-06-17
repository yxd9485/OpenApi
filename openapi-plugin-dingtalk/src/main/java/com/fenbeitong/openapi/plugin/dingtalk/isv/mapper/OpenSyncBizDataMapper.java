package com.fenbeitong.openapi.plugin.dingtalk.isv.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 *
 * @author lizhen
 * @date 2020/07/14
 */
@Component
@Mapper
public interface OpenSyncBizDataMapper extends OpenApiBaseMapper<OpenSyncBizData> {

}
