package com.fenbeitong.openapi.plugin.wechat.eia.service.attendance;

import com.fenbeitong.openapi.plugin.support.voucher.dto.CreateAttendanceReq;

import java.util.Date;
import java.util.List;

/**
 * <p>Title: WeChatAttendanceService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021-02-07 17:18
 */
public interface WeChatAttendanceService {


    /**
     * 生成本公司指定日期考勤表
     *
     * @param req      生成考勤参数
     * @param workDate 工作日
     * @throws Exception
     */
    void createAttendance(CreateAttendanceReq req, Date workDate) throws Exception;


    /**
     * 拉取钉钉考勤记录
     *
     * @param companyId          分贝公司id
     * @param onlyNormalLocation 只限内勤 1;不限制0
     * @param dateList           指定日期列表
     * @param weChatUserIdList   微信用户列表
     */
    void pullWeChatAttendance(String companyId, int onlyNormalLocation, List<Date> dateList, List<String> weChatUserIdList);
}
