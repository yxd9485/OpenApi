package com.fenbeitong.openapi.plugin.demo.controller;

import com.fenbeitong.openapi.plugin.core.entity.KvEntity;
import com.fenbeitong.openapi.plugin.demo.dto.ValidatedDemoDTO;
import com.fenbeitong.openapi.plugin.demo.service.DbDemoService;
import com.fenbeitong.openapi.plugin.demo.service.OpenApiSdkTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * demo
 * Created by log.chang on 2019/11/26.
 */
@Controller
@Slf4j
@RequestMapping("demo/test")
public class DemoController {
    @Autowired
    private OpenApiSdkTestService openApiSdkTestService;
    @Autowired
    private DbDemoService dbDemoService;

    @GetMapping("")
    public Object getTest(@RequestParam("i1") int i, Optional<Integer> optInt, Integer i2) {
        log.info("get请求测试参数：");
        log.info("非空参数i={}", i);
        log.info("可空参数OptInt={}", optInt);
        log.info("可空参数i2={}", i2);
        Map<String, Object> res = new HashMap<>();
        res.put("code", "200");
        res.put("msg", "Hello World  !");
        return res;
    }

    @PostMapping("")
    @ResponseBody
    public Object postTest(KvEntity kvEntity) {
        log.info("post请求测试参数kvEntity={}", kvEntity);
        Map<String, Object> res = new HashMap<>();
        res.put("code", "200");
        res.put("msg", "Hello World  !");
        return res;
    }

    @PostMapping("openapiSdkTest")
    @ResponseBody
    public Object openApiSdkTest(@RequestBody com.fenbeitong.openapi.sdk.dto.common.KvEntity kvEntity) throws Exception {
        return openApiSdkTestService.openApiSdkTest(kvEntity);
    }

    @PostMapping("dbTest")
    @ResponseBody
    public Object dbTest() {
        return dbDemoService.testDao();
    }

    @PostMapping("validatedTest")
    @ResponseBody
    public Object validatedTest(@Validated({ValidatedDemoDTO.Group1.class}) ValidatedDemoDTO req) {
        log.info("req is {}", req);
        return "Hello World!";
    }
}
