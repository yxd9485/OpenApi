package com.fenbeitong.openapi.plugin.dingtalk.eia.service.handler;

import com.dingtalk.api.response.OapiUserGetResponse;
import com.fenbeitong.finhub.common.utils.FinhubLogger;
import com.fenbeitong.finhub.common.validation.validators.UserValidator;
import com.fenbeitong.finhub.common.validation.validators.Validation;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingTalkNoticeService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.ApiUserServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.EmployeeDTOBuilderServiceImpl;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.employee.service.ThirdEmployeePostProcessService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.enums.EmployeeDefineEnum;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.organization.service.SupportFunDepartmentService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import com.luastar.swift.base.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkUserAddOrUpdateHandler implements ITaskHandler {


    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    private ApiUserServiceImpl apiUserService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    SupportFunDepartmentService funDepartmentService;

    @Autowired
    private IDingTalkNoticeService dingTalkNoticeService;

    @Autowired
    private ThirdEmployeePostProcessService postProcessService;

    @Override
    public TaskType getTaskType() {
        return TaskType.DINGTALK_EIA_CREATE_OR_UPDATE_USER;
    }

    @Autowired
    private OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    private EmployeeDTOBuilderServiceImpl employeeDTOBuilderServiceImpl;

    @Override
    public TaskResult execute(Task task) {
        String dataId = task.getDataId();
        String corpId = task.getCorpId();
        String companyId = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId).getAppId();
        OapiUserGetResponse userGetResponse = apiUserService.getUserWithOriginal(corpId, task.getDataId());
        if (!userGetResponse.isSuccess()) {
            log.info("人员已经被删除,corpId={},userId={}", corpId, dataId);
            return TaskResult.ABORT;
        }
        DingtalkUser userInfo = JsonUtils.toObj(userGetResponse.getBody(), DingtalkUser.class);
        Map map = openSysConfigService.getEmployeeDefinedConfig(companyId);
        // 判断配置是否为空，不为空时走配置
        String roleType = userInfo.getFbtRoleType();
        String mainDepartment = userInfo.getMainDepartment();
        String userPhoneNum = getUserPhoneNum(StringUtils.obj2str(userInfo.getFbtMobile(), ""));
        String idCard = userInfo.getIdCard();
        // 查询是否需要修改用户
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
        if (!ObjectUtils.isEmpty(map) && !ObjectUtils.isEmpty(userInfo.getExtattr())) {
            roleType = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEROLETYE.getValue())) == null ? roleType : StringUtils.obj2str(userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEROLETYE.getValue())));
            mainDepartment = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.MAINDEPARTMENT.getValue())) == null ? mainDepartment : userInfo.getExtattr().get(map.get(EmployeeDefineEnum.MAINDEPARTMENT.getValue())).toString();
            userPhoneNum = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEPHONE.getValue())) == null ? userPhoneNum : userInfo.getExtattr().get(map.get(EmployeeDefineEnum.THIRDEMPLOYEEPHONE.getValue())).toString();
            idCard = userInfo.getExtattr().get(map.get(EmployeeDefineEnum.ID_CARD.getValue())) == null ? idCard : userInfo.getExtattr().get(map.get(EmployeeDefineEnum.ID_CARD.getValue())).toString();
        }
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();

        // 获取员工自定义的配置文件
        Map employeeCustomizeAttributeMap = openSysConfigService.getEmployeeConfigByIdAndType(companyId, OpenSysConfigType.EMPLOYEE_CUSTOMIZE_ATTRIBUTE.getType());
        if (!MapUtils.isBlank(employeeCustomizeAttributeMap)) {
            Map<String, Object> mapResult = new HashMap<>();
            Field[] fields = userInfo.getClass().getDeclaredFields();
            if (!ObjectUtils.isEmpty(fields)) {
                for (Field f : fields) {
                    f.setAccessible(true);
                    employeeCustomizeAttributeMap.forEach((entryKey, entryValue) -> {
                        if (entryKey.equals(f.getName())) {
                            try {
                                mapResult.put(entryValue.toString(), f.get(userInfo));
                            } catch (Exception e) {
                                e.printStackTrace();
                                FinhubLogger.error("同步钉钉时反射属性出错,corpId={},userId={}", corpId, dataId);
                            }
                        }
                    });
                }
            }
            if (userInfo.getExtattr() != null) {
                for (String key : userInfo.getExtattr().keySet()) {
                    employeeCustomizeAttributeMap.forEach((entryKey, entryValue) -> {
                        if (entryKey.equals(key)) {
                            mapResult.put(entryValue.toString(), userInfo.getExtattr().get(key));
                        }
                    });
                }
            }
            if (!MapUtils.isBlank(mapResult)) {
                openThirdEmployeeDTO.setExtAttr(mapResult);
            }
        }

        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();

        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getUserid());
        openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
        openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
        // 如果手机号自定义字段为空,则取钉钉的默认手机号
        if (!"".equals(userPhoneNum) && userPhoneNum != null) {
            userPhoneNum = org.apache.commons.lang3.StringUtils.normalizeSpace(userPhoneNum);
            userPhoneNum = userPhoneNum.substring(userPhoneNum.length() > 11 ? userPhoneNum.length() - 11 : 0);
            openThirdEmployeeDTO.setThirdEmployeePhone(userPhoneNum);
            //验证分贝通手机号是否填写，如果没有填写，会发送钉钉消息到指定消息接收人，告知其完善手机号
            Validation validation = UserValidator.validateMobile(userPhoneNum);
            if (!validation.isSuccess()) {
                String message = StrUtils.formatString("您刚刚添加的员工[{0}], 分贝通手机号未设置成功, \n原因： {1} " +
                                "\n未设置分贝通手机号的员工暂时无法使用分贝通应用, 请及时为其设定", userInfo.getName(),
                        validation.getErrorMsg());
                dingTalkNoticeService.sendMsgToCustomerAdmin(corpId, message, dataId);
            }
        } else {
            openThirdEmployeeDTO.setThirdEmployeePhone(userInfo.getMobile());
        }

        //查询分贝通员工信息
        List<OpenThirdEmployee> srcEmployeeList = openThirdEmployeeDao.listEmployeeByThirdEmployeeId(OpenType.DINGTALK_EIA.getType(), companyId, Lists.newArrayList(openThirdEmployeeDTO.getThirdEmployeeId()));
        String departmentId = "";
        // 判断是否改变部门
        if (srcEmployeeList != null && srcEmployeeList.size() > 0) {
            String thirdDepartMentId = srcEmployeeList.get(0).getThirdDepartmentId();
            if (thirdDepartMentId.equals(corpId)) {
                thirdDepartMentId = "1";
            }
            if (StringUtils.isBlank(mainDepartment)) {
                if (userInfo.getDepartment().contains(Integer.parseInt(thirdDepartMentId))) {
                    departmentId = thirdDepartMentId;
                } else {
                    departmentId = String.valueOf(userInfo.getDepartment().get(0));
                }
            } else {
                departmentId = mainDepartment;
            }
        } else {
            if (StringUtils.isBlank(mainDepartment)) {
                departmentId = String.valueOf(userInfo.getDepartment().get(0));
            } else {
                departmentId = mainDepartment;
            }
        }

        if ("1".equals(departmentId)) {
            openThirdEmployeeDTO.setThirdDepartmentId(corpId);
        } else {
            openThirdEmployeeDTO.setThirdDepartmentId(departmentId);
        }
        if (!ObjectUtils.isEmpty(roleType)) {
            openThirdEmployeeDTO.setThirdEmployeeRoleTye(roleType);
        }
        openThirdEmployeeDTO.setThirdEmployeeIdCard(idCard);
        List<String> userIds = Arrays.asList(userInfo.getUserid());
        // 员工ID和花名册信息映射
        Map<String,Map<String,String>> userIdToRouterInfoMap = employeeDTOBuilderServiceImpl.getRouterInfo(companyId,corpId, userIds);
        userInfo.setRouterInfo(userIdToRouterInfoMap.get(userInfo.getUserid()));
        openThirdEmployeeDTO = postProcessService.process(openThirdEmployeeDTO,userInfo,companyId,employeeConfig);
        if ( null != openThirdEmployeeDTO ){
            employeeList.add(openThirdEmployeeDTO);
        }
        //如果员工已存在，则更新
        if (srcEmployeeList != null && srcEmployeeList.size() > 0) {
            openSyncThirdOrgService.updateEmployee(OpenType.DINGTALK_EIA.getType(), companyId, employeeList);
            // 删除部门中间表
            openThirdOrgUnitManagersDao.deleteDepManagers(srcEmployeeList.get(0).getThirdDepartmentId(), openThirdEmployeeDTO, companyId);
        } else {
            openSyncThirdOrgService.addEmployee(OpenType.DINGTALK_EIA.getType(), companyId, employeeList);
        }
        return TaskResult.SUCCESS;
    }

    private String getUserPhoneNum(String fbtMobile) {
        String[] arr = fbtMobile.split("-");
        if (arr.length > 1) {
            return arr[1];
        }
        return arr[0];
    }

}
