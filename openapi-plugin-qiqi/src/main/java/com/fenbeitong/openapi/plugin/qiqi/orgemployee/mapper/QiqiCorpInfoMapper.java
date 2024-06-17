package com.fenbeitong.openapi.plugin.qiqi.orgemployee.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName QiqiCorpInfoMapper
 * @Description 企企企业配置
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/5/17 下午11:31
 **/
@Mapper
@Component
public interface QiqiCorpInfoMapper extends OpenApiBaseMapper<QiqiCorpInfo> {
}
