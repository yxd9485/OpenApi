package com.fenbeitong.openapi.plugin.kingdee.support.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenThirdKingdeeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by ctl on 2021/07/01.
 */
@Component
@Mapper
public interface OpenThirdKingdeeConfigMapper extends OpenApiBaseMapper<OpenThirdKingdeeConfig> {

}
