package com.fenbeitong.openapi.plugin.customize.common.service;

import com.fenbeitong.openapi.plugin.customize.common.dto.CustomFieldTranDTO;

/**
 * @author ctl
 * @date 2021/10/29
 */
public interface CustomFieldTranService {

    /**
     * 清洗数据
     *
     * @param dto
     * @return 执行时间 ms
     */
    long tran(CustomFieldTranDTO dto);

    /**
     * 清洗数据并发送通知
     *
     * @param dto
     * @param companyName
     * @return
     */
    void tranAndNotify(CustomFieldTranDTO dto, String companyName);
}
