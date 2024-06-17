package com.fenbeitong.openapi.plugin.func.organization.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.func.organization.service.Func51TalkEmployeeAndDepartmentService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.dto.OrgDto;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dao.Talk51ProManageDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.SupperDepartmentDto;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.entity.Talk51ProManageConfig;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncEmployeeAndDepartmentServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-24 15:04
 */
@ServiceAspect
@Service
@Slf4j
public class Func51TalkEmployeeAndDepartmentServiceImpl implements Func51TalkEmployeeAndDepartmentService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    DepartmentUtilService departmentUtilService;

    @Autowired
    OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    Talk51ProManageDao talk51ProManageDao;

    @Value("${host.usercenter}")
    private String url;

    @Override
    public String EmployeeAndDepartmentSync(OrgDto orgDto, String companyId) {
        log.info("EmployeeAndDepartmentSync, 开始同步");
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
                String companyName = pluginCorpDefinition.getAppName();
                syncExecute(OpenType.OPEN_API.getType(), orgDto, companyName, companyId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("FuncEmployeeAndDepartmentServiceImpl, 未获取到锁，companyId={}", companyId);
        }
        return "success";

    }

    @Override
    public String updateMasterIds(String companyId) {
        // 默认设置二级部门主管
        List<OpenThirdOrgUnit> openThirdOrgUnits = openThirdOrgUnitDao.listOrgUnitByTopLevel(OpenType.OPEN_API.getType(), companyId);
        List<Talk51ProManageConfig> defaultManageConfits = new ArrayList<>();
        openThirdOrgUnits.forEach(t -> {
            Talk51ProManageConfig talk51ProManageConfig = new Talk51ProManageConfig();
            talk51ProManageConfig.setManageLevel(2);
            talk51ProManageConfig.setTopId(t.getThirdTopId());
            defaultManageConfits.add(talk51ProManageConfig);
        });
        setDepManage(defaultManageConfits, companyId, true);

        // 0无效 1有效
        List<Talk51ProManageConfig> talk51ProManageConfigsStop = talk51ProManageDao.selectByStatus(0);
        if (talk51ProManageConfigsStop != null && talk51ProManageConfigsStop.size() > 0) {
            setDepManage(talk51ProManageConfigsStop, companyId, false);
        }
        List<Talk51ProManageConfig> talk51ProManageConfigs = talk51ProManageDao.selectByStatus(1);
        if (talk51ProManageConfigs != null && talk51ProManageConfigs.size() > 0) {
            setDepManage(talk51ProManageConfigs, companyId, true);
        }
        return "success";
    }


    /**
     * 设置部门负责人执行体
     *
     * @param flag true:添加部门负责人 false:删除部分负责人
     */
    public void setDepManage(List<Talk51ProManageConfig> talk51ProManageConfigs, String companyId, boolean flag) {
        String operatorId = openEmployeeService.superAdmin(companyId);
        SupperDepartmentDto supperDepartmentDto = new SupperDepartmentDto();
        supperDepartmentDto.setCompany_id(companyId);
        supperDepartmentDto.setOperator_id(operatorId);
        supperDepartmentDto.setOperator_role(6);
        supperDepartmentDto.setSource("openApi");
        supperDepartmentDto.setType(1);
        supperDepartmentDto.setDelete_history(true);
        List<SupperDepartmentDto.RoleBean> roleBeanList = new ArrayList<>();
        talk51ProManageConfigs.forEach(info -> {
            //批量更新数据
            List<OpenThirdOrgUnit> openThirdOrgUnits = openThirdOrgUnitDao.listOrgUnitByLevel(OpenType.OPEN_API.getType(), companyId, info.getTopId(), info.getManageLevel());
            openThirdOrgUnits.forEach(t -> {
                if (t.getOrgUnitMasterIds() != null && !"".equals(t.getOrgUnitMasterIds())) {
                    SupperDepartmentDto.RoleBean roleBean = new SupperDepartmentDto.RoleBean();
                    String[] masterIds = t.getOrgUnitMasterIds().split(",");
                    List<String> masterList = Arrays.asList(masterIds).stream().map(id -> id.trim()).collect(Collectors.toList());
                    List<OpenThirdEmployee> openthirdMsters = openThirdEmployeeDao.listEmployeeByThirdEmployeeId(OpenType.OPEN_API.getType(), companyId, masterList);
                    masterList = openthirdMsters.stream().map(b -> b.getEmployeeId()).collect(Collectors.toList());
                    if (flag) {
                        roleBean.setUser_ids(masterList);
                    }
                    roleBean.setOrg_unit_id(t.getOrgUnitId());
                    roleBeanList.add(roleBean);
                }
            });
        });
        CollectionUtils.batch(roleBeanList, 50).forEach(roleBeans -> {
            supperDepartmentDto.setRole_ids(roleBeans);
            log.info("权限管理请求参数{}", JsonUtils.toJson(supperDepartmentDto));
            // 更新数据
            String str = RestHttpUtils.postJson(url + "/uc/inner/third/roles/employee/batch_save", JsonUtils.toJson(supperDepartmentDto));
            log.info("updateMasterIds 更新数据返回结果{}", str);
        });
    }


    /**
     * 部门同步执行体
     */
    private void syncExecute(int openType, OrgDto orgDto, String companyName, String companyId) {
        List<OpenThirdEmployeeDTO> employeeList = changeEmployee(orgDto, companyId);
        List<OpenThirdOrgUnitDTO> departmentList = changeDepartment(orgDto, companyId, companyName);

        // 同步部门
        openSyncThirdOrgService.syncThird(openType, companyId, departmentList, new ArrayList<>());

        // 更新中间表的数据
        updataDapartmentByMasterIds(departmentList, companyId, openType);

        // 同步人员
        openSyncThirdOrgService.syncThird(openType, companyId, new ArrayList<>(), employeeList);

    }


    /**
     * 修改部门负责人数据
     */
    public void updataDapartmentByMasterIds(List<OpenThirdOrgUnitDTO> departmentList, String companyId, Integer openType) {
        departmentList = DeparmentSetMasterIds(departmentList, "0");
        departmentList.forEach(t -> {
            OpenThirdOrgUnit openThirdOrgUnit = new OpenThirdOrgUnit();
            openThirdOrgUnit.setOrgUnitMasterIds(t.getOrgUnitMasterIds());
            openThirdOrgUnit.setOrgLevel(t.getOrgLevel());
            openThirdOrgUnit.setThirdTopId(t.getThirdTopId());
            Example example = new Example(OpenThirdOrgUnit.class);
            example.createCriteria()
                    .andEqualTo("companyId", companyId)
                    .andEqualTo("openType", openType)
                    .andEqualTo("thirdOrgUnitId", t.getThirdOrgUnitId())
                    .andEqualTo("status", 1);
            openThirdOrgUnitDao.updateByExample(openThirdOrgUnit, example);
        });
    }


    /**
     * 封装部门负责人
     */

    public List<OpenThirdOrgUnitDTO> DeparmentSetMasterIds(List<OpenThirdOrgUnitDTO> departmentList, String corpId) {
        Map<String, List<OpenThirdOrgUnitDTO>> groupBy = departmentList.stream().collect(Collectors.groupingBy(OpenThirdOrgUnitDTO::getThirdOrgUnitParentId));
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOS = new ArrayList<>();
        recursion(groupBy, openThirdOrgUnitDTOS, corpId, "");
        return openThirdOrgUnitDTOS;

    }

    /**
     * 递归层遍历
     */
    private void recursion(Map<String, List<OpenThirdOrgUnitDTO>> groupBy, List<OpenThirdOrgUnitDTO> departmentList, String corpId, String topId) {
        while (!groupBy.isEmpty()) {
            List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOS = groupBy.get(corpId);
            if (openThirdOrgUnitDTOS != null) {
                departmentList.addAll(openThirdOrgUnitDTOS);
                groupBy.remove(corpId);
                openThirdOrgUnitDTOS.forEach(t -> {
                    String finalTipId = "";
                    if (t.getOrgLevel() == 1 && ObjectUtils.isEmpty(topId)) {
                        finalTipId = t.getThirdOrgUnitId();
                    } else {
                        finalTipId = topId;
                    }
                    t.setThirdTopId(finalTipId);
                    recursion(groupBy, departmentList, t.getThirdOrgUnitId(), finalTipId);
                });
            } else {
                break;
            }

        }
    }


    /**
     * 人员转换
     */

    public List<OpenThirdEmployeeDTO> changeEmployee(OrgDto dataBean, String companyId) {
        // 转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();

        // 员工权限类型
        HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put("code", companyId);
        configMap.put("type", OpenSysConfigType.EMPLOYEE_ROLE_TYPE.getType());

        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfig(configMap);

        for (OrgDto.EmployeeList employee : dataBean.getEmployeeList()) {
            OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
            // 企业ID
            openThirdEmployeeDTO.setCompanyId(companyId);
            // 部门ID
            openThirdEmployeeDTO.setThirdDepartmentId(employee.getThirdOrgUnitId());
            // 姓名
            openThirdEmployeeDTO.setThirdEmployeeName(employee.getName());
            // 员工唯一编号
            openThirdEmployeeDTO.setThirdEmployeeId(employee.getThirdEmployeeId());
            // 邮箱
            if (!ObjectUtils.isEmpty(employee.getEmail())) {
                openThirdEmployeeDTO.setThirdEmployeeEmail(employee.getEmail());
            }
            // 身份证号
            if (employee.getCertList() != null && employee.getCertList().size() > 0) {
                // 映射成map
                Map<Integer, String> maps = employee.getCertList().stream().collect(Collectors.toMap(t -> t.getCertType(), t -> t.getCertNo(), (key1, key2) -> key2));
                if (maps.containsKey(1)) {
                    openThirdEmployeeDTO.setThirdEmployeeIdCard(maps.get(1));
                }

            }
            // 员工权限角色
            if (employee.getRole() != null) {
                openThirdEmployeeDTO.setThirdEmployeeRole(employee.getRole());
            }
            // 扩展字段
            openThirdEmployeeDTO.setExtAttr(JsonUtils.toObj(JsonUtils.toJson(employee.getExpandJson()), Map.class));

            if (ObjectUtils.isEmpty(openSysConfig)) {
                throw new FinhubException(3, "获取员工权限类型配置失败");
            } else {
                openThirdEmployeeDTO.setThirdEmployeeRoleTye(openSysConfig.getValue());
            }
            employeeList.add(openThirdEmployeeDTO);
        }
        return employeeList.stream().distinct().collect(Collectors.toList());

    }


    /**
     * 部门转换
     */
    public List<OpenThirdOrgUnitDTO> changeDepartment(OrgDto dataBean, String companyId, String companyName) {
        // 部门转
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        for (OrgDto.DepartmentInfoBean infoBean : dataBean.getDepartmentInfo()) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            openThirdOrgUnitDTO.setCompanyId(companyId);
            openThirdOrgUnitDTO.setThirdOrgUnitName(infoBean.getOrgUnitName());
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(infoBean.getThirdParentId());
            openThirdOrgUnitDTO.setThirdOrgUnitId(infoBean.getThirdOrgId());
            openThirdOrgUnitDTO.setOrgUnitMasterIds(infoBean.getOrgUnitMasterIds());
            openThirdOrgUnitDTO.setOrgLevel(Integer.parseInt(infoBean.getLevel()));

            if (dataBean.getRootDepartmentId().equals(infoBean.getThirdOrgId())) {
                openThirdOrgUnitDTO.setThirdOrgUnitParentId("0");
                openThirdOrgUnitDTO.setThirdOrgUnitName(companyName);
            }

            departmentList.add(openThirdOrgUnitDTO);
        }


        // 去重
        departmentList.stream().distinct().collect(Collectors.toList());

        log.info("DepartmentSync,排序前的数据 ,departmentList={}", JsonUtils.toJson(departmentList));
        // 部门排序 并去重
        departmentList = departmentUtilService.deparmentSort(departmentList, "0");

        log.info("DepartmentSync,排序后的数据 ,departmentList={}", JsonUtils.toJson(departmentList));
        return departmentList;
    }

}
