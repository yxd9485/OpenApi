package com.fenbeitong.openapi.plugin.qiqi.service.addjob.impl;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.sqs.model.Message;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiFinhubTaskDTO;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiMessageBodyDTO;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.qiqi.process.QiqiProcessAddSync;
import com.fenbeitong.openapi.plugin.qiqi.process.QiqiProcessAddSyncFactory;
import com.fenbeitong.openapi.plugin.qiqi.service.AbstractQiqiSqsReceiveMessageService;
import com.fenbeitong.openapi.plugin.qiqi.service.addjob.IQiqiDataAddJobService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskDataSrc;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.IFinhubTaskService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.q7link.openapi.model.Queue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName QiqiDataAddJobServiceImpl
 * @Description 企企增量数据同步实现类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/06/07
 **/
@Service
@Slf4j
public class QiqiDataAddJobServiceImpl implements IQiqiDataAddJobService {
    @Autowired
    private QiqiCorpInfoDao qiqiCorpInfoDao;
    @Autowired
    private IFinhubTaskService finhubTaskService;
    @Value("${finhub.task.src}")
    private String taskSrc;

    @Override
    @Async
    public void syncAddData(String companyId) throws Exception {
        log.info("【qiqi】 syncAddData, 任务[{}]开始执行", companyId);
        //查询所有企业授权配置
        QiqiCorpInfo corpInfo = qiqiCorpInfoDao.getCorpIdByCompanyId(companyId);
        String accessKeyId = corpInfo.getAccessKeyId();
        String secret = corpInfo.getSecretAccessKey();
        String openId = corpInfo.getOpenId();

        // 通过openapi queue接口获取queueUrl
        Queue queue = AbstractQiqiSqsReceiveMessageService.getQueue(accessKeyId, secret, openId);
        String queueUrl = queue.getQueueUrl();
        log.info("消息队列地址,queueUrl:"+queueUrl);
        // 接收消息
        List<Message> messages = AbstractQiqiSqsReceiveMessageService.receiveMessages(queueUrl, accessKeyId, secret);
        for (Message message : messages) {
            // 消息体
            String body = message.getBody();
            try {
                // 消费消息
                log.info("【qiqi】 增量同步消息内容：" + body);
                addSyncTaskHandler(body, openId);
            } catch (Exception e) {
                log.info("【qiqi】 增量同步入库任务[{}]执行失败, result: {}", corpInfo.getId(), e);
                throw new OpenApiQiqiException(QiqiResponseCode.DATA_ADD_JOB_ERROR);
            }
            // 删除消息
            AbstractQiqiSqsReceiveMessageService.amazonSQS(accessKeyId, secret).deleteMessage(queueUrl, message.getReceiptHandle());
        }
    }

    /**
     * 增量同步数据处理
     */
    public void addSyncTaskHandler(String data, String corpId) throws Exception {
        QiqiMessageBodyDTO dataMap = JSON.parseObject(data, QiqiMessageBodyDTO.class);
        //创建人
        String createId = dataMap.getVariables().getLastUserId().get("value");
        if (StringUtils.isBlank(dataMap.getObjectName())) {
            log.info("【qiqi】 addSyncTaskHandler, objectName为空");
            throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
        }
        //操作类型处理
        QiqiProcessAddSync processAddSync = QiqiProcessAddSyncFactory.getByObjectName(dataMap.getObjectName());
        TaskType taskType = processAddSync.addSync(dataMap.getOperation(), corpId);
        //数据封装，存入finhub_task表
        saveFinhubTask(QiqiFinhubTaskDTO.builder()
            .objectId(dataMap.getObjectId())
            .corpId(corpId)
            .createId(createId)
            .taskType(taskType)
            .dataMap(dataMap).build());
    }

    /**
     * 保存finhub_task表
     *
     * @param qiqiFinhubTaskDto
     */
    public void saveFinhubTask(QiqiFinhubTaskDTO qiqiFinhubTaskDto) {
        FinhubTask finhubTask = new FinhubTask();
        finhubTask.setTaskSrc(taskSrc);
        finhubTask.setTaskCategory(TaskDataSrc.PULL.getKey());
        finhubTask.setTaskType(qiqiFinhubTaskDto.getTaskType().getCode());
        finhubTask.setTaskName(qiqiFinhubTaskDto.getTaskType().getKey() + "(" + qiqiFinhubTaskDto.getTaskType().getValue() + ")");
        finhubTask.setDataId(qiqiFinhubTaskDto.getObjectId());
        finhubTask.setCompanyId(qiqiFinhubTaskDto.getCorpId());
        finhubTask.setDataContent(JsonUtils.toJson(qiqiFinhubTaskDto.getDataMap()));
        finhubTask.setCreateId(qiqiFinhubTaskDto.getCreateId());
        finhubTask.setExecuteMax(3);
        finhubTaskService.saveTask(finhubTask);
    }
}
