package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.NingBoWeiLiOrgService;
import com.fenbeitong.openapi.plugin.support.organization.dto.JobConfigDto;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CompletableFuture;

/**
 * <p>Title: TalkOrganizationController</p>
 * <p>Description: 宁波伟立组织架构同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:28
 */

@Controller
@Slf4j
@RequestMapping("/ningBoWeiLi/sync")
public class NingBoWeiLiOrgController {

    @Autowired
    NingBoWeiLiOrgService hyprocaOrgService;


    /**
     * @Description 组织架构信息全量同步
     * @Author duhui
     * @Date 2020-08-31
     **/
    @RequestMapping("/allSyncOrganization")
    @ResponseBody
    public Object syncOrganization(@RequestParam("jobConfig") String jobConfig) {
        JobConfigDto jobConfigDto = JsonUtils.toObj(jobConfig, JobConfigDto.class);
        CompletableFuture.supplyAsync(() -> hyprocaOrgService.allSync(jobConfigDto.getCompanyId(), jobConfigDto.getTopId())).exceptionally(e -> {
            log.warn("", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
