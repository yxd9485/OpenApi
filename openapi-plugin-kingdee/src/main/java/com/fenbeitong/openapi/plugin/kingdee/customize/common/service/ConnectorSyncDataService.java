package com.fenbeitong.openapi.plugin.kingdee.customize.common.service;

/**
 * @ClassName ConnectorSyncDataService
 * @Description 同步金蝶数据
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/7/27 上午10:50
 **/
public interface ConnectorSyncDataService {
    /**
     * 同步金额数据
     * @author helu
     * @date 2022/7/27 上午10:52
     * @param companyId 公司id
     * @param settingCode 连接器配置编码
     */
     void connectorSyncKingdeeData(String companyId,String settingCode);
}
