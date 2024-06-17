package com.fenbeitong.openapi.plugin.customize.shankun.service;

/**
 * <p>Title: IShangKunOrderCallbackService</p>
 * <p>Description: 上坤订单推送</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/26 6:54 PM
 */
public interface IShangKunOrderCallbackService {

    /**
     * 上坤订单推送
     *
     *
     * @param url
     * @param data
     * @return
     */
    Object callback(String url, String data);
}
