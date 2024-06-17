package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.openapi.plugin.rpc.api.wechat.service.IContactTranslateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 转译
 * Created by lizhen on 2020/9/23.
 */
@Component
@DubboService(timeout = 60000)
@Slf4j
public class DubboContactTranslateService implements IContactTranslateService {

    @Autowired
    private WeChatIsvTransferService weChatIsvTransferService;

    @Override
    public void translate(String taskId, String ossKey, String companyId) {
        weChatIsvTransferService.translateContactFromOss(taskId, ossKey,companyId);
    }
}
