package com.fenbeitong.openapi.plugin.kingdee.support.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeDataRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by duhui on 2022/01/14.
 */
@Component
@Mapper
public interface OpenKingdeeDataRecordMapper extends OpenApiBaseMapper<OpenKingdeeDataRecord> {

}
