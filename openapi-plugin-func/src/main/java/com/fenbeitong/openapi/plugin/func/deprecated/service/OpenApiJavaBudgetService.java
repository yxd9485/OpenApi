package com.fenbeitong.openapi.plugin.func.deprecated.service;

import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * module: 迁移 open-java 项目<br/>
 * <p>
 * description: 预算模块 <br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/11 19:52
 */
public interface OpenApiJavaBudgetService {
    /**
     * 新增第三方预算
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object createBudget(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 更新第三方预算
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object updateBudget(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 删除第三方预算
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object deleteBudget(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 第三方预算应用保存
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object saveApplyBudget(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 查询第三方预算列表
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryBudgetList(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 查询第三方预算详情
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryBudgetDetail(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 查询第三方预算进度
     *
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryBudgetProgress(HttpServletRequest httpRequest, ApiRequest request);
}
