package com.fenbeitong.openapi.plugin.task.wechat.eia;

import com.fenbeitong.finhub.common.utils.FinhubLogger;
import com.fenbeitong.finhub.common.utils.NumericUtils;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WeChatEiaConstant;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dave.hansins on 19/7/3.
 */
@ServiceAspect
@Service
public class WeChatEiaDepartmentDeleteProcessor extends AbstractTaskProcessor {

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Override
    public Integer getTaskType() {
        return TaskType.WECHAT_EIA_REMOVE_DEPT.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        String dataId = task.getDataId();
        String corpId = task.getCompanyId();
        String companyId = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId).getAppId();
        if (WeChatEiaConstant.DEPARTMENT_ROOT == (NumericUtils.obj2long(dataId))) {
            FinhubLogger.info("暂不支持解散企业. corpId: {}", task.getCompanyId());
            return TaskProcessResult.success("暂不支持解散企业 abort success");
        }
        //转换数据
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitId(dataId);
        departmentList.add(openThirdOrgUnitDTO);
        //同步
        openSyncThirdOrgService.deleteDepartment(OpenType.WECHAT_EIA.getType(), companyId, departmentList);
        return TaskProcessResult.success("success");
    }
}
