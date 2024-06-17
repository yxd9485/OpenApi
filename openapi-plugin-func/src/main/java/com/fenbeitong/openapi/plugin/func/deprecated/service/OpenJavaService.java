package com.fenbeitong.openapi.plugin.func.deprecated.service;


import com.fenbeitong.openapi.plugin.func.deprecated.dto.employee.OpenEmployeeIDetailRespDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * module: 迁移openapi-java项目<br/>
 * <p>
 * description: 登录公共接口<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/5 16:58
 * @since 2.0
 */
public interface OpenJavaService {

    /**
     * 获取token
     * @param appId
     * @param appKey
     * @return
     */
    String getToken(String appId, String appKey);


    /**
     * 获取人员详情
     * @param request
     * @return
     */
    Object getEmployeeInfo(OpenEmployeeIDetailRespDTO request);


    /**
     * 添加项目
     * @param httpRequest
     * @param request
     * @return
     */
    Object getAddThirdProject(HttpServletRequest httpRequest, ApiRequest request);


    /**
     * 获取企业人员信息
     * @param httpRequest
     * @param request
     * @return
     */
    Object getCompanyEmployeeInfo(HttpServletRequest httpRequest, ApiRequest request);


    /**
     * 更新项目
     * @param httpRequest
     * @param request
     * @return
     */
    Object getUpdateThirdProject(HttpServletRequest httpRequest, ApiRequest request);


    /**
     * 查询部门详情
     * @param httpRequest
     * @param request
     * @return
     */
    Object getThirdOrgUnitDetail(HttpServletRequest httpRequest, ApiRequest request);


    /**
     * 获取鉴权信息
     * @param request
     * @return
     */
    Object getRegister(HttpServletRequest request);


    /**
     * 查询三方人员信息 V2
     * @param httpRequest
     * @return
     */
    Object getQueryThirdEmployee(HttpServletRequest httpRequest);

    /**
     * 登录鉴权,分发Token
     * @param httpRequest
     * @return
     */
    Object getAuth(HttpServletRequest httpRequest);

}
