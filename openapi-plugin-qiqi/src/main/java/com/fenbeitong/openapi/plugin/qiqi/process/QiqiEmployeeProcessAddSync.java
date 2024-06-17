package com.fenbeitong.openapi.plugin.qiqi.process;

import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.constant.OperationTypeEnum;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName QiqiEmployeeProcessAddSync
 * @Description 企企人员增量同步实现类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/24
 **/
@Component
@Slf4j
public class QiqiEmployeeProcessAddSync implements QiqiProcessAddSync {

    @Override
    public TaskType addSync(String operation, String corpId) throws Exception {
        TaskType taskType = null;
        if (OperationTypeEnum.CREATE.getCode().equals(operation)) {
            taskType = TaskType.parse(TaskType.QIQI_SYNC_USER_ADD.getKey());
        } else if (OperationTypeEnum.UPDATE.getCode().equals(operation)) {
            taskType = TaskType.parse(TaskType.QIQI_SYNC_USER_UPDATE.getKey());
        } else if (OperationTypeEnum.DELETE.getCode().equals(operation)) {
            taskType = TaskType.parse(TaskType.QIQI_SYNC_USER_DELETE.getKey());
        } else {
            log.info("【qiqi】 addSync, 业务类型不存在，corpId={}", corpId);
            throw new OpenApiQiqiException(QiqiResponseCode.TYPE_NOT_EXIST);
        }
        return taskType;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        QiqiProcessAddSyncFactory.register(ObjectTypeEnum.USER.getCode(), this);
    }
}
