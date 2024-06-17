package com.fenbeitong.openapi.plugin.beisen.standard.service.impl;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenEmployeeListDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenOrgListDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.standard.listener.DefaultOrgListener;
import com.fenbeitong.openapi.plugin.beisen.standard.listener.OrgListener;
import com.fenbeitong.openapi.plugin.beisen.standard.service.BeisenPullDataServiceV2;
import com.fenbeitong.openapi.plugin.beisen.standard.service.third.BeisenApiServiceV2;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCustonmConstant;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenCustomizeConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenCustomizeConfig;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.core.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 组织架构同步
 * @Author duhui
 * @Date 2022/3/2
 **/
@ServiceAspect
@Service
@Slf4j
public class BeisenPullDataServiceImplV2 implements BeisenPullDataServiceV2 {
    @Autowired
    private BeisenApiServiceV2 beisenApiServiceV2;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    OpenCustomizeConfigDao openCustomizeConfigDao;
    @Autowired
    DepartmentUtilService departmentUtilService;
    @Autowired
    OpenSysConfigDao openSysConfigDao;
    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;


    @Override
    @Async
    public void pullAllDataV2(BeisenParamConfig beisenParamConfig) {
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(beisenParamConfig.getCompanyId(), EtlScriptType.EMPLOYEE_SYNC, 2);
        OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getCommonScriptConfig(beisenParamConfig.getCompanyId(), EtlScriptType.DEPARTMENT_SYNC, 2);
        // 查询全量的部门信息
        List<BeisenOrgListDTO.OrgDto> orgListData = beisenApiServiceV2.getOrgListData(beisenParamConfig);
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        // 转换部门
        if (!ObjectUtils.isEmpty(departmentConfig)) {
            departmentList = EtlUtils.etlFilter(departmentConfig, new HashMap<String, Object>() {{
                put("beisenParamConfig", beisenParamConfig);
                put("orgListData", orgListData);
            }});
        } else {
            departmentList = buildOpenThirdOrgUnitList(beisenParamConfig.getCompanyId(), orgListData);
        }
        // 部门排序
        departmentList = departmentUtilService.deparmentSort(departmentList, beisenParamConfig.getParentId(), beisenParamConfig.getCompanyName());
        //查询全量的员工信息
        List<BeisenEmployeeListDTO.EmployeeDto> beisenEmployeeList = beisenApiServiceV2.getEmployeeListData(beisenParamConfig);
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(employeeConfig)) {
            employeeList = EtlUtils.etlFilter(employeeConfig, new HashMap<String, Object>() {{
                put("beisenParamConfig", beisenParamConfig);
                put("employeeList", beisenEmployeeList);
            }});
        } else {
            employeeList = pacakgeEmployee(beisenParamConfig.getCompanyId(), beisenEmployeeList);
        }
        //人员数据后置监听处理
        OpenCustomizeConfig openCustomizeConfig = openCustomizeConfigDao.getOpenCustomizeConfig(beisenParamConfig.getCompanyId(), OpenCustonmConstant.open_customize_config_type.EMP_ALL);
        if (!ObjectUtils.isEmpty(openCustomizeConfig)) {
            OrgListener orgListener = getProjectListener(openCustomizeConfig);
            orgListener.filterOpenThirdOrgUnitDtoBefore(employeeList, beisenParamConfig.getCompanyId(), beisenParamConfig);
        }
        //同步
        openSyncThirdOrgService.syncThird(OpenType.BEISEN.getType(), beisenParamConfig.getCompanyId(), departmentList, employeeList);
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(beisenParamConfig.getCompanyId());
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            openSyncThirdOrgService.setAllDepManagePackV2(departmentList, beisenParamConfig.getCompanyId());
        }
    }


    //转换部门数据
    private List<OpenThirdOrgUnitDTO> buildOpenThirdOrgUnitList(String companyId, List<BeisenOrgListDTO.OrgDto> orgDtoList) {
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        for (BeisenOrgListDTO.OrgDto orgDto : orgDtoList) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            openThirdOrgUnitDTO.setCompanyId(companyId);
            openThirdOrgUnitDTO.setThirdOrgUnitFullName(orgDto.getName());
            openThirdOrgUnitDTO.setThirdOrgUnitName(orgDto.getShortName());
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(orgDto.getPoIdOrgAdmin());
            openThirdOrgUnitDTO.setThirdOrgUnitId(orgDto.getOId());
            openThirdOrgUnitDTO.setOrgUnitMasterIds(orgDto.getPersonInCharge());
            departmentList.add(openThirdOrgUnitDTO);
        }
        return departmentList;
    }

    private List<OpenThirdEmployeeDTO> pacakgeEmployee(String companyId, List<BeisenEmployeeListDTO.EmployeeDto> beisenEmployeeList) {
        List<OpenThirdEmployeeDTO> openThirdEmployeeDTOList = new ArrayList<>();
        beisenEmployeeList.stream().forEach(employeeDto -> {
            List<BeisenEmployeeListDTO.EmployeeServiceInfos> serviceInfos = employeeDto.getServiceInfos();
            if (serviceInfos.size() > 1) {
                serviceInfos = serviceInfos.stream().sorted((t1, t2) -> Long.compare(DateUtils.toDate(t2.getCreateTime(), DateUtils.FORMAT_DATE_PATTERN_T).getTime(), DateUtils.toDate(t1.getCreateTime(), DateUtils.FORMAT_DATE_PATTERN_T).getTime())).collect(Collectors.toList());
            }
            openThirdEmployeeDTOList.add(OpenThirdEmployeeDTO.builder()
                    .companyId(companyId)
                    .thirdEmployeeId(employeeDto.getBasicInfos().getUserId())
                    .thirdDepartmentId(serviceInfos.get(0).getDepartmentId())
                    .thirdEmployeeName(employeeDto.getBasicInfos().getName())
                    .thirdEmployeePhone(employeeDto.getBasicInfos().getMobilePhone())
                    .thirdEmployeeEmail(employeeDto.getBasicInfos().getEmail())
                    .thirdEmployeeGender(employeeDto.getBasicInfos().getGender() == null ? 2 : (employeeDto.getBasicInfos().getGender() == 0 ? 1 : 2))
                    .thirdEmployeeIdCard(employeeDto.getBasicInfos().getIdNumber())
                    .build());
        });
        return openThirdEmployeeDTOList;
    }

    /**
     * 反射获取监听类
     */
    public OrgListener getProjectListener(OpenCustomizeConfig openOrgConfig) {
        String className = openOrgConfig.getListenerClass();
        if (!ObjectUtils.isEmpty(className)) {
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz != null) {
                Object bean = SpringUtils.getBean(clazz);
                if (bean != null && bean instanceof OrgListener) {
                    return ((OrgListener) bean);
                }
            }
        }
        return SpringUtils.getBean(DefaultOrgListener.class);
    }

}
