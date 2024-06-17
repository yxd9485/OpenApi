package com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkApplyServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dao.DingtalkYidaCorpDao;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaCallbackDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.entity.DingtalkYidaCorp;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaCallbackService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.ITaskService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lizhen
 * @date 2020/7/9
 */
@Service
@Slf4j
public class YiDaCallbackServiceImpl implements IYiDaCallbackService {

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Autowired
    private DingtalkApplyServiceImpl dingtalkApplyService;

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private ITaskService taskService;

    @Autowired
    private DingtalkYidaCorpDao dingtalkYidaCorpDao;

    @Override
    public void callbackCommand(YiDaCallbackDTO callbackParam) {
        String corpId = callbackParam.getCorpId();
        DingtalkYidaCorp dingtalkYidaCorpByCorpId = dingtalkYidaCorpDao.getDingtalkYidaCorpByCorpId(corpId);
        if (dingtalkYidaCorpByCorpId == null) {
            log.info("易搭回调企业不存在，corpId：{}", corpId);
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.CORP_UNINITIALIZED));

        }
        initGenProcessTask(callbackParam);
    }

    /**
     * 三方应用审批
     */
    private void initGenProcessTask(YiDaCallbackDTO callbackParam) {
        String corpId = callbackParam.getCorpId();
        String instanceId = callbackParam.getFormInstId();
        Long eventTime = NumericUtils.obj2long(callbackParam.getCreateTime());
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", TaskType.YIDA_BPMS_INSTANCE_CHANGE.getKey());
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", instanceId);
        eventMsg.put("DataContent", JsonUtils.toJson(callbackParam));
        taskService.genTask(eventMsg, Lists.newArrayList(TaskType.YIDA_BPMS_INSTANCE_CHANGE.getKey()));
    }

}
