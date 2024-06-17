package com.fenbeitong.openapi.plugin.task.welink.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResponseCode;
import com.fenbeitong.openapi.plugin.welink.common.exception.OpenApiWeLinkException;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvDepartmentsListRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvCompanyTrial;
import com.fenbeitong.openapi.plugin.welink.isv.handler.WeLinkIsvDepartmentAddHandler;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvCompanyTrialDefinitionService;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvOrganizationService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * isv更新部门
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvDepartmentUpdateProcessor extends AbstractTaskProcessor {
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
        return TaskType.WELINK_ISV_CORP_EDIT_DEPT.getCode();
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
        //3.调用welink获取部门信息
        List<WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo> weLinkIsvDepartmentInfos = null;
        try {
            weLinkIsvDepartmentInfos = weLinkIsvOrganizationService.weLinkDepartmentsList(corpId, "0", weLinkIsvCompanyTrial.getCompanyName(), 1);
            weLinkIsvDepartmentInfos = weLinkIsvDepartmentInfos.stream().filter(d -> dataId.equals(d.getDeptCode())).collect(Collectors.toList());
            if (weLinkIsvDepartmentInfos == null || weLinkIsvDepartmentInfos.size() == 0) {
                return TaskProcessResult.success("weLinkIsvDepartmentInfos is null success");
            }
        } catch (OpenApiWeLinkException e) {
            if (e.getCode() == NumericUtils.obj2int(WeLinkResponseCode.WELINK_DEPARTMENTS_LIST_FAILED)) {
                //4.如果在welink查不到，标记为已处理
                return TaskProcessResult.success("如果在welink查不到，标记为已处理 success");
            }
            throw e;
        }
        //4.查询分贝通部门信息
        List<OpenThirdOrgUnit> srcOrgUnitList = openThirdOrgUnitDao.listOrgUnitByThirdOrgUnitId(OpenType.WELINK_ISV.getType(), companyId, Lists.newArrayList(dataId));
        //5.如果部门不存在，则新增
        if (srcOrgUnitList == null || srcOrgUnitList.size() == 0) {
            TaskResult taskResult = weLinkIsvDepartmentAddHandler.execute(FinhubTaskUtils.convert2Task(task));
            return FinhubTaskUtils.convert2FinhubTaskResult(taskResult);
        }
        //6.转换数据
        WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo weLinkIsvDepartmentInfo = weLinkIsvDepartmentInfos.get(0);
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitFullName(weLinkIsvDepartmentInfo.getThirdOrgUnitFullName());
        openThirdOrgUnitDTO.setThirdOrgUnitId(weLinkIsvDepartmentInfo.getDeptCode());
        openThirdOrgUnitDTO.setThirdOrgUnitName(weLinkIsvDepartmentInfo.getDeptNameCn());
        openThirdOrgUnitDTO.setThirdOrgUnitParentId(weLinkIsvDepartmentInfo.getFatherCode());
        if ("0".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
        }
        if ("0".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
        }
        departmentList.add(openThirdOrgUnitDTO);
        //7.同步
        openSyncThirdOrgService.updateDepartment(OpenType.WELINK_ISV.getType(), companyId, departmentList);
        return TaskProcessResult.success("success");
    }
}
