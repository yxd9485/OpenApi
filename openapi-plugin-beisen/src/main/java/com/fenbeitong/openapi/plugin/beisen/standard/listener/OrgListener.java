package com.fenbeitong.openapi.plugin.beisen.standard.listener;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;

import java.util.List;

/**
 * @Description 组织架构同步监听类接口
 * @Author duhui
 * @Date 2020-12-01
 **/
public interface OrgListener {

    /**
     * 人员数据监听后置处理
     *
     * @param openThirdOrgUnitDTOList 过滤的数据
     * @return List<OpenThirdOrgUnitDTO>
     */
    List<OpenThirdEmployeeDTO> filterOpenThirdOrgUnitDtoBefore(List<OpenThirdEmployeeDTO> openThirdOrgUnitDTOList, String companyId, BeisenParamConfig beisenParamConfig, Object... objects);






}
