package com.fenbeitong.openapi.plugin.definition.controller.enforcement;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 运维实施首页
 * Created by log.chang on 2020/2/19.
 */
@Controller
@RequestMapping("/definitions/plugin/html")
public class EnforcementMainController {


    @RequestMapping("/index")
    public String test(Map<String, Object> paramMap) {
//        paramMap.put("name", "张三");
//        paramMap.put("age", 28);
        return "index";
    }


}
