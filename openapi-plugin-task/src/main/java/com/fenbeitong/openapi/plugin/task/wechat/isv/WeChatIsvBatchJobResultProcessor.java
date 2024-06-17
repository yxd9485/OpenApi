package com.fenbeitong.openapi.plugin.task.wechat.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.wechat.isv.dao.WechatIsvContactTranslateDao;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WechatIsvContactTranslate;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lizhen
 * @date 2020/9/24
 */
@Component
@Slf4j
public class WeChatIsvBatchJobResultProcessor extends AbstractTaskProcessor {

    @Autowired
    private WechatIsvContactTranslateDao wechatIsvContactTranslateDao;

    @Autowired
    private WeChatIsvTransferService weChatIsvTransferService;

    @Override
    public Integer getTaskType() {
        return TaskType.WECHAT_ISV_BATCH_JOB_RESULT.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        String dataId = task.getDataId();
        WechatIsvContactTranslate wechatIsvContactTranslate = wechatIsvContactTranslateDao.getJobId(dataId);
        if (wechatIsvContactTranslate != null) {
            weChatIsvTransferService.callbackTranslateResult(wechatIsvContactTranslate);
        } else {
            return TaskProcessResult.success("wechatIsvContactTranslate is null success");
        }
        return TaskProcessResult.success("success");
    }

}
