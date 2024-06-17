package com.fenbeitong.openapi.plugin.func.deprecated.controller;

import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseResultEntity;
import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseUtils;
import com.fenbeitong.openapi.plugin.func.deprecated.service.OpenJavaService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/12 20:43
 * @since 2.0
 */

@RestController
@RequestMapping("/open/web")
public class OpenApiWebController {

    @Autowired
    private OpenJavaService openJavaService;


    @RequestMapping("/auth/register")
    @ApiOperation(value = "鉴权接口")
    public OpenResponseResultEntity<?> register(HttpServletRequest request) {
        return OpenResponseUtils.success(openJavaService.getRegister(request));
    }


    @RequestMapping("/auth/v1/dispense")
    @ApiOperation(value = "登录鉴权,分发Token")
    public OpenResponseResultEntity<?> auth(HttpServletRequest request) {
        return OpenResponseUtils.success(openJavaService.getAuth(request));
    }



}
