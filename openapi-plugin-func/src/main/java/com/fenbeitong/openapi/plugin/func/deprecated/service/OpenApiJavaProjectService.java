package com.fenbeitong.openapi.plugin.func.deprecated.service;

import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * module: 迁移 open-java 项目<br/>
 * <p>
 * description: 项目模块<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/16 17:48
 */
public interface OpenApiJavaProjectService {

    /**
     * 变更项目状态
     * @param httpRequest
     * @param request
     * @return
     */
    Object updateState(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 批量变更项目状态
     * @param httpRequest
     * @param request
     * @return
     */
    Object updateStateBatch(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 项目列表
     * @param httpRequest
     * @param request
     * @return
     */
    Object projectList(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 批量创建项目
     * @param httpRequest
     * @param request
     * @return
     */
    Object createBatch(HttpServletRequest httpRequest, ApiRequest request);
}
