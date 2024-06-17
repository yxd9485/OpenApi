package com.fenbeitong.openapi.plugin.lanxin.common.mapper;
import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.lanxin.common.entity.LanxinCorp;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by duhui on 2021/12/06.
 */
@Component
@Mapper
public interface LanxinCorpMapper extends OpenApiBaseMapper<LanxinCorp> {

}
