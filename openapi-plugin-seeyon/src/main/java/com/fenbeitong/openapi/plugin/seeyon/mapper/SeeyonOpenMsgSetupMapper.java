package com.fenbeitong.openapi.plugin.seeyon.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOpenMsgSetup;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by hanshuqi on 2020/05/12.
 */
@Component
@Mapper
public interface SeeyonOpenMsgSetupMapper extends OpenApiBaseMapper<SeeyonOpenMsgSetup> {

    public List<SeeyonOpenMsgSetup> getSetupListWithDef(Map map);



}
