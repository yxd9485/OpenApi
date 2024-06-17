package com.fenbeitong.openapi.plugin.welink.isv.handler;

import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResponseCode;
import com.fenbeitong.openapi.plugin.welink.common.exception.OpenApiWeLinkException;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvDepartmentsListRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvCompanyTrial;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvCompanyTrialDefinitionService;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvOrganizationService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * isv删除企业
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvDepartmentDeleteHandler implements ITaskHandler {

    @Autowired
    private WeLinkIsvCompanyTrialDefinitionService weLinkIsvCompanyTrialDefinitionService;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private WeLinkIsvDepartmentAddHandler weLinkIsvDepartmentAddHandler;
    @Autowired
    private WeLinkIsvOrganizationService weLinkIsvOrganizationService;
    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Override
    public TaskType getTaskType() {
        return TaskType.WELINK_ISV_CORP_DEL_DEPT;
    }

    public TaskResult execute(Task task) {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        WeLinkIsvCompanyTrial weLinkIsvCompanyTrial = weLinkIsvCompanyTrialDefinitionService.getWelinkIsvCompanyTrialByCorpId(corpId);
        if (weLinkIsvCompanyTrial == null) {
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_COMPANY_UNDEFINED);
        }
        String companyId = weLinkIsvCompanyTrial.getCompanyId();
        //3.转换数据
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitId(dataId);
        departmentList.add(openThirdOrgUnitDTO);
        //4.同步
        openSyncThirdOrgService.deleteDepartment(OpenType.WELINK_ISV.getType(), companyId, departmentList);
        return TaskResult.SUCCESS;
    }

}
