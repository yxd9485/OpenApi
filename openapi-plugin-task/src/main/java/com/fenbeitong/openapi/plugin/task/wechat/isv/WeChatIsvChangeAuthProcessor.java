package com.fenbeitong.openapi.plugin.task.wechat.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
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
public class WeChatIsvChangeAuthProcessor extends AbstractTaskProcessor {

    @Autowired
    private WeChatIsvPullThirdOrgService weChatIsvPullThirdOrgService;

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Override
    public Integer getTaskType() {
        return TaskType.WECHAT_ISV_CHANGE_AUTH.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        String corpId = task.getCompanyId();
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_COMPANY_UNDEFINED));
        }
        weChatIsvPullThirdOrgService.pullThirdOrg(corpId);
        return TaskProcessResult.success("success");
    }

}
