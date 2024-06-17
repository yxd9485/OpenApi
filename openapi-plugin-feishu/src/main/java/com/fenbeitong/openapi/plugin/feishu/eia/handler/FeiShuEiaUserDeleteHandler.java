package com.fenbeitong.openapi.plugin.feishu.eia.handler;

import com.fenbeitong.finhub.common.utils.StringUtils;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lizhen
 * @date 2020/6/3
 */
@Component
@Slf4j
public class FeiShuEiaUserDeleteHandler implements ITaskHandler {
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Override
    public TaskType getTaskType() {
        return TaskType.FEISHU_EIA_DELETE_USER;
    }

    @Autowired
    private OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;


    @Override
    public TaskResult execute(Task task) {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskResult.EXPIRED;
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
        openThirdOrgUnitManagersDao.deleteByThirdEmployeeId(companyId,dataId);
        return TaskResult.SUCCESS;
    }
}
