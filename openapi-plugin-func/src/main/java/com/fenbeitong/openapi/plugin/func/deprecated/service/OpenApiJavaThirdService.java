package com.fenbeitong.openapi.plugin.func.deprecated.service;

import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * module: 迁移openapi-java项目二期<br/>
 * <p>
 * description: 迁移openapi-java项目二期<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/11 17:40
 */
public interface OpenApiJavaThirdService {

    /**
     * 查询机场列表和火车列表
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryCityList(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 根据部门ID集合查询部门详情
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryOrgUnitDetailByIds(HttpServletRequest httpRequest, ApiRequest request);


    /**
     * 根据公司ID查询公司授权负责人
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryAdmin(HttpServletRequest httpRequest, ApiRequest request);


    /**
     * 批量授权部门主管和角色
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryCompanyRoleAuth(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 根据公司ID查询公司角色列表
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryCompanyRoles(HttpServletRequest httpRequest, ApiRequest request);


    /**
     * 根据审批规则ID查询审批详情
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryApplyDetailById(HttpServletRequest httpRequest, ApiRequest request);


    /**
     * 查询审批用车类型
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryCarApproveType(HttpServletRequest httpRequest, ApiRequest request);


}
