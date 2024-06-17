package com.fenbeitong.openapi.plugin.customize.hairou.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.hairou.dto.HaiRouProjectJobConfigDTO;
import com.fenbeitong.openapi.plugin.customize.hairou.service.HaiRouProjectService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author :zhiqiang.zhang
 * @title: HaiRouProjectController
 * @projectName openapi-plugin
 * @description: 海柔项目数据同步
 * @date 2022/5/20
 */
@Api(tags="海柔创新项目数据同步")
@Controller
@RequestMapping("/customize/hairou/sync")
public class HaiRouProjectController {

    @Autowired
    HaiRouProjectService projectSync;

    @ApiOperation(value="项目数据同步",notes="目前同步的字段：项目编码、项目名称、项目三方id")
    @ApiImplicitParam(name = "jobConfig", value = "接口相关配置,详情见具体的实体类HaiRouProjectJobConfigDTO", required = true)
    @RequestMapping("/project")
    @ResponseBody
    public Object syncProject(@RequestParam("jobConfig") String jobConfig) {
        HaiRouProjectJobConfigDTO jobConfigDto = JsonUtils.toObj(jobConfig, HaiRouProjectJobConfigDTO.class);
        projectSync.getProjectListSync(jobConfigDto);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
