package com.fenbeitong.openapi.plugin.fxiaoke.sdk.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@EnableAutoConfiguration
@RequestMapping("/fxiaoke/callback/receive")
public class TestCallbackController {

    @RequestMapping("/apply")
    public void receiveApply(HttpServletRequest request, @RequestParam(required = true) String eid) {

    }
}
