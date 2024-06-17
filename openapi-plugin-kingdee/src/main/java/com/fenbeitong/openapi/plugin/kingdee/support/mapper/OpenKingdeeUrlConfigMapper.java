package com.fenbeitong.openapi.plugin.kingdee.support.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by zhangpeng on 2021/06/04.
 */
@Component
@Mapper
public interface OpenKingdeeUrlConfigMapper extends OpenApiBaseMapper<OpenKingdeeUrlConfig> {

}
