package com.fenbeitong.openapi.plugin.customize.connector.service;

/**
 * @ClassName ConnectorSyncDataService
 * @Description 使用连接器同步数据
 * @Company www.fenbeitong.com
 * @Author chengzhigang1
 * @Date 2022/9/16 下午17:50
 **/
public interface CommonSyncDataService {
    /**
     * 同步数据
     *
     * @param companyId   公司id
     * @param settingCode 连接器配置编码
     * @author chengzhigang1
     * @date 2022/9/16 下午17:50
     */
    void connectorSyncData(String companyId, String settingCode);
}
