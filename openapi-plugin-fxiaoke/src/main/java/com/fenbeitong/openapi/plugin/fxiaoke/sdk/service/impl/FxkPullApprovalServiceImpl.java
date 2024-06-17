package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.enums.FxiaokeApprovalStatus;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.enums.FxiaokeApprovalTriggerType;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.enums.FxiaokeApprovalType;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeCorpAppDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeTaskDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.*;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeCorpApp;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeTask;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkAccessTokenService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkApprovalInstanceService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkCustomDataService;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ServiceAspect
@Service
@Slf4j
public class FxkPullApprovalServiceImpl {
    @Autowired
    IFxkApprovalInstanceService iFxkApprovalInstanceService;
    @Autowired
    IFxkCustomDataService iFxkCustomDataService;
    @Autowired
    ThirdApplyDefinitionDao thirdApplyDefinitionDao;
    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    IFxkAccessTokenService iFxkAccessTokenService;
    @Autowired
    FxiaokeCorpAppDao fxiaokeCorpAppDao;
    @Autowired
    FxiaokeTaskDao fxiaokeTaskDao;

    /**
     * 获取自定义审批列表数据,申请用车数据
     *
     * @param corpId
     * @return
     */
    public List<FxkApprovalInstance> getFxkApprovalList(String corpId, String flowApiName, String apiUserId) {
        Example example = new Example(FxiaokeCorpApp.class);
        example.createCriteria().andEqualTo("corpId", corpId)
                .andEqualTo("appState", 0);
        FxiaokeCorpApp fxiaokeCorpApp = fxiaokeCorpAppDao.getByExample(example);
        if (!ObjectUtils.isEmpty(fxiaokeCorpApp)) {
            FxkGetCorpAccessTokenReqDTO tokenReqDTO = FxkGetCorpAccessTokenReqDTO.builder()
                    .appId(fxiaokeCorpApp.getAppId())
                    .appSecret(fxiaokeCorpApp.getAppSecret())
                    .permanentCode(fxiaokeCorpApp.getPermanent())
                    .build();
            FxkGetCorpAccessTokenRespDTO corpAccessToken = iFxkAccessTokenService.getCorpAccessToken(tokenReqDTO);
            if (!ObjectUtils.isEmpty(corpAccessToken)) {
                Integer errorCode = corpAccessToken.getErrorCode();
                if (0 == errorCode) {
                    int pageSize = 100;
                    int offset = 1;
                    //分页查询
                    FxkGetApprovalInstanceListRespDTO instanceList = null;
                    List<FxkApprovalInstance> fxkApprovalInstances = new ArrayList<>();
                    LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);//当天零点
                    long todayBeginTimeL = todayStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    //十位
                    //long todayNowTimeL = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))+000;
                    long now = System.currentTimeMillis();
                    do {
                        FxkGetApprovalInstanceListReqDTO build = FxkGetApprovalInstanceListReqDTO.builder()
                                .corpId(corpId)
                                .corpAccessToken(corpAccessToken.getCorpAccessToken())
                                .pageNumber(offset)
                                .pageSize(pageSize)
                                .state(FxiaokeApprovalStatus.PASS.getKey())
                                .currentOpenUserId(apiUserId)
                                //时间可以设置为查询当天数据.只要是当天通过的审批数据
                                .startTime(todayBeginTimeL)
                                .endTime(now)
                                .flowApiName(flowApiName)
                                .build();
                        //根据条件过滤分贝通需要的审批数据
                        instanceList = iFxkApprovalInstanceService.getInstanceList(build);
                        fxkApprovalInstances.addAll(instanceList.getQueryResult().getInstanceList());
                        log.info("获取纷享销客申请用车数据：{}", instanceList);
//                        offset += pageSize;
                        offset++;
                    } while (instanceList != null && instanceList.getErrorCode() == 0 && !ObjectUtils.isEmpty(instanceList.getQueryResult().getInstanceList()));
                    return fxkApprovalInstances;
                }
            }
            log.info("获取纷享销客access_token 失败");
        }
        return null;
    }

    /**
     * @param corpId
     * @return
     */
    public String pullFxkApprovalData(String corpId, int applyType) {
        //使用用车审批对象apiname
        PluginCorpDefinition corpByThirdCorpId = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (!ObjectUtils.isEmpty(corpByThirdCorpId)) {
            //根据公司ID查询表，查找对应的用车审批apiname
            Example example = new Example(ThirdApplyDefinition.class);
            example.createCriteria()
                    .andEqualTo("appId", corpByThirdCorpId.getAppId())
                    .andEqualTo("processType", applyType);
            ThirdApplyDefinition thirdApplyDefinition = thirdApplyDefinitionDao.getByExample(example);
            if (!ObjectUtils.isEmpty(thirdApplyDefinition)) {
                //获取用车审批的列表，只查询当天
                List<FxkApprovalInstance> instanceList = getFxkApprovalList(corpId, thirdApplyDefinition.getThirdProcessCode(), corpByThirdCorpId.getThirdAdminId());
                if (!ObjectUtils.isEmpty(instanceList)) {//非空
                    List<FxiaokeTask> fxiaokeCarApprovalList = Lists.newArrayList();
                    instanceList.stream().forEach(instant -> {
                        FxiaokeTask fxiaokeTask = new FxiaokeTask();
                        fxiaokeTask.setCorpId(corpId);
                        if (1 == applyType) {//差旅
                            fxiaokeTask.setTaskType(FxiaokeApprovalType.TRIP_APPROVAL.getKey());
                        } else {
                            fxiaokeTask.setTaskType(FxiaokeApprovalType.CAR_APPROVAL.getKey());
                        }
                        String triggerType = instant.getTriggerType();
                        if (triggerType.equals("Create")) {
                            fxiaokeTask.setDataType(FxiaokeApprovalTriggerType.APPROVAL_CREATE.getValue());
                        } else if (triggerType.equals("Update")) {
                            fxiaokeTask.setDataType(FxiaokeApprovalTriggerType.APPROVAL_UPDATE.getValue());
                        } else if (triggerType.equals("Delete")) {
                            fxiaokeTask.setDataType(FxiaokeApprovalTriggerType.APPROVAL_DELETE.getValue());
                        }
                        fxiaokeTask.setDataId(instant.getDataId());
                        fxiaokeTask.setCreateTime(new Date());
                        fxiaokeTask.setUpdateTime(new Date());
                        //默认执行三次，超出三次后丢弃
                        fxiaokeTask.setExecuteMax(3);
                        fxiaokeTask.setPriority(0);
                        fxiaokeTask.setExecuteNum(0);
                        fxiaokeTask.setState(0);
                        //根据审批单ID查询分贝通是否已经存在，如果存在则不需要存储
                        ArrayList<String> taskTypes = null;
                        if (1 == applyType) {
                            taskTypes = Lists.newArrayList(FxiaokeApprovalType.TRIP_APPROVAL.getKey());
                        } else {
                            taskTypes = Lists.newArrayList(FxiaokeApprovalType.CAR_APPROVAL.getKey());
                        }
                        //查询fxiaoke_task表里是否存在
                        List<FxiaokeTask> fxiaokeTasks = fxiaokeTaskDao.listFxiaokeUpdateOrAddTaskWithCondition(corpId, instant.getDataId(), taskTypes);
                        //查询历史表里是否存在，如果存在则不进行添加操作,根据ID，类型，动作类型查询
                        if (ObjectUtils.isEmpty(fxiaokeTasks)) {//不存在时进行数据存储
                            fxiaokeCarApprovalList.add(fxiaokeTask);
                        }
                    });
                    //数据入库操作
                    try {
                        if (!ObjectUtils.isEmpty(fxiaokeCarApprovalList)) {
                            fxiaokeTaskDao.saveList(fxiaokeCarApprovalList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("入库失败 {}", fxiaokeCarApprovalList);
                    }

                    return "success";
                }
                log.info("拉取企业用车审批数据为空 {}", instanceList);
            }
        }
        return null;

    }
}
