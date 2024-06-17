package com.fenbeitong.openapi.plugin.dingtalk.yida.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.dingtalk.yida.entity.DingtalkYidaCorp;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by lizhen on 2021/08/13.
 */
@Component
@Mapper
public interface DingtalkYidaCorpMapper extends OpenApiBaseMapper<DingtalkYidaCorp> {

}
