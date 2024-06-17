package com.fenbeitong.openapi.plugin.yiduijie.service;

import com.fenbeitong.openapi.plugin.yiduijie.model.app.CreateAppReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.app.GetAppListRespDTO;

import java.util.List;

/**
 * <p>Title: IYiDuiJieInitService</p>
 * <p>Description: 易对接初始化服务接口</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 3:51 PM
 */
public interface IYiDuiJieInitService {

    /**
     * @return 易对接应用列表
     */
    List<GetAppListRespDTO> listApp();

    /**
     * 创建易对接应用
     *
     * @param createAppReq
     */
    void createApp(CreateAppReqDTO createAppReq);
}
