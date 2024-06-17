package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkApproveKitResultEntity;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.IFormFieldDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 钉钉市场版自定义审批套件
 *
 * @author xiaohai
 */
public interface IDingtalkIsvApproveKitService {

    /**
     * 获取用车套件初始化数据
     * @param request
     * @return
     */
    List<IFormFieldDTO> getCarInitData(HttpServletRequest request);

    /**
     * 获取用车套件刷新数据
     * @param request
     * @return
     */
    List<IFormFieldDTO> getCarRefreshData(HttpServletRequest request);

    /**
     * 用车套件提交校验
     * @param request
     * @return
     */
    DingtalkApproveKitResultEntity checkSubmitData(HttpServletRequest request);

    /**
     * 获取差旅套件初始化数据
     * @param request
     * @return
     */
    List<IFormFieldDTO> getTripInitData(HttpServletRequest request);

    /**
     * 差旅套件刷新数据
     * @param request
     * @return
     */
    List<IFormFieldDTO> getTripRefreshData(HttpServletRequest request);

    /**
     * 差旅套件提交校验
     * @param request
     * @return
     */
    DingtalkApproveKitResultEntity checkSubmitTripData(HttpServletRequest request);

    /**
     * 获取用餐套件初始化数据
     */
    List<IFormFieldDTO> getDinnerInitData(HttpServletRequest request);

    /**
     * 获取用餐套件刷新数据
     */
    List<IFormFieldDTO> getDinnerRefreshData(HttpServletRequest request);

    /**
     * 用餐提交数据校验
     */
    DingtalkApproveKitResultEntity getDinnerSubmitData(HttpServletRequest request);

    /**
     * 获取外卖套件初始化数据
     */
    List<IFormFieldDTO> getTakeawayInitData(HttpServletRequest request);

    /**
     * 获取外卖套件刷新数据
     */
    List<IFormFieldDTO> getTakeawayRefreshData(HttpServletRequest request);

    /**
     * 外卖提交数据校验
     */
    DingtalkApproveKitResultEntity getTakeawaySubmitData(HttpServletRequest request);

}
