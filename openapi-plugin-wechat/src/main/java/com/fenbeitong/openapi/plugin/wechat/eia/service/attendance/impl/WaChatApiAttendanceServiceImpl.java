package com.fenbeitong.openapi.plugin.wechat.eia.service.attendance.impl;


import com.alibaba.fastjson.JSON;
import com.fenbeitong.openapi.plugin.support.init.enums.WaChatSecretEnum;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.wechat.eia.dto.attendance.WeChatAttendanceDTO;
import com.fenbeitong.openapi.plugin.wechat.eia.service.attendance.WaChatApiAttendanceService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WechatTokenService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * @Description
 * @Author duhui
 * @Date 2021-02-19
 **/
@Slf4j
@ServiceAspect
@Service
public class WaChatApiAttendanceServiceImpl implements WaChatApiAttendanceService {

    @Autowired
    private WechatTokenService wechatTokenService;

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Override
    public WeChatAttendanceDTO getAttendance(int opencheckindatatype, Long starttime, Long endtime, String companyId, String[] userIdlist) {
        //企业wqxc token,
        String wechatToken = wechatTokenService.getWeChatToken(companyId, WaChatSecretEnum.ATTENDANCE.getValue());
        //1.调用企业微信API获取token，
        String attendanceUrl = wechatHost + "/cgi-bin/checkin/getcheckindata?access_token=" + wechatToken;
        Map<String, Object> map = Maps.newHashMap();
        map.put("opencheckindatatype", opencheckindatatype);
        map.put("starttime", starttime);
        map.put("endtime", endtime);
        map.put("useridlist", userIdlist);
        String result = RestHttpUtils.postJson(attendanceUrl, JsonUtils.toJson(map));
        log.info("企业{}，的考勤记录：{}", companyId, result);
        WeChatAttendanceDTO weChatAttendanceDTO = JSON.parseObject(result, WeChatAttendanceDTO.class);
        log.info("企业{}，封装的考勤记录：{}", companyId, JsonUtils.toJson(weChatAttendanceDTO));
        if (!ObjectUtils.isEmpty(weChatAttendanceDTO) && 0 == weChatAttendanceDTO.getErrcode()) {
            return weChatAttendanceDTO;

        }
        return null;
    }
}
