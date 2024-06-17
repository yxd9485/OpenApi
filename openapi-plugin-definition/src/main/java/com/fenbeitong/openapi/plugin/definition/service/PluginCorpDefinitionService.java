package com.fenbeitong.openapi.plugin.definition.service;

import com.fenbeitong.finhub.common.utils.CheckUtils;
import com.fenbeitong.finhub.common.utils.FinhubLogger;
import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.corp.CreatePluginCorpDefinitionReqDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.corp.PluginCorpDefinitionInfoDTO;
import com.fenbeitong.openapi.plugin.definition.util.DefinitionCheckUtils;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.task.service.impl.TaskServiceImpl;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luastar.swift.base.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 插件集成企业三方配置
 * Created by log.chang on 2019/12/23.
 */
@ServiceAspect
@Service
@Slf4j
public class PluginCorpDefinitionService {

    @Autowired
    private DingTalkRouteDefinitionService dingTalkRouteDefinitionService;
    @Autowired
    private PluginCorpAppDefinitionService pluginCorpAppDefinitionService;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;
    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    TaskServiceImpl taskService;

    @Autowired
    private IOpenCompanySourceTypeService iOpenCompanySourceTypeService;
    /**
     * 添加企业插件集成三方配置
     */
    public synchronized PluginCorpDefinitionInfoDTO createPluginCorp(CreatePluginCorpDefinitionReqDTO req) {
        AuthDefinition authDefinition = getAuthDefinitionByAppId(req.getAppId());
        Date now = DateUtils.now();
        PluginCorpDefinition pluginCorpDefinition = createPluginCorpDefinition(req, authDefinition, now);
        PluginCorpAppDefinition pluginCorpAppDefinition = pluginCorpAppDefinitionService.createPluginCorpAppDefinition(req.getThirdCorpId(), req.getThirdAppKey(),
                req.getThirdAppSecret(), req.getThirdAppName(), req.getThirdAgentId(), now);
        dingTalkRouteDefinitionService.createDingTalkRouteDefinition(req.getThirdCorpId(), req.getProxyUrl(), authDefinition.getAppName(), now);
        iOpenCompanySourceTypeService.saveOpenCompanySourceType(req.getAppId(), req.getThirdAppName(), req.getThirdCorpId(), req.getOpenType());
        return PluginCorpDefinitionInfoDTO.builder()
                .appId(authDefinition.getAppId())
                .adminId(pluginCorpDefinition.getAdminId())
                .thirdAdminId(pluginCorpDefinition.getThirdAdminId())
                .thirdCorpId(pluginCorpDefinition.getThirdCorpId())
                .thirdAppKey(pluginCorpAppDefinition.getThirdAppKey())
                .thirdAppSecret(pluginCorpAppDefinition.getThirdAppSecret())
                .thirdAppName(pluginCorpAppDefinition.getThirdAppName())
                .thirdAgentId(pluginCorpAppDefinition.getThirdAgentId())
                .proxyUrl(pluginCorpAppDefinition.getAppUrl())
                .openType(req.getOpenType())
                .build();
    }

    /**
     * 保存插件集成企业三方配置信息
     */
    private PluginCorpDefinition createPluginCorpDefinition(CreatePluginCorpDefinitionReqDTO req, AuthDefinition authDefinition, Date now) {
        PluginCorpDefinition pluginCorpDefinition = PluginCorpDefinition.builder()
                .appId(req.getAppId())
                .appKey(authDefinition.getAppKey())
                .appName(authDefinition.getAppName())
                .signKey(authDefinition.getSignKey())
                .adminId(req.getAdminId())
                .thirdAdminId(req.getThirdAdminId())
                .thirdCorpId(req.getThirdCorpId())
                .createTime(now)
                .updateTime(now)
                .state(1)
                .build();
        pluginCorpDefinitionDao.saveSelective(pluginCorpDefinition);
        return pluginCorpDefinition;
    }

    /**
     * 根据appId查询企业授权信息
     */
    public AuthDefinition getAuthDefinitionByAppId(String appId) {
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(appId);
        DefinitionCheckUtils.checkAuthDefinition(appId, authDefinition);
        return authDefinition;
    }




    public void genTask(String corpId,String pluginType,String taskType,String dataId) {
        CheckUtils.checkEmpty(corpId, "corpId 不能为空");
        CheckUtils.checkEmpty(corpId, "pluginType 不能为空");
        CheckUtils.checkEmpty(dataId, "dataIdListStr 不能为空");
        CheckUtils.checkEmpty(taskType, "taskType 不能为空");
        List<String> dataIdList = JsonUtils.toObj(dataId, List.class);
        CheckUtils.checkEmpty(dataIdList, "dataId 类型错误");
        List<Map<String,String>> resultList = Lists.newArrayList();
        for(String data:dataIdList){
            Map<String, Object> eventMsg = new HashMap<>();
            //先判断插件类型
            //再判断任务类型
            if(pluginType.equals("dingtalk")){
                if ("2".equals(taskType)) {//删除人员任务
                    eventMsg.put("EventType", "user_leave_org");
                } else if ("0".equals(taskType)) {//新增人员
                    eventMsg.put("EventType", "user_add_org");
                } else if ("1".equals(taskType)) {//人员更新
                    eventMsg.put("EventType", "user_modify_org");
                } else if ("5".equals(taskType)) {//部门删除
                    eventMsg.put("EventType", "org_dept_remove");
                } else if ("3".equals(taskType)) {//部门新增
                    eventMsg.put("EventType", "org_dept_create");
                } else if ("4".equals(taskType)) {//部门更新
                    eventMsg.put("EventType", "org_dept_modify");
                }
            }else if(pluginType.equals("feishu")){
                if("2".equals(taskType)){//飞书模拟数据 删除人
                    eventMsg.put("EventType", "feishu_eia_delete_user");
                }else if("0".equals(taskType)){//新增人
                    eventMsg.put("EventType", "feishu_eia_create_user");
                }else if("1".equals(taskType)){//更新人
                    eventMsg.put("EventType", "feishu_eia_update_user");
                }else if("5".equals(taskType)){//删除部门
                    eventMsg.put("EventType", "feishu_eia_org_dept_delete");
                }else if("3".equals(taskType)){//新增部门
                    eventMsg.put("EventType", "feishu_eia_org_dept_create");
                }else if("4".equals(taskType)){//更新部门
                    eventMsg.put("EventType", "feishu_eia_org_dept_update");
                }
            }

            eventMsg.put("CorpId", corpId);
            eventMsg.put("TimeStamp", String.valueOf(System.currentTimeMillis()));
            eventMsg.put("DataId", data);
            eventMsg.put("DataContent", "");

            FinhubLogger.info("手动创建定时任务请求参数 {}", JsonUtils.toJson(eventMsg));
            Map<String,String> resultMap = Maps.newHashMap();
            try {
                taskService.genTask(eventMsg,null);
                resultMap.put(dataId, "succeed");
            } catch (Exception e) {
                log.info("失败信息 {}",e.getCause());
//                e.printStackTrace();
                resultMap.put(dataId, "failed");
            }
            resultList.add(resultMap);
        }
        DefinitionResultDTO.success(resultList);
    }
}
