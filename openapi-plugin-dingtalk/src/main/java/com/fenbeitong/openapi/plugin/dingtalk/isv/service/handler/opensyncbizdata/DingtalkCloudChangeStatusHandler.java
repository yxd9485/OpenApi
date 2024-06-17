package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdata;

import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkCallbackTagConstant;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizData;
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
public class DingtalkCloudChangeStatusHandler implements IOpenSyncBizDataTaskHandler {

    @Autowired
    IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Override
    public OpenSyncBizDataType getTaskType() {
        return OpenSyncBizDataType.DINGTALK_ISV_CHANGE_STATUS;
    }

    @Override
    public TaskResult execute(OpenSyncBizData task) {
        String corpId = task.getCorpId();
        String bizData = task.getBizData();
        Map<String, Object> dataMap = JsonUtils.toObj(bizData, Map.class);
        String syncAction = StringUtils.obj2str(dataMap.get("syncAction"));
        // 微应用启用
        if (DingtalkCallbackTagConstant.ORG_MICRO_APP_RESTORE.equals(syncAction)) {
            dingtalkIsvCompanyAuthService.updateCompanyAuth( corpId );
//            dingtalkIsvCompanyAuthService.companyAuth(corpId);
        } else if (DingtalkCallbackTagConstant.ORG_MICRO_APP_STOP.equals(syncAction)) {
            // 微应用停用
            dingtalkIsvCompanyAuthService.companyCancelAuth(corpId);
        } else if (DingtalkCallbackTagConstant.ORG_MICRO_APP_REMOVE.equals(syncAction)) {
            //微应用删除，保留企业对套件的授权
            dingtalkIsvCompanyAuthService.companyCancelAuth(corpId);
        }
        return TaskResult.SUCCESS;
    }

}
