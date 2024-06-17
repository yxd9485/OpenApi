package com.fenbeitong.openapi.plugin.feishu.eia.controller;

import com.fenbeitong.openapi.plugin.event.schedule.dto.ScheduleMsgDTO;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaScheduleService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 飞书同步日程信息
 *
 * @author xiaohai
 * @date 2021/12/26
 */
@Controller
@RequestMapping("/feishu/eia/schedule")
public class FeishuEiaPushScheduleController {

    @Autowired
    private FeiShuEiaScheduleService feiShuEiaScheduleService;

    @PostMapping
    @ResponseBody
    public Object pushSchedule(@RequestBody String data) {
        ScheduleMsgDTO schedule = JsonUtils.toObj(data, ScheduleMsgDTO.class);
        return feiShuEiaScheduleService.parseScheduleInfo(schedule);
    }

}
