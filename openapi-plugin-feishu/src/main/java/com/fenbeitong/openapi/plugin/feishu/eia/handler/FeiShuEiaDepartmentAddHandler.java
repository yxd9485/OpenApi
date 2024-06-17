package com.fenbeitong.openapi.plugin.feishu.eia.handler;

import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuDepartmentSimpleListRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaOrganizationService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;

/**
 * 新增部门
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuEiaDepartmentAddHandler implements ITaskHandler {
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private FeiShuEiaDepartmentUpdateHandler feiShuEiaDepartmentUpdateHandler;
    @Autowired
    private FeiShuEiaOrganizationService feiShuEiaOrganizationService;
    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;
    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    public TaskType getTaskType() {
        return TaskType.FEISHU_EIA_ORG_DEPT_CREATE;
    }

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
        String companyName = pluginCorpDefinition.getAppName();
        String companyId = pluginCorpDefinition.getAppId();
        //3.调用飞书获取部门信息
        FeiShuDepartmentSimpleListRespDTO.DepartmentInfo departmentInfo = null;
        try {
            departmentInfo = feiShuEiaOrganizationService.getDepartmentInfo(dataId, corpId);
        } catch (OpenApiFeiShuException e) {
            if (e.getCode() == FeiShuResponseCode.FEISHU_ISV_GET_DEPARTMENT_INFO_FAILED) {
                //4.如果在飞书查不到，标记为已处理
                return TaskResult.EXPIRED;
            }
            throw e;
        }
        //4.查询分贝通部门信息
        List<OpenThirdOrgUnit> srcOrgUnitList = openThirdOrgUnitDao.listOrgUnitByThirdOrgUnitId(OpenType.FEISHU_EIA.getType(), companyId, Lists.newArrayList(dataId));
        //5.如果部门已存在，则更新
        if (srcOrgUnitList != null && srcOrgUnitList.size() > 0) {
            return feiShuEiaDepartmentUpdateHandler.execute(task);
        }
        //6.转换数据
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitFullName(departmentInfo.getThirdOrgUnitFullName());
        openThirdOrgUnitDTO.setThirdOrgUnitName(departmentInfo.getName());
        openThirdOrgUnitDTO.setThirdOrgUnitParentId(departmentInfo.getParentId());
        openThirdOrgUnitDTO.setThirdOrgUnitId(departmentInfo.getId());
        if ("0".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
        }
        if ("0".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
        }
        // 先查一下配置表 看是否需要过滤
        OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getDepartmentConfig(companyId);
        boolean departmentNeedFilter = departmentConfig != null;
        OpenThirdOrgUnitDTO targetDTO = null;
        if (departmentNeedFilter) {
            targetDTO = feiShuEiaOrganizationService.departmentBeforeSyncFilter(departmentConfig, departmentInfo, openThirdOrgUnitDTO);
        }
        departmentList.add(targetDTO != null ? targetDTO : openThirdOrgUnitDTO);
        //7.同步
        openSyncThirdOrgService.addDepartment(OpenType.FEISHU_EIA.getType(), companyId, departmentList);
        return TaskResult.SUCCESS;
    }
}
