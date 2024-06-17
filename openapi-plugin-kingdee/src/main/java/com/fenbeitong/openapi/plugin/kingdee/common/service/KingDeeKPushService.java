package com.fenbeitong.openapi.plugin.kingdee.common.service;


/**
 * 金蝶相关接口
 *
 * @Auther zhang.peng
 * @Date 2021/6/7
 */
public interface KingDeeKPushService {

    /**
     * 订单推送
     */
     void orderPush(String requestBody, String companyId, String moduleType, Integer templateType, String dataType);


    /**
     * 凭证推送
     */
     void voucherPush(String compayId, String batchId);

    /**
     * 账单推送
     */
     void billPush(String companyId, String billNo, String moduleType, Integer templateType);

    /**
     * 账单手动推送
     */
     void madeBillPush(String companyId, String billNo, String moduleType, Integer templateType);

    /**
     * 账单手动推送
     */
    void initThirdData(String companyId, String billNo, String moduleType, Integer templateType);
}
