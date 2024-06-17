package com.fenbeitong.openapi.plugin.dingtalk.customize.controller;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.customize.service.DingTalkCustomizeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: CustomizeApi</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/1/7 12:03 下午
 */
@Slf4j
@RestController
@RequestMapping("/dingtalk/customize")
@Api(value = "api", tags = "钉钉api")
public class CustomizeController {
    @Autowired
    DingTalkCustomizeService dingTalkCustomizeService;

    /**
     * @Description 获取部门详情
     * @Author duhui
     * @Date 2022/1/7
     **/
    @RequestMapping("/get_dingtalk_dep_detail")
    @ApiOperation(value = "获取部门详情", notes = "获取部门详情", httpMethod = "GET")
    public Object getDingtalkDepDetail(String companyId, String depId) {
        return DingtalkResponseUtils.success(dingTalkCustomizeService.getDepDetail(companyId, depId));
    }
}
