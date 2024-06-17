package com.fenbeitong.openapi.plugin.dingtalk.isv.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataHistory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by lizhen on 2020/07/15.
 */
@Component
@Mapper
public interface OpenSyncBizDataHistoryMapper extends OpenApiBaseMapper<OpenSyncBizDataHistory> {

}
