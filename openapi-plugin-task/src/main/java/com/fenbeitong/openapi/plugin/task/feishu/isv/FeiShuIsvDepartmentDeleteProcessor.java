package com.fenbeitong.openapi.plugin.task.feishu.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvOrganizationService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
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
public class FeiShuIsvDepartmentDeleteProcessor extends AbstractTaskProcessor {

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private FeiShuIsvDepartmentUpdateProcessor feiShuIsvDepartmentUpdateHandler;
    @Autowired
    private FeiShuIsvOrganizationService feiShuIsvOrganizationService;
    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;
    @Autowired
    private OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;

    @Override
    public Integer getTaskType() {
        return TaskType.FEISHU_ISV_ORG_DEPT_DELETE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        if (feishuIsvCompany == null) {
            return TaskProcessResult.success("检查企业未注册 success");
        }
        String companyId = feishuIsvCompany.getCompanyId();
        //3.转换数据
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitId(dataId);
        departmentList.add(openThirdOrgUnitDTO);
        //4.同步
        openSyncThirdOrgService.deleteDepartment(OpenType.FEISHU_ISV.getType(), companyId, departmentList);
        // 删除部门主管
        openThirdOrgUnitManagersDao.deleteByThirdOrgUnitId(companyId, dataId);
        return TaskProcessResult.success("success");
    }


}
