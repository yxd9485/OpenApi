package com.fenbeitong.openapi.plugin.task.yunzhijia;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.handler.YunzhijiaOrgHandler;
import com.fenbeitong.openapi.sdk.webservice.organization.FbtOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class YunzhijiaOrgDeleteProcessor extends YunzhijiaOrgHandler implements ITaskProcessor {

    @Autowired
    FbtOrganizationService fbtOrganizationService;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private TaskConfig taskConfig;

    @Override
    public Integer getTaskType() {
        return TaskType.YUNZHIJIA_ORG_DEPT_REMOVE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        //1.解析task
        String corpId = task.getCompanyId();
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
        return TaskProcessResult.success("success");
    }


    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
