package com.fenbeitong.openapi.plugin.dingtalk.isv.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by lizhen on 2020/07/13.
 */
@Component
@Mapper
public interface DingtalkIsvCompanyMapper extends OpenApiBaseMapper<DingtalkIsvCompany> {

}
