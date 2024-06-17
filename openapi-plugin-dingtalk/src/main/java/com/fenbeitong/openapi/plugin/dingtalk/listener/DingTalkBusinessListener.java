package com.fenbeitong.openapi.plugin.dingtalk.listener;

import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;

import java.util.List;

public interface DingTalkBusinessListener {


    /**
     * 填充tripbean通用字段
     *
     * @param companyId          企业ID
     * @param rowValues          表单数据
     * @param returnTrip         是否为返程
     * @param tripListBean       tripListBean
     * @param applyDepartureDate 出发时间配置 0-精确时间（天） 1-范围时间
     * @param type 行程类型
     */
    void fillTripBeanFields(String companyId, List rowValues, boolean returnTrip, DingtalkTripApplyProcessInfo.TripListBean tripListBean, boolean isIntlAir, String dingtalkUserId, String instanceId, Integer applyDepartureDate, int type);

    /**
     * 国际机票表单解析
     *
     * @param companyId
     * @param rowValues
     * @param returnTrip
     * @param tripListBean
     * @param isIntlAir
     */
    void fillIntlAirTripBeanFields(String companyId, List rowValues, boolean returnTrip, DingtalkTripApplyProcessInfo.TripListBean tripListBean, boolean isIntlAir, String dingtalkUserId, String instanceId, Integer applyDepartureDate);


    /**
     * 创建酒店行程
     *
     * @param rowValues 行程表单数据
     * @param companyId 企业ID
     * @return
     */
    DingtalkTripApplyProcessInfo.TripListBean createHotelTripBean(String companyId, String dingtalkUserId, List rowValues, String instanceId);
}