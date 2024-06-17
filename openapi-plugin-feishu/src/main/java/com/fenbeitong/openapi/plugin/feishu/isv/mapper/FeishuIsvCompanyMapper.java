package com.fenbeitong.openapi.plugin.feishu.isv.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by lizhen on 2020/06/01.
 */
@Component
@Mapper
public interface FeishuIsvCompanyMapper extends OpenApiBaseMapper<FeishuIsvCompany> {

}
