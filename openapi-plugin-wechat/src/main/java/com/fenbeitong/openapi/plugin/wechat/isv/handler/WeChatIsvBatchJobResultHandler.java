package com.fenbeitong.openapi.plugin.wechat.isv.handler;

import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.wechat.isv.dao.WechatIsvContactTranslateDao;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WechatIsvContactTranslate;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author lizhen
 * @date 2020/9/24
 */
@Component
@Slf4j
public class WeChatIsvBatchJobResultHandler implements ITaskHandler {

    @Autowired
    private WechatIsvContactTranslateDao wechatIsvContactTranslateDao;

    @Autowired
    private WeChatIsvTransferService weChatIsvTransferService;

    @Override
    public TaskType getTaskType() {
        return TaskType.WECHAT_ISV_BATCH_JOB_RESULT;
    }

    @Override
    public TaskResult execute(Task task) {
        String dataId = task.getDataId();
        WechatIsvContactTranslate wechatIsvContactTranslate = wechatIsvContactTranslateDao.getJobId(dataId);
        if (wechatIsvContactTranslate != null) {
            weChatIsvTransferService.callbackTranslateResult(wechatIsvContactTranslate);
        } else {
            return TaskResult.EXPIRED;
        }
        return TaskResult.SUCCESS;
    }

}
