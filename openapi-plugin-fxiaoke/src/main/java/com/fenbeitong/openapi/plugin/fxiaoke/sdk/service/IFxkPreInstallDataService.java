package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.*;

/**
 * <p>Title: IFxkCustomDataService</p>
 * <p>Description: 纷享销客自定义对象服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 8:14 PM
 */
public interface IFxkPreInstallDataService {

    /**
     * 获取预设对象数据
     *
     * @param req 请求参数
     * @return 自定义对象数据
     */
    FxkGetCustomDataRespDTO getCustomData(FxkGetCustomDataReqDTO req);

    /**
     * 获取预设对象数据列表
     *
     * @param req 请求参数
     * @return 自定义对象数据列表
     */
//    FxkGetCustomDataListRespDTO getPreInstallDataList(FxkGetCustomDataListReqDTO req);

    /**
     * 获取预设对象数据列表
     *
     * @param req 请求参数
     * @return 自定义对象数据列表
     */
    FxkGetCustomDataListRespDTO  getFxkPrInstallDataList(FxkGetCustomDataListReqDTO req);
}
