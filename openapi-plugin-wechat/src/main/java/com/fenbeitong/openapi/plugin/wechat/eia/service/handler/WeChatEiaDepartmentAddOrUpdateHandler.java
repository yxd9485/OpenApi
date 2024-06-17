package com.fenbeitong.openapi.plugin.wechat.eia.service.handler;

import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.employee.service.IThirdOrgPostProcessService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.eia.service.organization.WeChatEiaOrgUnitService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WechatTokenService;
import com.google.common.collect.Lists;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dave.hansins on 19/7/3.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatEiaDepartmentAddOrUpdateHandler implements ITaskHandler {

    @Autowired
    private WechatTokenService wechatTokenService;

    @Autowired
    private WeChatEiaOrgUnitService qywxOrgUnitService;

    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private IThirdOrgPostProcessService thirdOrgPostProcessService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    public TaskType getTaskType() {
        return TaskType.WECHAT_EIA_CREATE_OR_UPDATE_DEPT;
    }

    @Override
    public TaskResult execute(Task task) {
        String dataId = task.getDataId();
        String corpId = task.getCorpId();
        String companyId = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId).getAppId();
        String qywxAccessToken = wechatTokenService.getWeChatContactTokenByCorpId(corpId);
        OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.DEPARTMENT_SYNC);
        List<Map<String, Object>> qywxDepByDepId = qywxOrgUnitService.getQywxAllDepByDepId(qywxAccessToken, dataId);
        if (ObjUtils.isEmpty(qywxDepByDepId)) {//根据部门ID没有查询到企业微信部门信息，说明该部门已经被删除
            log.info("部门已经被删除,corpId={},deptId={}", corpId, dataId);
            return TaskResult.ABORT;
        }
        Map<String, Object> objectMap = qywxDepByDepId.get(0);
        //转换
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
        openThirdOrgUnitDTO.setCompanyId(companyId);
        openThirdOrgUnitDTO.setThirdOrgUnitName(StringUtils.obj2str(objectMap.get("name")));
        openThirdOrgUnitDTO.setThirdOrgUnitParentId(StringUtils.obj2str(objectMap.get("parentid")));
        openThirdOrgUnitDTO.setThirdOrgUnitId(dataId);
        if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
        }
        if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
        }
        openThirdOrgUnitDTO = thirdOrgPostProcessService.process(openThirdOrgUnitDTO,objectMap,companyId,departmentConfig);
        if ( null != openThirdOrgUnitDTO ){
            departmentList.add(openThirdOrgUnitDTO);
        }
        //4.查询分贝通部门信息
        List<OpenThirdOrgUnit> srcOrgUnitList = openThirdOrgUnitDao.listOrgUnitByThirdOrgUnitId(OpenType.WECHAT_EIA.getType(), companyId, Lists.newArrayList(dataId));
        //5.如果部门已存在，则更新
        if (srcOrgUnitList != null && srcOrgUnitList.size() > 0) {
            openSyncThirdOrgService.updateDepartment(OpenType.WECHAT_EIA.getType(), companyId, departmentList);
        } else {
            openSyncThirdOrgService.addDepartment(OpenType.WECHAT_EIA.getType(), companyId, departmentList);
        }
        return TaskResult.SUCCESS;
    }
}
