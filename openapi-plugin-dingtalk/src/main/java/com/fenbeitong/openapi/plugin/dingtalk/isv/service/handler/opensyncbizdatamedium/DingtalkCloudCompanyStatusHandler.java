package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdatamedium;

import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkCallbackTagConstant;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataMediumType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author lizhen
 * @date 2020/7/15
 */
@Component
@Slf4j
public class DingtalkCloudCompanyStatusHandler implements IOpenSyncBizDataMediumTaskHandler {

    @Autowired
    IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Override
    public OpenSyncBizDataMediumType getTaskType() {
        return OpenSyncBizDataMediumType.DINGTALK_ISV_COMPANY_STATUS_CHANGE;
    }

    @Override
    public TaskResult execute(OpenSyncBizDataMedium task) {
        String corpId = task.getCorpId();
        String bizData = task.getBizData();
        Map<String, Object> dataMap = JsonUtils.toObj(bizData, Map.class);
        String syncAction = StringUtils.obj2str(dataMap.get("syncAction"));
        // 企业删除
        if (DingtalkCallbackTagConstant.ORG_REMOVE.equals(syncAction)) {
            dingtalkIsvCompanyAuthService.companyCancelAuth(corpId);
        } else {
            log.info("钉钉三方企业信息变更，corpId:{}", corpId);
        }
        return TaskResult.SUCCESS;
    }

}
