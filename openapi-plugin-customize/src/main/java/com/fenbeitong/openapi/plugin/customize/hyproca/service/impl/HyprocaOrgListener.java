package com.fenbeitong.openapi.plugin.customize.hyproca.service.impl;

import com.fenbeitong.openapi.plugin.customize.common.service.impl.DefaultOrgListener;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdEmployeeVo;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdOrgUnitVo;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: TestListener</p>
 * <p>Description: 海普诺凯组织架构监听实现类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-30 16:31
 */
@ServiceAspect
@Service
public class HyprocaOrgListener extends DefaultOrgListener {

    @Autowired
    OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    IEtlService etlService;

    @Autowired
    OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Override
    public List<OpenThirdOrgUnitDTO> filterOpenThirdOrgUnitDtoBefore(List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList, String companyId, String topId, String companyName) {
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setThirdOrgUnitId(topId);
        openThirdOrgUnitDTO.setThirdOrgUnitParentId(companyId);
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitName(companyName);
        openThirdOrgUnitDTOList.add(openThirdOrgUnitDTO);
        return openThirdOrgUnitDTOList;
    }


    @Override
    public List<OpenThirdOrgUnitDTO> filterOpenThirdOrgUnitDtoAfter(List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList) {
        openThirdOrgUnitDTOList.remove(0);
        return openThirdOrgUnitDTOList;
    }


    @Override
    public OpenThirdEmployeeVo getEmployeeMaping(Long etlConfigId, String respData) {

        List<Map<String, Object>> data = JsonUtils.toObj(respData, List.class);
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
        List<Map<String, Object>> data = JsonUtils.toObj(respData, List.class);
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

    @Override
    public void setDepManage(String companyId, int openType) {
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            List<OpenThirdOrgUnit> openThirdOrgUnits = openThirdOrgUnitDao.listOrgUnitByMaster(openType, companyId);
            openSyncThirdOrgService.setDepManage(openThirdOrgUnits, companyId, openType);
        }
    }

    @Override
    public void updateDepManage(List<OpenThirdOrgUnitDTO> departmentList, String companyId, int openType) {
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            departmentList.forEach(t -> {
                OpenThirdOrgUnit openThirdOrgUnit = new OpenThirdOrgUnit();
                openThirdOrgUnit.setOrgUnitMasterIds(t.getOrgUnitMasterIds());
                Example example = new Example(OpenThirdOrgUnit.class);
                example.createCriteria()
                        .andEqualTo("companyId", companyId)
                        .andEqualTo("openType", openType)
                        .andEqualTo("thirdOrgUnitId", t.getThirdOrgUnitId())
                        .andEqualTo("status", 1);
                openThirdOrgUnitDao.updateByExample(openThirdOrgUnit, example);
            });
        }
    }

    @Override
    public void setBody(Map<String, String> map,String companyId) {
        map.put("date1", DateUtils.beforeHourToNowDate(36));
        map.put("date2", DateUtils.toSimpleStr(new Date()));
    }


}
