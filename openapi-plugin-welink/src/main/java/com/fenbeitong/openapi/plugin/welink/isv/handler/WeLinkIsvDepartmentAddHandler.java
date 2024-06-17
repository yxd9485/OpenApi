package com.fenbeitong.openapi.plugin.welink.isv.handler;

import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
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
 * isv新增部门
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvDepartmentAddHandler {
    @Autowired
    private WeLinkIsvCompanyTrialDefinitionService weLinkIsvCompanyTrialDefinitionService;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private WeLinkIsvDepartmentUpdateHandler weLinkIsvDepartmentUpdateHandler;
    @Autowired
    private WeLinkIsvOrganizationService weLinkIsvOrganizationService;
    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;

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
        //3.调用welink获取部门信息
        List<WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo> weLinkIsvDepartmentInfos = null;
        try {
            weLinkIsvDepartmentInfos = weLinkIsvOrganizationService.weLinkDepartmentsList(corpId, "0", weLinkIsvCompanyTrial.getCompanyName(),1);
            weLinkIsvDepartmentInfos = weLinkIsvDepartmentInfos.stream().filter(d -> dataId.equals(d.getDeptCode())).collect(Collectors.toList());
            if (weLinkIsvDepartmentInfos == null || weLinkIsvDepartmentInfos.size() == 0) {
                return TaskResult.EXPIRED;
            }
        } catch (OpenApiWeLinkException e) {
            if (e.getCode() == NumericUtils.obj2int(WeLinkResponseCode.WELINK_DEPARTMENTS_LIST_FAILED)) {
                //4.如果在welink查不到，标记为已处理
                return TaskResult.EXPIRED;
            }
            throw e;
        }
        //4.查询分贝通部门信息
        List<OpenThirdOrgUnit> srcOrgUnitList = openThirdOrgUnitDao.listOrgUnitByThirdOrgUnitId(OpenType.WELINK_ISV.getType(), companyId, Lists.newArrayList(dataId));
        //5.如果部门已存在，则更新
        if (srcOrgUnitList != null && srcOrgUnitList.size() > 0) {
            return weLinkIsvDepartmentUpdateHandler.execute(task);
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
        openSyncThirdOrgService.addDepartment(OpenType.WELINK_ISV.getType(), companyId, departmentList);
        return TaskResult.SUCCESS;
    }
}
