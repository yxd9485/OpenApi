package com.fenbeitong.openapi.plugin.yunzhijia.handler;

import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class YunzhijiaEmployeeDeleteHandler extends YunzhijiaEmployeeHandler implements ITaskHandler {

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Override
    public TaskType getTaskType() {
        return TaskType.YUNZHIJIA_USER_LEAVE_ORG;
    }

    @Override
    public TaskResult execute(Task task) {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        PluginCorpDefinition byCorpId = getPluginCorpDefinitionByCorpId(corpId);
        //分贝通公司ID
        String companyId = byCorpId.getAppId();
        //同步人员数据到分贝通
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdEmployeeId(dataId);
        employeeList.add(openThirdEmployeeDTO);
        openSyncThirdOrgService.deleteEmployee(OpenType.YUNZHIJIA.getType(), companyId, employeeList);
        return TaskResult.SUCCESS;
    }
}
