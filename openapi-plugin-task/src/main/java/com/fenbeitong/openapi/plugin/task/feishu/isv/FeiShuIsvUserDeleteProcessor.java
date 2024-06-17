package com.fenbeitong.openapi.plugin.task.feishu.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCompanyDefinitionService;
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
public class FeiShuIsvUserDeleteProcessor extends AbstractTaskProcessor {
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;

    @Override
    public Integer getTaskType() {
        return TaskType.FEISHU_ISV_DELETE_USER.getCode();
    }

    @Autowired
    private OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;

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
        //3.转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdEmployeeId(dataId);
        employeeList.add(openThirdEmployeeDTO);
        //4.同步
        openSyncThirdOrgService.deleteEmployee(OpenType.FEISHU_ISV.getType(), companyId, employeeList);
        // 删除部门中间表
        openThirdOrgUnitManagersDao.deleteByThirdEmployeeId(companyId, dataId);
        return TaskProcessResult.success("success");
    }
}
