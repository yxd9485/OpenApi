package com.fenbeitong.openapi.plugin.customize.yuntianlifei.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.yuntianlifei.dto.YunTianJobConfigDto;
import com.fenbeitong.openapi.plugin.customize.yuntianlifei.service.YunTianOrgService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>Title: OrgPullController</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/4/26 4:56 下午
 */
@Controller
@Slf4j
@RequestMapping("/customize/yuntianlifei/sync")
public class YunTianOrgAndProjectController {
    @Autowired
    YunTianOrgService yunTianLiFeiOrgService;

    /**
     * @Description 云天厉飞组织架构同步
     * @Author duhui
     * @Date 2022/4/28
     **/
    @RequestMapping("/organization")
    @ResponseBody
    public Object syncOrganization(@RequestParam("jobConfig") String jobConfig) {
        YunTianJobConfigDto jobConfigDto = JsonUtils.toObj(jobConfig, YunTianJobConfigDto.class);
        yunTianLiFeiOrgService.orgSync(jobConfigDto);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }


    /**
     * @Description 云天厉飞项目同步
     * @Author duhui
     * @Date 2022/4/28
     **/
    @RequestMapping("/project")
    @ResponseBody
    public Object syncProject(@RequestParam("jobConfig") String jobConfig) {
        YunTianJobConfigDto jobConfigDto = JsonUtils.toObj(jobConfig, YunTianJobConfigDto.class);
        yunTianLiFeiOrgService.projectSync(jobConfigDto);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

}
