package com.fenbeitong.openapi.plugin.customize.sync.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.customize.common.service.impl.DefaultOrgListener;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdEmployeeVo;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdOrgUnitVo;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author duhui
 * @Date 2021/8/5
 **/
@ServiceAspect
@Service
public class PicoOrgListener extends DefaultOrgListener {

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
        OpenThirdEmployeeVo openThirdEmployeeVo = new OpenThirdEmployeeVo();
        List<OpenThirdEmployeeDTO> openThirdEmployeeDTOS = new ArrayList<>();
        Map<String, Object> map = JsonUtils.toObj(respData, Map.class);
        if (!ObjectUtils.isEmpty(map) && "200".equals(map.get("code").toString())) {
            List<Map<String, Object>> empList = JsonUtils.toObj(JsonUtils.toJson(map.get("result")), new TypeReference<List<Map<String, Object>>>() {
            });
            empList.forEach(employee -> {
                if (!"123@123.com".equals(StringUtils.obj2str(employee.get("Email")))) {
                    OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                    openThirdEmployeeDTO.setThirdEmployeeId(StringUtils.obj2str(employee.get("UserID")));
                    openThirdEmployeeDTO.setThirdEmployeeName(StringUtils.obj2str(employee.get("StaffName")));
                    openThirdEmployeeDTO.setThirdEmployeeEmail(StringUtils.obj2str(employee.get("Email")));
                    openThirdEmployeeDTO.setThirdEmployeePhone(StringUtils.obj2str(employee.get("Mobile")));
                    openThirdEmployeeDTO.setThirdEmployeeRoleTye(StringUtils.obj2str(employee.get("FBTID")));
                    openThirdEmployeeDTO.setThirdEmployeeIdCard(StringUtils.obj2str(employee.get("IDNumber")));
                    List<Map<String, Object>> DeptList = JsonUtils.toObj(JsonUtils.toJson(employee.get("Dept")), new TypeReference<List<Map<String, Object>>>() {
                    });
                    openThirdEmployeeDTO.setThirdDepartmentId(StringUtils.obj2str(DeptList.get(0).get("DeptID")));
                    openThirdEmployeeDTOS.add(openThirdEmployeeDTO);
                }
            });
        }

        openThirdEmployeeVo.setOpenThirdEmployeeDTOS(openThirdEmployeeDTOS);
        return openThirdEmployeeVo;
    }

    @Override
    public OpenThirdOrgUnitVo getOrgMaping(Long etlConfigId, String respData) {
        OpenThirdOrgUnitVo openThirdOrgUnitVo = new OpenThirdOrgUnitVo();
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOS = new ArrayList<>();
        Map<String, Object> map = JsonUtils.toObj(respData, Map.class);
        if (!ObjectUtils.isEmpty(map) && "200".equals(map.get("code").toString())) {
            Map<String, Object> childMap = MapUtils.request2map2(map.get("result"));
            if (!ObjectUtils.isEmpty(childMap) && !ObjectUtils.isEmpty(childMap.get("children"))) {
                List<Map<String, Object>> childMap1 = JsonUtils.toObj(JsonUtils.toJson(childMap.get("children")), new TypeReference<List<Map<String, Object>>>() {
                });
                recursionGetData(childMap1, openThirdOrgUnitDTOS, StringUtils.obj2str(childMap.get("DeptID")));
            }
        }
        openThirdOrgUnitVo.setOpenThirdOrgUnitDTOS(openThirdOrgUnitDTOS);
        return openThirdOrgUnitVo;
    }

    /**
     * 递归获取数据
     */
    private void recursionGetData(List<Map<String, Object>> children, List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOS, String parentId) {
        if (!ObjectUtils.isEmpty(children) && !StringUtils.isBlank(parentId)) {
            children.forEach(t -> {
                OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
                openThirdOrgUnitDTO.setThirdOrgUnitId(StringUtils.obj2str(t.get("DeptID")));
                openThirdOrgUnitDTO.setThirdOrgUnitName(StringUtils.obj2str(t.get("DeptName")));
                openThirdOrgUnitDTO.setThirdOrgUnitParentId(parentId);
                openThirdOrgUnitDTOS.add(openThirdOrgUnitDTO);
                if (!ObjectUtils.isEmpty(t.get("children"))) {
                    List<Map<String, Object>> childMap = JsonUtils.toObj(JsonUtils.toJson(t.get("children")), new TypeReference<List<Map<String, Object>>>() {
                    });
                    recursionGetData(childMap, openThirdOrgUnitDTOS, StringUtils.obj2str(t.get("DeptID")));
                }
            });
        }
    }
}
