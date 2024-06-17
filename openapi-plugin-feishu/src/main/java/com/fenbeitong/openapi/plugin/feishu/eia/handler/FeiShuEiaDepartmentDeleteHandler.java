package com.fenbeitong.openapi.plugin.feishu.eia.handler;

import com.fenbeitong.finhub.common.utils.StringUtils;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除企业
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuEiaDepartmentDeleteHandler implements ITaskHandler {

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Override
    public TaskType getTaskType() {
        return TaskType.FEISHU_EIA_ORG_DEPT_DELETE;
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
        //3.转换数据
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitId(dataId);
        departmentList.add(openThirdOrgUnitDTO);
        //4.同步
        openSyncThirdOrgService.deleteDepartment(OpenType.FEISHU_EIA.getType(), companyId, departmentList);
        // 删除部门主管
        openThirdOrgUnitManagersDao.deleteByThirdOrgUnitId(companyId, dataId);
        return TaskResult.SUCCESS;
    }

}
