package com.fenbeitong.openapi.plugin.demo.service;

import com.fenbeitong.openapi.sdk.dto.common.KvEntity;
import com.fenbeitong.openapi.sdk.webservice.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import retrofit2.Response;

/**
 * OpenapiSdk测试
 * Created by log.chang on 2019/12/2.
 */
@ServiceAspect
@Service
@Slf4j
public class OpenApiSdkTestService {

    @Autowired
    private TestService testService;

    public Object openApiSdkTest(KvEntity kvEntity) throws Exception {
        Response<Object> resp = testService.hello(kvEntity).execute();
        return resp.body();
    }

}
