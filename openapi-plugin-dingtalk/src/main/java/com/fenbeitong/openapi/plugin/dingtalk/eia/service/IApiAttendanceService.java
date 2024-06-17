package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import java.util.List;

import static com.dingtalk.api.response.OapiAttendanceListResponse.Recordresult;

/**
 * <p>Title: IApiAttendanceService</p>
 * <p>Description: 钉钉考勤服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 2:50 PM
 */
public interface IApiAttendanceService {

    /**
     * 获取考勤记录
     *
     * @param workDateFrom 查询考勤打卡记录的起始工作日 格式为“yyyy-MM-dd HH:mm:ss”
     * @param workDateTo   查询考勤打卡记录的结束工作日 格式为“yyyy-MM-dd HH:mm:ss”
     * @param corpId       钉钉企业id
     * @param userIdList   钉钉用户id
     * @return 用户考勤列表
     */
    List<Recordresult> getAttendanceList(String workDateFrom, String workDateTo, String corpId, List<String> userIdList);
}
