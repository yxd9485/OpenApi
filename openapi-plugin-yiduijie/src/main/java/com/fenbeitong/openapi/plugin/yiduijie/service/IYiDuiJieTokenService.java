package com.fenbeitong.openapi.plugin.yiduijie.service;

/**
 * <p>Title: IYiDuiJieTokenService</p>
 * <p>Description: 易对接token服务接口</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 4:07 PM
 */
public interface IYiDuiJieTokenService {

    /**
     * @return
     */
    String getYiDuiJieToken();

    /**
     * @return
     */
    String getYiDuiJieToken(String userName, String password);

}
