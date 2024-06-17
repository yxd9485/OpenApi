package com.fenbeitong.openapi.plugin.wechat.eia.service.attendance;

import com.fenbeitong.openapi.plugin.wechat.eia.dto.attendance.WeChatAttendanceDTO;

import java.util.List;

/**
 * @Description 微信考勤服务
 * @Author duhui
 * @Date 2021-02-19
 **/
public interface WaChatApiAttendanceService {

    /**
     * 获取考勤记录
     *
     * @param opencheckindatatype
     * @param starttime           查询考勤打卡记录起始工作日 1492790400
     * @param endtime             查询考勤打卡记录的结束工作日 1492617600
     * @param companyId           公司ID
     * @param userIdlist          微信用户id
     * @return 用户考勤列表
     */
    WeChatAttendanceDTO getAttendance(int opencheckindatatype, Long starttime, Long endtime, String companyId, String[] userIdlist);

}
