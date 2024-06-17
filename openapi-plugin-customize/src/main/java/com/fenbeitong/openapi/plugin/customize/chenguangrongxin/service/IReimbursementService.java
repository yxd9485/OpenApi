package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service;

import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.ReimbursementDetailDTO;

/**
 * @author machao
 * @date 2022/9/16
 */
public interface IReimbursementService {

    /**
     * 报销单数据推送
     *
     * @param data data
     */
    void pushData(ReimbursementDetailDTO data);
}
