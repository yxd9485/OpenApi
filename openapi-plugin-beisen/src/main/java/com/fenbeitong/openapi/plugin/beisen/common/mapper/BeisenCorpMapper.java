package com.fenbeitong.openapi.plugin.beisen.common.mapper;

import com.fenbeitong.openapi.plugin.beisen.common.entity.BeisenCorp;
import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * <p>Title: BeisenCorpMapper<p>
 * <p>Description: 北森公司配置<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author: liuhong
 * @date: 2022/9/14 11:04
 */
@Component
@Mapper
public interface BeisenCorpMapper extends OpenApiBaseMapper<BeisenCorp> {
}
