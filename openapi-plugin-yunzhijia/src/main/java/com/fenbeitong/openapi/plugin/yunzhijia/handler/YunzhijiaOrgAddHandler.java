package com.fenbeitong.openapi.plugin.yunzhijia.handler;

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
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaOrgDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class YunzhijiaOrgAddHandler extends YunzhijiaOrgHandler implements ITaskHandler {


    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Override
    public TaskType getTaskType() {
        return TaskType.YUNZHIJIA_ORG_DEPT_CREATE;
    }

    @Override
    public TaskResult execute(Task task) {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.查询企业注册信息
        PluginCorpDefinition byCorpId = getPluginCorpDefinitionByCorpId(corpId);
        //3.查询云之家部门详情
        YunzhijiaResponse<List<YunzhijiaOrgDTO>> yunzhijiaOrgDetail = getYunzhijiaOrg(corpId, dataId);
        if (ObjectUtils.isEmpty(yunzhijiaOrgDetail) || ObjectUtils.isEmpty(yunzhijiaOrgDetail.getData())) {
            return TaskResult.EXPIRED;
        }
        //5.拼装分贝通请求参数
        YunzhijiaOrgDTO yunzhijiaOrgDTO = yunzhijiaOrgDetail.getData().get(0);
        String orgName = yunzhijiaOrgDTO.getName();
        String orgParentId = yunzhijiaOrgDTO.getParentId();

        //转换部门
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(byCorpId.getAppId());
        openThirdOrgUnitDTO.setThirdOrgUnitName(yunzhijiaOrgDTO.getName());
        openThirdOrgUnitDTO.setThirdOrgUnitParentId(yunzhijiaOrgDTO.getParentId());
        openThirdOrgUnitDTO.setThirdOrgUnitId(yunzhijiaOrgDTO.getId());
        departmentList.add(openThirdOrgUnitDTO);
        List<OpenThirdOrgUnit> srcOrgUnitList = openThirdOrgUnitDao.listOrgUnitByThirdOrgUnitId(OpenType.YUNZHIJIA.getType(), byCorpId.getAppId(), Lists.newArrayList(yunzhijiaOrgDTO.getId()));
        //5.如果部门已存在，则更新
        if (srcOrgUnitList != null && srcOrgUnitList.size() > 0) {
            openSyncThirdOrgService.updateDepartment(OpenType.YUNZHIJIA.getType(), byCorpId.getAppId(), departmentList);
        } else {
            openSyncThirdOrgService.addDepartment(OpenType.YUNZHIJIA.getType(), byCorpId.getAppId(), departmentList);
        }
        return TaskResult.SUCCESS;
    }
}
