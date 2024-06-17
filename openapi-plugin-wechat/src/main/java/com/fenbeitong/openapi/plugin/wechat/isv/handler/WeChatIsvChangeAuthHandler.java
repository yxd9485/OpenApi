package com.fenbeitong.openapi.plugin.wechat.isv.handler;

import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvPullThirdOrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode.WECHAT_ISV_COMPANY_UNDEFINED;

/**
 * Created by lizhen on 2020/3/27.
 */
@Component
@Slf4j
public class WeChatIsvChangeAuthHandler implements ITaskHandler {

    @Autowired
    private WeChatIsvPullThirdOrgService weChatIsvPullThirdOrgService;

    @Autowired
    private  WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Override
    public TaskType getTaskType() {
        return TaskType.WECHAT_ISV_CHANGE_AUTH;
    }

    @Override
    public TaskResult execute(Task task) {
        String corpId = task.getCorpId();
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_COMPANY_UNDEFINED));
        }
        weChatIsvPullThirdOrgService.pullThirdOrg(corpId);
        return TaskResult.SUCCESS;
    }

}
