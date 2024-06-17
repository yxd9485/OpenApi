package com.fenbeitong.openapi.plugin.yiduijie.service;

import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieNotifyMsgResultReq;

/**
 * <p>Title: IYiDuiJieInteractService</p>
 * <p>Description: 易对接交互服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 4:27 PM
 */
public interface IYiDuiJieInteractService {

    /**
     * 查询业务数据
     *
     * @param id 批次号
     * @return 业务数据
     */
    Object queryBusinessData(String id);

    /**
     * 易对接通知分贝通结果
     *
     * @param req 通知参数
     * @return 响应信息
     */
    void notifyMsgResult(YiDuiJieNotifyMsgResultReq req);
}
