package com.fenbeitong.openapi.plugin.wechat.isv.handler;

import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;

import static com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode.WECHAT_ISV_COMPANY_UNDEFINED;

/**
 * isv删除企业
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvDepartmentDeleteHandler implements ITaskHandler {

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Override
    public TaskType getTaskType() {
        return TaskType.WECHAT_ISV_ORG_DEPT_REMOVE;
    }

    @Override
    public TaskResult execute(Task task) {
        //1.解析task
        String dataId = task.getDataId();
        String corpId = task.getCorpId();
        if (WeChatIsvConstant.DEPARTMENT_ROOT == (Long.valueOf(dataId))) {
            log.info("暂不支持解散企业. corpId: {}", task.getCorpId());
            return TaskResult.ABORT;
        }
        //2.查询企业信息
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_COMPANY_UNDEFINED));
        }
        String companyId = weChatIsvCompany.getCompanyId();
        //3.转换数据
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitId(dataId);
        departmentList.add(openThirdOrgUnitDTO);
        //4.同步
        openSyncThirdOrgService.deleteDepartment(OpenType.WECHAT_ISV.getType(), companyId, departmentList);
        return TaskResult.SUCCESS;
    }


}
