package com.fenbeitong.openapi.plugin.yiduijie.service.app;

import com.fenbeitong.openapi.plugin.yiduijie.model.app.CreateAppReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.app.GetAppListRespDTO;

import java.util.List;

/**
 * <p>Title: IAppService</p>
 * <p>Description: 帐号服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 3:09 PM
 */
public interface IAppService {

    /**
     * @return 获取易对接app列表
     */
    List<GetAppListRespDTO> getAppList(String companyId);

    /**
     * 生成易对接帐号
     *
     * @param createAppReqDTO
     */
    void createApp(CreateAppReqDTO createAppReqDTO);
}
