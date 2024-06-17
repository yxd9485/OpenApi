package com.fenbeitong.openapi.plugin.landray.ekp.service;

import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.revert.apply.service.AbstractBuildFormDataService;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * 蓝凌表单内容构造
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
public abstract class LandrayFormDataBuildService extends AbstractBuildFormDataService {

    public abstract MultiValueMap<String,Object> buildFormDataInfo(FenbeitongApproveDto fenbeitongApproveDto, List<OpenEtlMappingConfig> mappingConfigList, String fbtApplyId) throws Exception;
}
