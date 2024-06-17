package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import javax.servlet.http.HttpServletRequest;

/**
 * 小组件服务
 *
 * @author lizhen
 */
public interface IDingtalkIsvComponentService {

    /**
     * 获取人员token
     * @param corpId
     * @param userId
     * @return
     */
    String getUserToken(String corpId, String userId);

    /**
     * 获取审批列表
     * @return
     */
    Object getApprovalList(HttpServletRequest request);

    /**
     * 获取审批抄送列表
     * @param request
     * @return
     */
    Object getApprovalCCList(HttpServletRequest request);

    /**
     * 获取日程
     * @param request
     * @return
     */
    Object getScheduleList(HttpServletRequest request);


    Object getMyConsume(HttpServletRequest request);

    /**
     * 查询主企业id是否存在
     * @param request
     * @return
     */
    Object checkMaincorpIdIsExsit(HttpServletRequest request,String corpId);

    /**
     * 企业看板数据
     * @param request
     * @param corpId
     * @return
     */
    Object dashboardData(HttpServletRequest request,String corpId);

}
