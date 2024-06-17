package com.fenbeitong.openapi.plugin.demo.controller;

import com.fenbeitong.openapi.plugin.demo.service.AsyncDemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.IntStream;

/**
 * 异步demo
 * Created by log.chang on 2019/11/26.
 */
@Controller
@Slf4j
@RequestMapping("demo/async")
public class AsyncDemoController {

    @Autowired
    private AsyncDemoService asyncDemoService;

    /**
     * 可配置异步执行逻辑
     */
    @PostMapping("")
    @ResponseBody
    public Object asyncTest() {
        log.info("进入异步测试控制器...");
        IntStream.range(0, 100).forEach((i) -> asyncDemoService.asyncLogic());
        log.info("异步测试控制器结束...");
        return "Hello World!";
    }

}
