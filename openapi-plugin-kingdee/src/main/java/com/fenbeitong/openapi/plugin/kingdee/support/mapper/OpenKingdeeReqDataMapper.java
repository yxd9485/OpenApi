package com.fenbeitong.openapi.plugin.kingdee.support.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeReqData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by duhui on 2021/07/27.
 */
@Component
@Mapper
public interface OpenKingdeeReqDataMapper extends OpenApiBaseMapper<OpenKingdeeReqData> {

}
