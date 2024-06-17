package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.fenbeitong.openapi.plugin.support.voucher.dto.CreateAttendanceReq;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.sdk.dto.employee.GetUserByPhoneNumRespDTO;

import java.util.Date;
import java.util.List;

/**
 * <p>Title: IDingtalkAttendanceService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/17 11:52 AM
 */
public interface IDingtalkAttendanceService {

    /**
     * 获取配置加班发券的所有钉钉人员信息
     *
     * @param corpId 钉钉企业平台id
     * @return 钉钉人员列表
     */
    List<DingtalkUser> listVoucherGrantUsers(String corpId);

    /**
     * 获取配置加班发券的所有分贝人员信息
     *
     * @param corpId 钉钉企业平台id
     * @return 钉钉分贝列表
     * @throws Exception
     */
    List<GetUserByPhoneNumRespDTO> listVoucherGrantFbUsers(String corpId) throws Exception;

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
     * @param dingtalkUserIdList 钉钉用户id列表
     */
    void pullDingTalkAttendance(String companyId, String onlyNormalLocation, List<Date> dateList, List<String> dingtalkUserIdList,Boolean isCheckApprove);
}
