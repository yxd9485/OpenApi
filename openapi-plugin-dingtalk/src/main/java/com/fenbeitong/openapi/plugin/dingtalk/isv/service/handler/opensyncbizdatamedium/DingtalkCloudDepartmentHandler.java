package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdatamedium;

import com.dingtalk.api.request.OapiDepartmentGetRequest;
import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserListbypageResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkCallbackTagConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataMediumType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvEmployeeService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvOrganizationService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lizhen
 * @date 2020/7/18
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkCloudDepartmentHandler implements IOpenSyncBizDataMediumTaskHandler {

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    private IDingtalkIsvOrganizationService dingtalkIsvOrganizationService;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    private OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;

    @Override
    public OpenSyncBizDataMediumType getTaskType() {
        return OpenSyncBizDataMediumType.DINGTALK_ISV_DEPARTMENT;
    }

    @Override
    public TaskResult execute(OpenSyncBizDataMedium task) {
        String corpId = task.getCorpId();
        String bizData = task.getBizData();
        String deptId = task.getBizId();
        //检查企业是否注册
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        String companyId = dingtalkIsvCompany.getCompanyId();
        Map<String, Object> dataMap = JsonUtils.toObj(bizData, Map.class);
        String syncAction = StringUtils.obj2str(dataMap.get("syncAction"));
        if (DingtalkCallbackTagConstant.ORG_DEPT_REMOVE.equals(syncAction)) {
            departmentRemove(deptId, companyId);
        } else {
            return addOrUpdateDepartment(deptId, corpId, companyId);
        }
        return TaskResult.SUCCESS;
    }

    /**
     * 删除部门
     *
     * @param deptId
     * @param companyId
     */
    private void departmentRemove(String deptId, String companyId) {
        //转换数据
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitId(deptId);
        departmentList.add(openThirdOrgUnitDTO);
        //同步
        openSyncThirdOrgService.deleteDepartment(OpenType.DINGTALK_ISV.getType(), companyId, departmentList);
        // 删除部门主管
        openThirdOrgUnitManagersDao.deleteByThirdOrgUnitId(companyId, deptId);
    }

    /**
     * 添加或修改部门
     *
     * @param deptId
     * @param corpId
     * @param companyId
     * @return
     */
    private TaskResult addOrUpdateDepartment(String deptId, String corpId, String companyId) {
        OapiDepartmentGetResponse departmentInfo = dingtalkIsvOrganizationService.getDepartmentDetail(deptId, corpId);
        if (!departmentInfo.isSuccess()) {
            //不存在，任务过期
            return TaskResult.EXPIRED;
        }
        //转换
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitName(departmentInfo.getName());
        openThirdOrgUnitDTO.setThirdOrgUnitParentId(StringUtils.obj2str(departmentInfo.getParentid()));
        openThirdOrgUnitDTO.setThirdOrgUnitId(StringUtils.obj2str(departmentInfo.getId()));
        if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
        }
        if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
        }

        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        // 判断设置部门主管配置是否为空
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            String deptManagerUseridList = departmentInfo.getDeptManagerUseridList();
            // 查询部门主管
            openThirdOrgUnitDTO.setOrgUnitMasterIds(deptManagerUseridList.replaceAll("\\|", ","));
        }
        departmentList.add(openThirdOrgUnitDTO);
        //4.查询分贝通部门信息
        List<OpenThirdOrgUnit> srcOrgUnitList = openThirdOrgUnitDao.listOrgUnitByThirdOrgUnitId(OpenType.DINGTALK_ISV.getType(), companyId, Lists.newArrayList(deptId));
        //5.如果部门已存在，则更新
        if (srcOrgUnitList != null && srcOrgUnitList.size() > 0) {
            openSyncThirdOrgService.updateDepartment(OpenType.DINGTALK_ISV.getType(), companyId, departmentList);
        } else {
            openSyncThirdOrgService.addDepartment(OpenType.DINGTALK_ISV.getType(), companyId, departmentList);
        }
        // 判断设置部门主管配置是否为空
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            openSyncThirdOrgService.setPartDepManagePackV2(departmentList, companyId);
        }
        return TaskResult.SUCCESS;
    }


}
