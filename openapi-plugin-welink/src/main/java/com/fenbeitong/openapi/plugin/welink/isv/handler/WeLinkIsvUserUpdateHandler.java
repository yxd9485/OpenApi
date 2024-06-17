package com.fenbeitong.openapi.plugin.welink.isv.handler;

import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResponseCode;
import com.fenbeitong.openapi.plugin.welink.common.exception.OpenApiWeLinkException;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvUsersEmailRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvCompanyTrial;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvCompanyTrialDefinitionService;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvEmployeeService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhen on 2020/3/27.
 */
@Component
@Slf4j
public class WeLinkIsvUserUpdateHandler implements ITaskHandler {
    @Autowired
    private WeLinkIsvCompanyTrialDefinitionService weLinkIsvCompanyTrialDefinitionService;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private WeLinkIsvUserAddHandler weLinkIsvUserAddHandler;
    @Autowired
    private WeLinkIsvEmployeeService weLinkIsvEmployeeService;
    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Override
    public TaskType getTaskType() {
        return TaskType.WELINK_ISV_CORP_EDIT_USER;
    }

    @Override
    public TaskResult execute(Task task) {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        WeLinkIsvCompanyTrial weChatIsvCompany = weLinkIsvCompanyTrialDefinitionService.getWelinkIsvCompanyTrialByCorpId(corpId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_COMPANY_UNDEFINED);
        }
        String companyId = weChatIsvCompany.getCompanyId();
        //3.调用welink获取用用户信息
        WeLinkIsvUsersEmailRespDTO welinkUser = null;
        try {
            welinkUser = weLinkIsvEmployeeService.welinkUsersEmail(dataId, corpId);
        } catch (OpenApiWeLinkException e) {
            if (e.getCode() == NumericUtils.obj2int(WeLinkResponseCode.WELINK_ISV_USERS_EMAIL_FAILED)) {
                //4.如果在welink查不到，标记为已处理
                return TaskResult.EXPIRED;
            }
            throw e;
        }
        //4.查询分贝通员工信息
        List<OpenThirdEmployee> srcEmployeeList = openThirdEmployeeDao.listEmployeeByThirdEmployeeId(OpenType.WELINK_ISV.getType(), companyId, Lists.newArrayList(dataId));
        //5.如果员工已存在，则更新
        if (srcEmployeeList == null || srcEmployeeList.size() == 0) {
            return weLinkIsvUserAddHandler.execute(task);
        }
        //6.转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdDepartmentId(welinkUser.getDeptCode());
        openThirdEmployeeDTO.setThirdEmployeeId(welinkUser.getUserId());
        openThirdEmployeeDTO.setThirdEmployeeName(welinkUser.getUserNameCn());
        openThirdEmployeeDTO.setThirdEmployeeEmail(welinkUser.getUserEmail());
        if ("0".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
            openThirdEmployeeDTO.setThirdDepartmentId(corpId);
        }
        employeeList.add(openThirdEmployeeDTO);
        //7.同步
        openSyncThirdOrgService.updateEmployee(OpenType.WELINK_ISV.getType(), companyId, employeeList);
        return TaskResult.SUCCESS;
    }

}
