package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdatamedium;

import com.dingtalk.api.response.OapiUserGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkCallbackTagConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataMediumType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvEmployeeService;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.privilege.service.OpenEmployeePrivService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lizhen
 * @date 2020/7/18
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkCloudUserHandler implements IOpenSyncBizDataMediumTaskHandler {

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private IDingtalkIsvEmployeeService dingtalkIsvEmployeeService;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    private OpenEmployeePrivService openEmployeePrivService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    private OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;


    @Override
    public OpenSyncBizDataMediumType getTaskType() {
        return OpenSyncBizDataMediumType.DINGTALK_ISV_USER;
    }

    @Override
    public TaskResult execute(OpenSyncBizDataMedium task) {
        String corpId = task.getCorpId();
        String bizData = task.getBizData();
        String userId = task.getBizId();
        //检查企业是否注册
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        String companyId = dingtalkIsvCompany.getCompanyId();
        Map<String, Object> dataMap = JsonUtils.toObj(bizData, Map.class);
        String syncAction = StringUtils.obj2str(dataMap.get("syncAction"));
        Long agentid = dingtalkIsvCompany.getAgentid();
        if (DingtalkCallbackTagConstant.USER_LEAVE_ORG.equals(syncAction)) {
            userLeave(userId, companyId);
        } else {
            return addOrUpdateUser(userId, corpId, companyId,agentid);
        }
        return TaskResult.SUCCESS;
    }

    /**
     * 删除
     *
     * @param userId
     * @param companyId
     */
    private void userLeave(String userId, String companyId) {
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdEmployeeId(userId);
        employeeList.add(openThirdEmployeeDTO);
        openSyncThirdOrgService.deleteEmployee(OpenType.DINGTALK_ISV.getType(), companyId, employeeList);
        // 删除部门中间表
        openThirdOrgUnitManagersDao.deleteByThirdEmployeeId(companyId, userId);
    }

    /**
     * 添加或删除人员
     *
     * @param userId
     * @param corpId
     * @param companyId
     * @return
     */
    private TaskResult addOrUpdateUser(String userId, String corpId, String companyId, Long agentid) {
        OapiUserGetResponse userInfo = dingtalkIsvEmployeeService.getUserInfo(userId, corpId);
        if (!userInfo.isSuccess()) {
            //人员不存在，任务过期
            return TaskResult.EXPIRED;
        }
        //获取已存在的部门id，只授权人未授权部门的人，放根部门下
        List<OpenThirdOrgUnit> openThirdOrgUnits = openThirdOrgUnitDao.listOrgUnitByCompanyIdAndOpenType(OpenType.DINGTALK_ISV.getType(), companyId);
        List<String> departmentIds = openThirdOrgUnits.stream().map(OpenThirdOrgUnit::getThirdOrgUnitId).collect(Collectors.toList());
        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getUserid());
        openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
        openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
        openThirdEmployeeDTO.setThirdEmployeePhone(userInfo.getMobile());
        List<Long> userDepartmentIds = userInfo.getDepartment();
        Iterator<Long> iterator = userDepartmentIds.iterator();
        while (iterator.hasNext()) {
            Long item = iterator.next();
            if (!departmentIds.contains(StringUtils.obj2str(item))) {
                iterator.remove();
            }
        }
        //部门为空或为1，为根部门用户
        if (ObjectUtils.isEmpty(userDepartmentIds) || 1L == userDepartmentIds.get(0)) {
            openThirdEmployeeDTO.setThirdDepartmentId(corpId);
        } else {
            openThirdEmployeeDTO.setThirdDepartmentId(StringUtils.obj2str(userDepartmentIds.get(0)));
        }
        String initRoleTypeFlag = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE_FLAG.getCode());
        if ("true".equals(initRoleTypeFlag)) {
            openEmployeePrivService.initRoleType("1", OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE.getCode(), companyId);
        }
        String extattr = userInfo.getExtattr();
        Map employeeCustomizeAttributeMap = openSysConfigService.getEmployeeConfigByIdAndType(companyId, OpenSysConfigType.EMPLOYEE_CUSTOMIZE_ATTRIBUTE.getType());
        //自定义字段
        dingtalkIsvEmployeeService.parseEmployeeCustomeAttr(employeeCustomizeAttributeMap , extattr , openThirdEmployeeDTO);
        // 先查一下配置表 看是否需要过滤
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
        OpenThirdEmployeeDTO targetDTO = null;
        if (employeeConfig != null) {
            //给员工设置花名册信息
            OpenMsgSetup openMsgSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, ItemCodeEnum.DINGTALK_ROSTER_CONFIG.getCode());
            Map<String, Map<String, String>> userIdFieldMap = new HashMap<>();
            if (openMsgSetup != null && !StringUtils.isTrimBlank(openMsgSetup.getStrVal1())) {
                userIdFieldMap = dingtalkIsvEmployeeService.batchGetSmartHrmEmployee(
                    corpId,
                    agentid,
                    Collections.singletonList(userId),
                    Arrays.asList(openMsgSetup.getStrVal1().split(",")));
            }
            targetDTO = dingtalkIsvEmployeeService.employeeBeforeSyncFilter(employeeConfig, userInfo, openThirdEmployeeDTO, userIdFieldMap.get(userId));
        }
        employeeList.add(targetDTO != null ? targetDTO : openThirdEmployeeDTO);
        //查询分贝通员工信息
        List<OpenThirdEmployee> srcEmployeeList = openThirdEmployeeDao.listEmployeeByThirdEmployeeId(OpenType.DINGTALK_ISV.getType(), companyId, Lists.newArrayList(userId));
        //如果员工已存在，则更新
        if (srcEmployeeList != null && srcEmployeeList.size() > 0) {
            openSyncThirdOrgService.updateEmployee(OpenType.DINGTALK_ISV.getType(), companyId, employeeList);
            // 删除部门中间表
            openThirdOrgUnitManagersDao.deleteDepManagers(srcEmployeeList.get(0).getThirdDepartmentId(), openThirdEmployeeDTO, companyId);
        } else {
            openSyncThirdOrgService.addEmployee(OpenType.DINGTALK_ISV.getType(), companyId, employeeList);
        }
        return TaskResult.SUCCESS;

    }
}
