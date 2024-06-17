package com.fenbeitong.openapi.plugin.feishu.isv.handler;

import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuDepartmentSimpleListRespDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvOrganizationService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 新增部门
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvDepartmentAddHandler  implements ITaskHandler {
    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private FeiShuIsvDepartmentUpdateHandler feiShuIsvDepartmentUpdateHandler;
    @Autowired
    private FeiShuIsvOrganizationService feiShuIsvOrganizationService;
    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;
    @Autowired
    private OpenSysConfigDao openSysConfigDao;
    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    public TaskType getTaskType() {
        return TaskType.FEISHU_ISV_ORG_DEPT_CREATE;
    }

    @Override
    public TaskResult execute(Task task) {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        if (feishuIsvCompany == null) {
            return TaskResult.ABORT;
        }
        String companyId = feishuIsvCompany.getCompanyId();
        //3.调用飞书获取部门信息
        FeiShuDepartmentSimpleListRespDTO.DepartmentInfo departmentInfo = null;
        try {
            departmentInfo = feiShuIsvOrganizationService.getDepartmentInfo(dataId, corpId);
        } catch (OpenApiFeiShuException e) {
            if (e.getCode() == FeiShuResponseCode.FEISHU_ISV_GET_DEPARTMENT_INFO_FAILED) {
                //4.如果在飞书查不到，标记为已处理
                return TaskResult.EXPIRED;
            }
            throw e;
        }
        //4.查询分贝通部门信息
        List<OpenThirdOrgUnit> srcOrgUnitList = openThirdOrgUnitDao.listOrgUnitByThirdOrgUnitId(OpenType.FEISHU_ISV.getType(), companyId, Lists.newArrayList(dataId));
        //5.如果部门已存在，则更新
        if (srcOrgUnitList != null && srcOrgUnitList.size() > 0) {
            return feiShuIsvDepartmentUpdateHandler.execute(task);
        }
        //6.转换数据
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitFullName(departmentInfo.getThirdOrgUnitFullName());
        openThirdOrgUnitDTO.setThirdOrgUnitName(departmentInfo.getName());
        openThirdOrgUnitDTO.setThirdOrgUnitParentId(departmentInfo.getParentId());
        openThirdOrgUnitDTO.setThirdOrgUnitId(departmentInfo.getId());
        openThirdOrgUnitDTO.setOrgUnitMasterIds(departmentInfo.getLeaderUserId());
        if ("0".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
        }
        if ("0".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
        }
        // 先查一下配置表 看是否需要过滤
        OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getDepartmentConfig(companyId);
        boolean departmentNeedFilter = departmentConfig != null;
        OpenThirdOrgUnitDTO targetDTO = null;
        if (departmentNeedFilter) {
            targetDTO = feiShuIsvOrganizationService.departmentBeforeSyncFilter(departmentConfig, departmentInfo, openThirdOrgUnitDTO);
        }
        departmentList.add(targetDTO != null ? targetDTO : openThirdOrgUnitDTO);

        //7.同步
        openSyncThirdOrgService.addDepartment(OpenType.FEISHU_ISV.getType(), companyId, departmentList);

        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        // 判断设置部门主管配置时否为空
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            openSyncThirdOrgService.setPartDepManagePackV2(departmentList, companyId);
        }
        return TaskResult.SUCCESS;
    }


}
