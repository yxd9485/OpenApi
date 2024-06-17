package com.fenbeitong.openapi.plugin.daoyiyun.service.impl;

import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunReqDTO;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.daoyiyun.constant.DaoYiYunConstant;
import com.fenbeitong.openapi.plugin.daoyiyun.dao.DaoyiyunCorpDao;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunCallbackBodyDTO;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunCallbackReqDTO;
import com.fenbeitong.openapi.plugin.daoyiyun.entity.DaoyiyunCorp;
import com.fenbeitong.openapi.plugin.daoyiyun.service.DaoYiYunCallbackService;
import com.fenbeitong.openapi.plugin.daoyiyun.util.EncryptUtils;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.ITaskService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 回调
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class DaoYiYunCallbackServiceImpl implements DaoYiYunCallbackService {

    @Autowired
    private DaoyiyunCorpDao daoyiyunCorpDao;

    @Autowired
    private ThirdApplyDefinitionDao applyDefinitionDao;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private ITaskService taskService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String revice( DaoYiYunCallbackReqDTO daoYiYunCallbackReqDTO ) {
        //验证applicationId是否存在
        String applicationId = daoYiYunCallbackReqDTO.getApplicationId();
        DaoyiyunCorp corpConfigInfo = daoyiyunCorpDao.getByApplicationId(applicationId);
        String eventType = daoYiYunCallbackReqDTO.getEventType();
        String result = null;
        switch (eventType) {
            //url验证
            case DaoYiYunConstant.EVNET_TYPE_URL_VERIFY:
                result = verifyUrl(daoYiYunCallbackReqDTO, corpConfigInfo);
                break;
            case DaoYiYunConstant.EVENT_TYPE_FORM_DATA_MODIFY:
                //表单数据更新
                saveFormData(daoYiYunCallbackReqDTO, corpConfigInfo );
                break;
            default:
                log.info("无法识别的回调类型{}", eventType);
                break;
        }
        return result;
    }

    private String verifyUrl(DaoYiYunCallbackReqDTO daoYiYunCallbackReqDTO, DaoyiyunCorp corpConfigInfo) {
        String encrypt = null;
        try {
            encrypt = EncryptUtils.aesEncrypt(daoYiYunCallbackReqDTO.getData(), corpConfigInfo.getToken());
        } catch (Exception e) {
            log.info("aes加密异常", e);
        }
        return encrypt;
    }

    /**
     * 保存表单数据
     *
     * @param daoYiYunCallbackReqDTO
     * @param corpConfigInfo
     */
    private void saveFormData(DaoYiYunCallbackReqDTO daoYiYunCallbackReqDTO, DaoyiyunCorp corpConfigInfo) {
        String data = EncryptUtils.aesDecrypt(daoYiYunCallbackReqDTO.getData(), corpConfigInfo.getToken());
        log.info("接收到回调信息:{}", data);
        OpenMsgSetup openMsgSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(corpConfigInfo.getCompanyId(), DaoYiYunConstant.APPROVE_ITEM_CODE);
        if(openMsgSetup == null || StringUtils.isBlank(openMsgSetup.getStrVal1())){
            log.info("配置信息有误，请联系管理员！");
            return;
        }
        DaoYiYunReqDTO daoYiYunReqDTO = JsonUtils.toObj(openMsgSetup.getStrVal1(), DaoYiYunReqDTO.class);
        if(daoYiYunReqDTO == null ){
            log.info("配置信息有误，请联系管理员！");
            return;
        }
        String isApprovedKey = daoYiYunReqDTO.getIsApprovedKey();
        String isApprovedValue = daoYiYunReqDTO.getIsApprovedValue();
        String isCancelValue = daoYiYunReqDTO.getIsCancelValue();
        String notNullKey = daoYiYunReqDTO.getNotNullKey();
        String mainApplicationIdKey = daoYiYunReqDTO.getMainApplicationIdKey();
        daoYiYunCallbackReqDTO.setData(data);
        List<DaoYiYunCallbackBodyDTO> daoYiYunCallbackBodyDTOList = JsonUtils.toObj(data,
            new TypeReference<List<DaoYiYunCallbackBodyDTO>>() {
            });
        if (ObjectUtils.isEmpty(daoYiYunCallbackBodyDTOList)) {
            log.info("回调data数据为空");
            return;
        }

        for (DaoYiYunCallbackBodyDTO daoYiYunCallbackBodyDTO : daoYiYunCallbackBodyDTOList) {
            // 根据传递的条件过滤通过的单据
            Map<String, Object> variables = daoYiYunCallbackBodyDTO.getVariables();
            if (ObjectUtils.isEmpty(variables)) {
                log.info("variables 为空，无法判定单据状态");
                continue;
            }
            //判断状态是审核通过的或者是审批终止的
            String applyStatus = StringUtils.obj2str(MapUtils.getValueByExpress(variables, isApprovedKey));
            String mainApplicationId = StringUtils.obj2str(MapUtils.getValueByExpress(variables, mainApplicationIdKey));
            String applyType = "";
            if (!StringUtils.isBlank(isApprovedKey) && !StringUtils.isBlank(isApprovedValue) && !StringUtils.isBlank(isCancelValue)) {
                if (isApprovedValue.equals(applyStatus) && StringUtils.isBlank(mainApplicationId) ) {
                    //新增
                    applyType = TaskType.DAOYIYUN_APPLY_CREATE.getKey();
                } else if(isApprovedValue.equals(applyStatus) && !StringUtils.isBlank(mainApplicationId) ){
                    //修改
                    applyType = TaskType.DAOYIYUN_APPLY_UPDATE.getKey();
                } else if(isCancelValue.equals(applyStatus)){
                    //作废
                    applyType = TaskType.DAOYIYUN_APPLY_CANCEL.getKey();
                }else{
                    log.info("isApprovedValue || isCancelValue 与报文中isApprovedKey对应结果不符，未通过。applyStatus:{}", applyStatus);
                    continue;
                }
            }else {
                log.info("配置信息有误，请联系管理员！");
                continue;
            }

            daoYiYunCallbackBodyDTO.setMainApplicationIdKey( mainApplicationIdKey );
            if (!StringUtils.isBlank(notNullKey) && !isCancelValue.equals(applyStatus)) {
                Object notNullValue = MapUtils.getValueByExpress(variables, notNullKey);
                //审批通过的单子行程信息不能为空，审批终止单子行程信息可以为空
                if (ObjectUtils.isEmpty(notNullValue) || StringUtils.isBlank(isCancelValue)) {
                    log.info("审批通过，notNullKey值为空，非完整表单数据，跳过");
                    continue;
                }
            }
            //表单定义ID
            String formDefinitionId = daoYiYunCallbackBodyDTO.getFormDefinitionId();
            if (StringUtils.isBlank(formDefinitionId)) {
                log.info("非分贝通审批单,formDefinitionId为空");
                continue;
            }
            ThirdApplyDefinition applyDefinition =
                applyDefinitionDao.getThirdApplyConfigByProcessCode(formDefinitionId);
            if (applyDefinition == null) {
                log.info("非分贝通审批单,formDefinitionId{}", formDefinitionId);
                continue;
            }
            //实例ID
            String processInstanceId = daoYiYunCallbackBodyDTO.getId();
            String lastModifyDate = StringUtils.obj2str(daoYiYunCallbackBodyDTO.getLastModifyDate());
            String lockKey =
                MessageFormat.format(DaoYiYunConstant.REDIS_KEY_APPLY, applyDefinition.getAppId(), processInstanceId , applyType);
            Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, 1800000L);
            if (lockTime > 0) {
                try {
                    saveApply(StringUtils.obj2str( applyType ), applyDefinition.getAppId(), lastModifyDate,
                        processInstanceId, JsonUtils.toJson(daoYiYunCallbackBodyDTO));
                } catch (Exception e) {
                    //有重复推送的情况，排重，锁30分钟，如果异常时释放锁。
                    RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
                    log.error("数据存储异常", e);
                    throw new OpenApiArgumentException("数据存储异常");
                }
            } else {
                log.info("未获取到锁或数据已存在，companyId:{},dataId:{}", applyDefinition.getAppId(), processInstanceId);
            }
        }
    }

    private void saveApply(String eventType, String corpId, String lastModifyDate, String dataId, String dataContent) {
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", lastModifyDate);
        eventMsg.put("DataId", dataId);
        eventMsg.put("DataContent", dataContent);
        taskService.genTask(eventMsg, null);
    }

}
