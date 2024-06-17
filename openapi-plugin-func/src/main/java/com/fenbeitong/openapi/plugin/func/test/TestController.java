package com.fenbeitong.openapi.plugin.func.test;

import com.fenbeitong.openapi.plugin.core.util.EnumValidator;
import com.fenbeitong.openapi.plugin.event.core.EventBusCenter;
import com.fenbeitong.openapi.plugin.func.callback.OrderEventRockMqService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.support.callback.dto.CallBackOrderData;
import com.fenbeitong.openapi.plugin.support.util.PhoneCheckUtil;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Created by log.chang on 2019/12/17.
 */
@RestController
@RequestMapping("/func/tests")
@Api(value = "测试工具", tags = "测试工具", description = "测试工具大全")
@Slf4j
public class TestController {

    @Autowired
    private EventBusCenter eventBusCenter;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OrderEventRockMqService orderEventRockMqService;

    @PostMapping(value = "test")
    @ApiOperation("验证enum校验")
    public String testValidator(@Valid @RequestBody A a) {
        return "hello world";
    }


    @RequestMapping("testForm")
    @ApiOperation(value = "testForm", notes = "testForm", httpMethod = "POST", response = String.class)
    public String testForm(@Valid TestReq req) {
        System.out.println(JsonUtils.toJson(req));
        return "Ok";
    }

    @RequestMapping("testBody")
    @ApiOperation(value = "testBody", notes = "testBody", httpMethod = "POST", response = String.class)
    public String testBody(@Valid @RequestBody TestReq req) {
        System.out.println(JsonUtils.toJson(req));
        return "Ok";
    }

    @RequestMapping("/callbackSuccess")
    @ApiOperation(value = "回调成功", notes = "回调成功", httpMethod = "POST", response = String.class)
    public Object callbackSuccess(@RequestBody CallBackOrderData callBackOrderData) {
        log.info(JsonUtils.toJson(callBackOrderData));
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/callbackError")
    @ApiOperation(value = "回调成功", notes = "回调成功", httpMethod = "POST", response = String.class)
    public Object callbackError(@RequestBody CallBackOrderData callBackOrderData) {
        log.info(JsonUtils.toJson(callBackOrderData));
        return FuncResponseUtils.error(1, "error");
    }

    @RequestMapping("/checkMobileNum")
    @ApiOperation(value = "校验手机号", notes = "校验手机号", httpMethod = "GET", response = String.class)
    public Object checkMobileNum(@RequestParam String mobile) {
        Boolean result = PhoneCheckUtil.validMomile(mobile);
        return FuncResponseUtils.error(1, result.toString());
    }

    @RequestMapping("/testFormUrlEncode")
    public Object testFormUrlEncode(HttpServletRequest request) {
        String jsonData = request.getParameter("jsonData");
        System.out.println(jsonData);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/testOrderEvent")
    public Object testOrderEvent(@RequestBody Map data) {
        String eventClassName = (String) data.get("eventClass");
        try {
            Class<?> eventClass = Class.forName(eventClassName);
            eventBusCenter.postSync(JsonUtils.toObj(JsonUtils.toJson(data), eventClass));
        } catch (Exception e) {
        }
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/checkOrderEvent")
    public Object checkOrderEvent(@RequestBody Map data) {
        String id = (String) data.get("id");
        String collectionName = (String) data.get("collectionName");
        Map mongoData = mongoTemplate.findById(id, Map.class, collectionName);
        orderEventRockMqService.sendCheckMsg(collectionName, id, 0, NumericUtils.obj2int(mongoData.get("status")));
        return FuncResponseUtils.success(Maps.newHashMap());
    }


    @Data
    public static class TestReq {

        @NotBlank(message = "姓名不可为空")
        private String name;

        private Integer age;
    }

    @Data
    @ApiModel
    public static class A {

        @NotBlank(message = "name is null")
        private String name;

        @Valid
        @ApiModelProperty("b")
        private B b;
    }

    @Data
    @ApiModel
    public static class B {

        @EnumValidator(value = Btype.class, message = "错误的枚举值(1,2)")
        @NotNull
        @ApiModelProperty("类型")
        private Integer type;

        @NotBlank(message = "value is null'")
        @ApiModelProperty("值")
        private String value;
    }

    @SuppressWarnings("all")
    enum Btype {

        One(1),
        Two(2);

        private int value;

        Btype(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
