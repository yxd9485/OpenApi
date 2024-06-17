package com.fenbeitong.openapi.plugin.task.dingtalk.eia;

import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiDepartmentService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lizhen
 * @date 2020/8/21
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkDepartmentAddOrUpdateProcessor extends AbstractTaskProcessor {

    @Autowired
    private IApiDepartmentService apiDepartmentService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Override
    public Integer getTaskType() {
        return TaskType.DINGTALK_EIA_CREATE_OR_UPDATE_DEPT.getCode();
    }

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        String corpId = task.getCompanyId();
        String departmentId = task.getDataId();
        String companyId = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId).getAppId();
        OapiDepartmentGetResponse response = apiDepartmentService.getWithOriginal(departmentId, corpId);

        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        if (!response.isSuccess()) {
            log.info("部门已经被删除,corpId={},deptId={}", corpId, departmentId);
            return TaskProcessResult.success("success");
        }
        //转换
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitName(response.getName());
        openThirdOrgUnitDTO.setThirdOrgUnitParentId(StringUtils.obj2str(response.getParentid()));
        openThirdOrgUnitDTO.setThirdOrgUnitId(StringUtils.obj2str(response.getId()));
        if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
        }
        if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
        }
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);

        // 判断设置部门主管配置时否为空
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            // 查询部门主管并赋值
            openThirdOrgUnitDTO.setOrgUnitMasterIds(getDepMasters(accessToken, proxyUrl, openThirdOrgUnitDTO.getThirdOrgUnitId()));
        }
        departmentList.add(openThirdOrgUnitDTO);
        //4.查询分贝通部门信息
        List<OpenThirdOrgUnit> srcOrgUnitList = openThirdOrgUnitDao.listOrgUnitByThirdOrgUnitId(OpenType.DINGTALK_EIA.getType(), companyId, Lists.newArrayList(StringUtils.obj2str(response.getId())));
        //5.如果部门已存在，则更新
        if (srcOrgUnitList != null && srcOrgUnitList.size() > 0) {
            openSyncThirdOrgService.updateDepartment(OpenType.DINGTALK_EIA.getType(), companyId, departmentList);
        } else {
            openSyncThirdOrgService.addDepartment(OpenType.DINGTALK_EIA.getType(), companyId, departmentList);
        }
        // 判断设置部门主管配置时否为空
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            syncThirdOrgManagers(companyId, departmentList);
        }
        return TaskProcessResult.success("success");
    }

    /**
     * 获取部门负责人
     */
    public String getDepMasters(String accessToken, String proxyUrl, String depId) {
        OapiDepartmentGetResponse oapiDepartmentGetResponse = apiDepartmentService.getDepartmentInfo(accessToken, proxyUrl, depId);
        if (oapiDepartmentGetResponse != null) {
            return oapiDepartmentGetResponse.getDeptManagerUseridList().replaceAll("\\|", ",");
        }
        return null;
    }

    public void syncThirdOrgManagers(String companyId, List<OpenThirdOrgUnitDTO> departmentList) {
        //钉钉用户信息
        List<OpenThirdOrgUnitManagers> openThirdOrgUnitManagersList = new ArrayList<>();
        departmentList.forEach(department -> {
            openThirdOrgUnitManagersList.add(OpenThirdOrgUnitManagers.builder()
                    .id(RandomUtils.bsonId())
                    .companyId(companyId)
                    .thirdEmployeeIds(department.getOrgUnitMasterIds())
                    .thirdOrgUnitId(StringUtils.obj2str(department.getThirdOrgUnitId()))
                    .status(0)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build());
        });
        openSyncThirdOrgService.setPartDepManageV2(openThirdOrgUnitManagersList, companyId);
    }

}
