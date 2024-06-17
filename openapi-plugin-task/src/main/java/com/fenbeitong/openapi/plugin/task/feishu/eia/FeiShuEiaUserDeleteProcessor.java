package com.fenbeitong.openapi.plugin.task.feishu.eia;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lizhen
 * @date 2020/6/3
 */
@Component
@Slf4j
public class FeiShuEiaUserDeleteProcessor extends AbstractTaskProcessor {

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Override
    public Integer getTaskType() {
        return TaskType.FEISHU_EIA_DELETE_USER.getCode();
    }

    @Autowired
    private OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;

    @Override
    public TaskProcessResult process(FinhubTask task) {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskProcessResult.success("企业不存在，任务丢弃 success");
        }
        String companyId = pluginCorpDefinition.getAppId();
        //3.转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdEmployeeId(dataId);
        employeeList.add(openThirdEmployeeDTO);
        //4.同步
        openSyncThirdOrgService.deleteEmployee(OpenType.FEISHU_EIA.getType(), companyId, employeeList);
        // 删除部门中间表
        openThirdOrgUnitManagersDao.deleteByThirdEmployeeId(companyId, dataId);
        return TaskProcessResult.success("success");
    }
}
