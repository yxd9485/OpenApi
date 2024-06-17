package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResultEntity;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingtalkJsapiSignRespDTO;

/**
 * 钉钉H5或WEB后台的一些服务
 *
 * @author lizhen
 */
public interface IDingtalkIsvWebService {

    /**
     * 获取签名 改成使用uc token解析器获取到的对象
     *
     * @param user
     * @param url
     * @return
     */
    DingtalkJsapiSignRespDTO getJsapiSign(UserComInfoVO user, String url);

    DingtalkResultEntity getUserInfo(String code);

    /**
     * 修改手机号
     *
     * @param unionId
     * @return
     */
    DingtalkResultEntity updateMobile(String token, String unionId);


}
