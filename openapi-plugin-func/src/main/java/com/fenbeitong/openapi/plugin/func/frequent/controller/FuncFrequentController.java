package com.fenbeitong.openapi.plugin.func.frequent.controller;

import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.frequent.service.FuncFrequentService;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 常用联系人
 */
@RestController
@RequestMapping("/func/third/frequent")
@Api(value = "常用联系人", tags = "常用联系人", description = "常用联系人")
public class FuncFrequentController {

    @Autowired
    private FuncFrequentService funcFrequentService;

    @RequestMapping("/create")
    @ApiOperation(value = "添加常用联系人", notes = "添加常用联系人", httpMethod = "POST", response = FuncResultEntity.class)
    public Object createThirdFrequent(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcFrequentService.createThirdFrequent(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/update")
    @ApiOperation(value = "修改常用联系人", notes = "修改常用联系人", httpMethod = "POST", response = FuncResultEntity.class)
    public Object updateThirdFrequent(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcFrequentService.updateThirdFrequent(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/del")
    @ApiOperation(value = "删除常用联系人", notes = "删除常用联系人", httpMethod = "POST", response = FuncResultEntity.class)
    public Object deleteThirdFrequent(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcFrequentService.deleteThirdFrequent(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/list")
    @ApiOperation(value = "查询常用联系人", notes = "查询常用联系人", httpMethod = "POST", response = FuncResultEntity.class)
    public Object listThirdFrequent(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcFrequentService.listThirdFrequent(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/get")
    @ApiOperation(value = "查询常用联系人详情", notes = "查询常用联系人详情", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getThirdFrequent(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcFrequentService.getThirdFrequent(apiRequest);
        return FuncResponseUtils.success(result);
    }

}
