package com.fenbeitong.openapi.plugin.customize.common.service.impl;

import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: TestListener</p>
 * <p>Description: 测试代码</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-30 16:31
 */
@ServiceAspect
@Service
public class TestListener extends DefaultOrgListener {

    @Override
    public List<OpenThirdOrgUnitDTO> filterOpenThirdOrgUnitDtoAfter(List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList) {
        openThirdOrgUnitDTOList = openThirdOrgUnitDTOList.stream().filter(t -> ("false").equals(t.getIsStop())).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
        return openThirdOrgUnitDTOList;
    }

    @Override
    public List<OpenThirdEmployeeDTO> fileOpenThirdEmployeeDto(List<OpenThirdEmployeeDTO> openThirdEmployeeDTOList) {
        openThirdEmployeeDTOList = openThirdEmployeeDTOList.stream().filter(t -> ("false").equals(t.getIsStop())).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
        return openThirdEmployeeDTOList;
    }

}
