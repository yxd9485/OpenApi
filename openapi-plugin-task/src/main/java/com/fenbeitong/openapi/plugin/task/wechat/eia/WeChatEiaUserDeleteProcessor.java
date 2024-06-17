package com.fenbeitong.openapi.plugin.task.wechat.eia;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdExpandFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.wechat.eia.service.employee.WeChatEiaEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WechatTokenService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by  dave.hansins on 19/7/2.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatEiaUserDeleteProcessor extends AbstractTaskProcessor {

    @Autowired
    private WechatTokenService wechatTokenService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private WeChatEiaEmployeeService qywxEmployeeService;

    @Autowired
    private OpenThirdExpandFieldConfigDao expandFieldConfigDao;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Override
    public Integer getTaskType() {
        return TaskType.WECHAT_EIA_DELETE_USER.getCode();
    }


    @Override
    public TaskProcessResult process(FinhubTask task) {
        String userId = task.getDataId();
        String corpId = task.getCompanyId();
        String companyId = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId).getAppId();
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdEmployeeId(userId);
        employeeList.add(openThirdEmployeeDTO);
        openSyncThirdOrgService.deleteEmployee(OpenType.WECHAT_EIA.getType(), companyId, employeeList);
        return TaskProcessResult.success("success");
    }


}
