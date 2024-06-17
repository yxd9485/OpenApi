package com.fenbeitong.openapi.plugin.beisen.standard.listener;


import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenCustomizeConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author duhui
 * @Date 2021/8/5
 **/
@Service
@Primary
@Slf4j
public class DefaultOrgListener implements OrgListener {
    @Autowired
    OpenCustomizeConfigDao openCustomizeConfigDao;

    @Override
    public List<OpenThirdEmployeeDTO> filterOpenThirdOrgUnitDtoBefore(List<OpenThirdEmployeeDTO> openThirdOrgUnitDTOList, String companyId, BeisenParamConfig beisenParamConfig, Object... objects) {
        return openThirdOrgUnitDTOList;
    }


}
