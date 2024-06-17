package com.fenbeitong.openapi.plugin.customize.yixin.service;

import com.fenbeitong.openapi.plugin.support.apply.dto.ChangeOrRefundApplyDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.OrderApplyDTO;

import java.util.Map;

/**
 * 宜信服务
 * @Auther zhang.peng
 * @Date 2021/12/29
 */
public interface YiXinOrderService {

    /**
     * 构建费控和行程审批数据
     * @param apply 原始申请数据
     *
     * @return true 推送成功; false 推送失败
     */
    Object buildCostAndThirdApplyInfo(OrderApplyDTO apply);

    /**
     * 构建超规信息
     * @param apply 退改订单数据
     *
     * @return true 推送成功; false 推送失败
     */
    Object buildExceedApplyInfo(Map<String,Object> apply);
}
