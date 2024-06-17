package com.fenbeitong.openapi.plugin.definition.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessCheckFactory {
    @Autowired
    DingtalkCheckProcess dingtalkCheckProcess;
    @Autowired
    WeChatCheckProcess weChatCheckProcess;

    public ICheckProcess getProcessApply(int processType) {
        switch (processType) {
            case 1:
                return dingtalkCheckProcess;
            case 2:
                return weChatCheckProcess;
            default:
                return null;
        }
    }

}
