package com.fenbeitong.openapi.plugin.demo.service;

import com.fenbeitong.openapi.plugin.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 异步执行逻辑
 * Created by log.chang on 2019/11/26.
 */
@ServiceAspect
@Service
@Slf4j
public class AsyncDemoService {

    @Async // 此注解可完成此方法异步执行，前提是启动类需要配置@EnableAsync
    public void asyncLogic() {
        ThreadUtils.sleep(1000);
        log.info("asyncLogic do something ...");
    }
}
