package com.fenbeitong.openapi.plugin.daoyiyun.service;


import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunCallbackReqDTO;

/**
 * 道一云回调
 *
 * @author lizhen
 */

public interface DaoYiYunCallbackService {
    /**
     * 回调处理
     *
     * @param daoYiYunCallbackReqDTO
     * @return
     */
    String revice(DaoYiYunCallbackReqDTO daoYiYunCallbackReqDTO);

}
