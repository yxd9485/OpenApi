package com.fenbeitong.openapi.plugin.func.deprecated.controller;

import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseResultEntity;
import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseUtils;
import com.fenbeitong.openapi.plugin.func.deprecated.service.OpenApiJavaSecondService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * module: 迁移openapi-java项目二期<br/>
 * <p>
 * description: openapi-java对外接口<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/3 17:19
 * @since 1.0
 */
@RestController
@RequestMapping("/open/api")
public class OpenApiJavaSecondController {

    @Autowired
    private OpenApiJavaSecondService openApiJavaSecondService;

    @FuncAuthAnnotation
    @RequestMapping("/train/order/detail")
    @ApiOperation(value = "查询火车详情")
    public OpenResponseResultEntity<?> trainOrderDetail(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaSecondService.queryTrainOrderDetail(httpRequest, request));
    }

    @FuncAuthAnnotation
    @RequestMapping("/common/order_param")
    @ApiOperation(value = "根据订单ID查询下单时关联的自定义字段")
    public OpenResponseResultEntity<?> orderParam(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaSecondService.queryOrderParam(httpRequest, request));
    }

    @FuncAuthAnnotation
    @RequestMapping("/third/company/rule")
    @ApiOperation(value = "查询公司相关规则")
    public OpenResponseResultEntity<?> companyRule(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaSecondService.queryCompanyRule(httpRequest, request));
    }

    @FuncAuthAnnotation
    @RequestMapping("/third/company/role")
    @ApiOperation(value = "根据公司ID查询公司相关权限")
    public OpenResponseResultEntity<?> companyRole(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaSecondService.queryCompanyRole(httpRequest, request));
    }


    @RequestMapping("/common/national")
    @ApiOperation(value = "查询国籍信息")
    public OpenResponseResultEntity<?> nationality(HttpServletRequest httpRequest) {
        return OpenResponseUtils.success(openApiJavaSecondService.queryNationality(httpRequest));
    }

    @RequestMapping("/common/city-code")
    @FuncAuthAnnotation
    @ApiOperation(value = "查询城市编码")
    public OpenResponseResultEntity<?> cityCode(HttpServletRequest httpRequest,@Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaSecondService.queryCityCode(httpRequest,request));
    }

}
