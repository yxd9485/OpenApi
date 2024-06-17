package com.fenbeitong.openapi.plugin.customize.common.service.impl;

import com.fenbeitong.openapi.plugin.customize.common.service.OrgListener;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdEmployeeVo;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdOrgUnitVo;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description 可配置化的组织架构监听
 * @Author duhui
 * @Date 2020-11-26
 **/

@Primary
public class DefaultOrgListener implements OrgListener {

    @Autowired
    IEtlService etlService;

    @Autowired
    OpenThirdOrgUnitDao openThirdOrgUnitDao;


    /**
     * 部门数据过滤前置处理
     */
    @Override
    public List<OpenThirdOrgUnitDTO> filterOpenThirdOrgUnitDtoBefore(List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList, String companyId, String topId, String companyName) {
        return openThirdOrgUnitDTOList;
    }

    /**
     * 部门数据过滤后置处理
     */
    @Override
    public List<OpenThirdOrgUnitDTO> filterOpenThirdOrgUnitDtoAfter(List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList) {
        return openThirdOrgUnitDTOList;
    }


    /**
     * 人员数据过滤
     */
    @Override
    public List<OpenThirdEmployeeDTO> fileOpenThirdEmployeeDto(List<OpenThirdEmployeeDTO> openThirdEmployeeDTOList) {
        return openThirdEmployeeDTOList;
    }

    @Override
    public OpenThirdEmployeeVo getEmployeeMaping(Long etlConfigId, String respData) {
        Map<String, String> map = etlService.transform(etlConfigId, JsonUtils.toObj(respData, Map.class));
        OpenThirdEmployeeVo openThirdEmployeeVo = new OpenThirdEmployeeVo();
        try {
            openThirdEmployeeVo = JsonUtils.toObj(JsonUtils.toJson(map), OpenThirdEmployeeVo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return openThirdEmployeeVo;
    }

    @Override
    public OpenThirdOrgUnitVo getOrgMaping(Long etlConfigId, String respData) {
        Map<String, Object> map = etlService.transform(etlConfigId, JsonUtils.toObj(respData, Map.class));
        OpenThirdOrgUnitVo openThirdOrgUnitVo = new OpenThirdOrgUnitVo();
        try {
            openThirdOrgUnitVo = JsonUtils.toObj(JsonUtils.toJson(map), OpenThirdOrgUnitVo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return openThirdOrgUnitVo;
    }

    @Override
    public void setDepManage(String companyId, int openType) {
    }

    @Override
    public void updateDepManage(List<OpenThirdOrgUnitDTO> departmentList, String companyId, int openType) {
    }


    @Override
    public void setHead(Map<String, String> map,String companyId) {

    }

    @Override
    public void setBody(Map<String, String> map,String companyId) {

    }


}
