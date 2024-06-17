package com.fenbeitong.openapi.plugin.func.deprecated.service;

import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * module: 迁移openapi-java项目二期<br/>
 * <p>
 * description: 迁移openapi-java项目二期<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/3 17:22
 * @since 1.0
 */
public interface OpenApiJavaSecondService {

    /**
     * 查询火车订单详情
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryTrainOrderDetail(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 根据订单ID查询下单时关联的自定义字段
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryOrderParam(HttpServletRequest httpRequest, ApiRequest request);


    /**
     * 查询公司相关规则
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryCompanyRule(HttpServletRequest httpRequest, ApiRequest request);

    /**
     * 根据公司ID查询公司相关权限
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryCompanyRole(HttpServletRequest httpRequest, ApiRequest request);


    /**
     * 查询国籍信息
     * @param httpRequest
     * @return
     */
    Object queryNationality(HttpServletRequest httpRequest);


    /**
     * 查询城市相对应的编码
     * @param httpRequest
     * @param request
     * @return
     */
    Object queryCityCode(HttpServletRequest httpRequest,ApiRequest request);

}
