package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.impl;

import com.fenbeitong.openapi.plugin.customize.common.service.impl.DefaultOrgListener;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdEmployeeVo;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdOrgUnitVo;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto.DataDto;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: TestListener</p>
 * <p>Description: 宁波伟立组织架构监听实现类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-30 16:31
 */
@ServiceAspect
@Service
public class NingBoWeiLiOrgListener extends DefaultOrgListener {


    @Autowired
    IEtlService etlService;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    CommonServiceImpl commonService;

    @Override
    public List<OpenThirdOrgUnitDTO> filterOpenThirdOrgUnitDtoBefore(List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList, String companyId, String topId, String companyName) {
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = openThirdOrgUnitDTOList.stream().filter(t -> t.getThirdOrgUnitParentId().equals(topId)).findFirst().get();
        openThirdOrgUnitDTO.setThirdOrgUnitParentId(companyId);
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitName(companyName);
        return openThirdOrgUnitDTOList;
    }


    @Override
    public List<OpenThirdOrgUnitDTO> filterOpenThirdOrgUnitDtoAfter(List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList) {
        openThirdOrgUnitDTOList.remove(0);
        return openThirdOrgUnitDTOList;
    }

    @Override
    public OpenThirdEmployeeVo getEmployeeMaping(Long etlConfigId, String respData) {
        DataDto departmentDto = JsonUtils.toObj(respData, DataDto.class);
        List<Map<String, Object>> data = JsonUtils.toObj(JsonUtils.toJson(departmentDto.getResult().getRecords()), List.class);
        List<Map> mapList = etlService.transform(etlConfigId, data);
        OpenThirdEmployeeVo openThirdEmployeeVo = new OpenThirdEmployeeVo();
        List<OpenThirdEmployeeDTO> openThirdEmployeeDTOS = new ArrayList<>();

        try {
            mapList.forEach(t -> {
                OpenThirdEmployeeDTO openThirdEmployeeDTO = JsonUtils.toObj(JsonUtils.toJson(t), OpenThirdEmployeeDTO.class);
                openThirdEmployeeDTOS.add(openThirdEmployeeDTO);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        openThirdEmployeeVo.setOpenThirdEmployeeDTOS(openThirdEmployeeDTOS);
        return openThirdEmployeeVo;
    }

    @Override
    public OpenThirdOrgUnitVo getOrgMaping(Long etlConfigId, String respData) {
        DataDto departmentDto = JsonUtils.toObj(respData, DataDto.class);
        List<Map<String, Object>> data = JsonUtils.toObj(JsonUtils.toJson(departmentDto.getResult().getRecords()), List.class);
        List<Map> mapList = etlService.transform(etlConfigId, data);
        OpenThirdOrgUnitVo openThirdOrgUnitVo = new OpenThirdOrgUnitVo();
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOS = new ArrayList<>();
        try {
            mapList.forEach(t -> {
                OpenThirdOrgUnitDTO openThirdOrgUnitDTO = JsonUtils.toObj(JsonUtils.toJson(t), OpenThirdOrgUnitDTO.class);
                openThirdOrgUnitDTOS.add(openThirdOrgUnitDTO);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        openThirdOrgUnitVo.setOpenThirdOrgUnitDTOS(openThirdOrgUnitDTOS);
        return openThirdOrgUnitVo;
    }


    /**
     * 获取token
     */
    @Override
    public void setHead(Map<String, String> map, String companyId) {
        String token = commonService.getToken(companyId);
        map.put("Authorization", "Bearer " + token);

    }


}
