package com.fenbeitong.openapi.plugin.task.wechat.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
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
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatDepartmentListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvOrganizationService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode.WECHAT_ISV_COMPANY_UNDEFINED;

/**
 * isv更新部门
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvDepartmentUpdateProcessor extends AbstractTaskProcessor {

    @Autowired
    private WeChatIsvCompanyAuthService weChatIsvCompanyAuthService;

    @Autowired
    private WeChatIsvOrganizationService weChatIsvOrganizationService;

    @Autowired
    private WeChatIsvEmployeeService weChatIsvEmployeeService;

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Autowired
    private WeChatIsvDepartmentAddProcessor weChatIsvDepartmentAddHandler;

    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Override
    public Integer getTaskType() {
        return TaskType.WECHAT_ISV_ORG_DEPT_MODIFY.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        //1.解析task
        String dataId = task.getDataId();
        String corpId = task.getCompanyId();
        //2.查询企业信息
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_COMPANY_UNDEFINED));
        }
        String companyId = weChatIsvCompany.getCompanyId();
        //3.查询部门信息
        List<WechatDepartmentListRespDTO.WechatDepartment> wechatDepartmentList = weChatIsvOrganizationService.getWechatDepartmentList(corpId, dataId, false);
        if (wechatDepartmentList == null || wechatDepartmentList.size() == 0) {
            return TaskProcessResult.success("wechatDepartmentList is null success");
        }
        WechatDepartmentListRespDTO.WechatDepartment wechatDepartment = wechatDepartmentList.get(0);
        //4.查询分贝通部门信息
        List<OpenThirdOrgUnit> srcOrgUnitList = openThirdOrgUnitDao.listOrgUnitByThirdOrgUnitId(OpenType.WECHAT_ISV.getType(), companyId, Lists.newArrayList(dataId));
        //5.如果部门已存在，则更新
        if (srcOrgUnitList == null && srcOrgUnitList.size() == 0) {
            return weChatIsvDepartmentAddHandler.process(task);
        }
        //转换部门
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitFullName(wechatDepartment.getThirdOrgUnitFullName());
        openThirdOrgUnitDTO.setThirdOrgUnitName(wechatDepartment.getName());
        openThirdOrgUnitDTO.setThirdOrgUnitParentId(StringUtils.obj2str(wechatDepartment.getParentId()));
        openThirdOrgUnitDTO.setThirdOrgUnitId(StringUtils.obj2str(wechatDepartment.getId()));
        if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
        }
        if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
        }
        departmentList.add(openThirdOrgUnitDTO);
        openSyncThirdOrgService.updateDepartment(OpenType.WECHAT_ISV.getType(), companyId, departmentList);
        return TaskProcessResult.success("success");

    }
}
