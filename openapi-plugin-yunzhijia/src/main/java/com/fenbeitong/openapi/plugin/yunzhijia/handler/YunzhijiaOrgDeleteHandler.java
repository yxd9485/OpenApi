package com.fenbeitong.openapi.plugin.yunzhijia.handler;

import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.sdk.webservice.organization.FbtOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class YunzhijiaOrgDeleteHandler extends YunzhijiaOrgHandler implements ITaskHandler {

    @Autowired
    FbtOrganizationService fbtOrganizationService;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Override
    public TaskType getTaskType() {
        return TaskType.YUNZHIJIA_ORG_DEPT_REMOVE;
    }

    @Override
    public TaskResult execute(Task task) {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.查询企业注册信息
        PluginCorpDefinition byCorpId = getPluginCorpDefinitionByCorpId(corpId);
        //转换数据
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(byCorpId.getAppId());
        openThirdOrgUnitDTO.setThirdOrgUnitId(dataId);
        departmentList.add(openThirdOrgUnitDTO);
        //同步
        openSyncThirdOrgService.deleteDepartment(OpenType.YUNZHIJIA.getType(), byCorpId.getAppId(), departmentList);
        return TaskResult.SUCCESS;
    }
}
