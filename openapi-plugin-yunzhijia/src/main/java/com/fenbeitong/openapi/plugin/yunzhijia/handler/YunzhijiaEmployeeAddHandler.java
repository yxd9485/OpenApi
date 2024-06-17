package com.fenbeitong.openapi.plugin.yunzhijia.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.ThirdEmployeePostProcessService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdExpandFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdExpandFieldConfig;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaEmployeeContactDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaEmployeeDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class YunzhijiaEmployeeAddHandler extends YunzhijiaEmployeeHandler implements ITaskHandler {

    @Autowired
    private OpenThirdExpandFieldConfigDao expandFieldConfigDao;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private ThirdEmployeePostProcessService postProcessService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    public TaskType getTaskType() {
        return TaskType.YUNZHIJIA_USER_ADD_ORG;
    }

    @Override
    public TaskResult execute(Task task) {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        PluginCorpDefinition byCorpId = getPluginCorpDefinitionByCorpId(corpId);
        //4.调用云之家获取员工详情
        YunzhijiaResponse<List<YunzhijiaEmployeeDTO>> yunzhijiaEmployeeDetail = getYunzhijiaEmp(corpId, dataId);
        if (ObjectUtils.isEmpty(yunzhijiaEmployeeDetail) || ObjectUtils.isEmpty(yunzhijiaEmployeeDetail.getData())) {
            return TaskResult.EXPIRED;
        }
        //5.拼装分贝通请求参数
        //分贝通公司ID
        String companyId = byCorpId.getAppId();
        //分贝通授权负责人ID，用于operaterId设置
        YunzhijiaEmployeeDTO userInfo = yunzhijiaEmployeeDetail.getData().get(0);
        OpenThirdExpandFieldConfig expandFieldConfig = expandFieldConfigDao.getByCompanyId(companyId);
        String userExpandFields = expandFieldConfig == null ? null : expandFieldConfig.getUserExpandFields();
        //分贝通添加人员数据
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        // 查询是否需要修改用户
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
        if (userInfo.getStatus() == 1) {//正常状态
            openThirdEmployeeDTO.setCompanyId(companyId);
            openThirdEmployeeDTO.setThirdDepartmentId(userInfo.getOrgId());
//            if (StringUtils.isBlank(userInfo.getDepartment())) {//在根部门下的人员数据
//                openThirdEmployeeDTO.setThirdDepartmentId(rootDeptId);
//            } else {
//                openThirdEmployeeDTO.setThirdDepartmentId(userInfo.getOrgId());
//            }
            openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getOpenId());
            openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
            openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
            openThirdEmployeeDTO.setThirdEmployeePhone(userInfo.getPhone());
            openThirdEmployeeDTO.setThirdEmployeeGender(userInfo.getGender());
            if (!ObjectUtils.isEmpty(userExpandFields)) {
                Map map = JsonUtils.toObj(JsonUtils.toJson(userInfo), Map.class);
                Map expandJson = Maps.newHashMap();
                Lists.newArrayList(userExpandFields.split(",")).forEach(field -> {
                    expandJson.put(field, map.get(field));
                });
                openThirdEmployeeDTO.setExtAttr(expandJson);
            }
            String contactStr = userInfo.getContact();
            if (StringUtils.isNotBlank(contactStr)) {
                List<YunzhijiaEmployeeContactDTO> list = JsonUtils.toObj(contactStr, new TypeReference<List<YunzhijiaEmployeeContactDTO>>() {
                });
                if (!ObjectUtils.isEmpty(list)) {
                    list.stream().forEach(contact -> {
                        if (contact.getName().trim().equals("分贝权限")) {
                            String fbRoleType = contact.getValue();
                            openThirdEmployeeDTO.setThirdEmployeeRoleTye(fbRoleType);
                        }
                    });
                }
            }
            OpenThirdEmployeeDTO newOpenThirdEmployeeDTO = postProcessService.process(openThirdEmployeeDTO,userInfo,companyId,employeeConfig);
            if ( null != newOpenThirdEmployeeDTO ){
                employeeList.add(newOpenThirdEmployeeDTO);
            }
            //查询分贝通员工信息
            List<OpenThirdEmployee> srcEmployeeList = openThirdEmployeeDao.listEmployeeByThirdEmployeeId(OpenType.YUNZHIJIA.getType(), companyId, Lists.newArrayList(openThirdEmployeeDTO.getThirdEmployeeId()));
            //如果员工已存在，则更新
            if (srcEmployeeList != null && srcEmployeeList.size() > 0) {
                openSyncThirdOrgService.updateEmployee(OpenType.YUNZHIJIA.getType(), companyId, employeeList);
            } else {
                openSyncThirdOrgService.addEmployee(OpenType.YUNZHIJIA.getType(), companyId, employeeList);
            }
        } else {
            return TaskResult.EXPIRED;

        }
        return TaskResult.SUCCESS;
    }
}
