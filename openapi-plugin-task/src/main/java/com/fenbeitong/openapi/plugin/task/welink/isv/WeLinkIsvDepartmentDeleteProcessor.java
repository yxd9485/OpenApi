package com.fenbeitong.openapi.plugin.task.welink.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResponseCode;
import com.fenbeitong.openapi.plugin.welink.common.exception.OpenApiWeLinkException;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvCompanyTrial;
import com.fenbeitong.openapi.plugin.welink.isv.handler.WeLinkIsvDepartmentAddHandler;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvCompanyTrialDefinitionService;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvOrganizationService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * isv删除企业
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvDepartmentDeleteProcessor extends AbstractTaskProcessor {

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
    public Integer getTaskType() {
        return TaskType.WELINK_ISV_CORP_DEL_DEPT.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        //1.解析task
        String corpId = task.getCompanyId();
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
        return TaskProcessResult.success("success");
    }

}
