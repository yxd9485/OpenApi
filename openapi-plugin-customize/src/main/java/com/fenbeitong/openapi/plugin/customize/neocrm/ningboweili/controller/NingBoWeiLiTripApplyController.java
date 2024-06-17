package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.controller;

import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto.NingBoWeiLIJobConfigDto;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.impl.NingBoWeiLiTripApplyJobServiceImpl;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.impl.NingBoWeiLiTripApplyPullServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>Title: TalkOrganizationController</p>
 * <p>Description: 宁波伟立审批单同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:28
 */

@Controller
@Slf4j
@RequestMapping("/ningboweili/tripApply/sync")
public class NingBoWeiLiTripApplyController {
    @Autowired
    NingBoWeiLiTripApplyJobServiceImpl ningBoWeiLiTripApplyJobService;

    @Autowired
    NingBoWeiLiTripApplyPullServiceImpl ningBoWeiLiTripApplyPullService;


    /**
     * 审批单同步执行
     */
    @RequestMapping("/pull")
    @ResponseBody
    public String tripApplyPull(@RequestParam("jobConfig") String jobConfig) {
        NingBoWeiLIJobConfigDto ningBoWeiLIJobConfigDto = JsonUtils.toObj(jobConfig, NingBoWeiLIJobConfigDto.class);
        ningBoWeiLiTripApplyPullService.tripApplyPull(ningBoWeiLIJobConfigDto);
        return "ok";
    }

    /**
     * 审批单同步执行
     */
    @RequestMapping("/execute")
    @ResponseBody
    public String executeTask() {
        ningBoWeiLiTripApplyJobService.start();
        return "ok";
    }


}
