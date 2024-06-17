package com.fenbeitong.openapi.plugin.zhongxin.isv.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.zhongxin.isv.entity.ZhongxinIsvUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by wanghaoqiang on 2021/04/22.
 */
@Component
@Mapper
public interface ZhongxinIsvUserMapper extends OpenApiBaseMapper<ZhongxinIsvUser> {

}
