package com.fenbeitong.openapi.plugin.feishu.isv.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.event.schedule.dto.ScheduleMsgDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuNoPermissionException;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvScheduleService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 飞书市场版同步日程信息
 *
 * @author xiaohai
 * @date 2021/12/26
 */
@Controller
@RequestMapping("/feishu/isv/schedule")
public class FeishuIsvPushScheduleController {

    @Autowired
    private FeiShuIsvScheduleService feiShuIsvScheduleService;

    @PostMapping
    @ResponseBody
    public Object pushSchedule(@RequestBody String data) {
        ScheduleMsgDTO schedule = JsonUtils.toObj(data, ScheduleMsgDTO.class);
        return feiShuIsvScheduleService.parseScheduleInfo(schedule);
    }

}
