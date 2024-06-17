package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingtalkJsapiSignRespDTO;


public interface IDingtalkEiaWebService {

    DingtalkJsapiSignRespDTO getJsapiSign(UserComInfoVO user, String url);

}
