package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuDepartmentSimpleListRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuEmployeeService;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuOrganizationService;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.isv.util.FeiShuIsvHttpUtils;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * 飞书部门service
 *
 * @author lizhen
 * @date 2020/6/2
        */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvOrganizationService extends AbstractFeiShuOrganizationService {

    @Autowired
    private FeiShuIsvHttpUtils feiShuIsvHttpUtils;

    @Autowired
    private FeiShuIsvEmployeeService feiShuIsvEmployeeService;

    @Override
    protected AbstractFeiShuHttpUtils getFeiShuHttpUtils() {
        return feiShuIsvHttpUtils;
    }

    @Override
    protected AbstractFeiShuEmployeeService getFeiShuEmployeeService() {
        return feiShuIsvEmployeeService;
    }

    @Override
    protected int getOpenType(){
        return OpenType.FEISHU_ISV.getType();
    }

    /**
     * 部门同步前按需过滤
     *
     * @param departmentConfig
     * @param departmentInfo
     * @param openThirdOrgUnitDTO
     * @return
     */
    public OpenThirdOrgUnitDTO departmentBeforeSyncFilter(OpenThirdScriptConfig departmentConfig, FeiShuDepartmentSimpleListRespDTO.DepartmentInfo departmentInfo, OpenThirdOrgUnitDTO openThirdOrgUnitDTO) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("departmentInfo", departmentInfo);
            put("openThirdOrgUnitDTO", openThirdOrgUnitDTO);
        }};
        if (StringUtils.isNotBlank(departmentConfig.getParamJson()) && JsonUtils.toObj(departmentConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(departmentConfig.getParamJson(), Map.class));
        }
        return (OpenThirdOrgUnitDTO) EtlUtils.execute(departmentConfig.getScript(), params);
    }

}
