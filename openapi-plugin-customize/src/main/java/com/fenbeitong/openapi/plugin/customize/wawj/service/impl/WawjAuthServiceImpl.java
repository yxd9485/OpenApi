package com.fenbeitong.openapi.plugin.customize.wawj.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.util.AesUtils;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjAuthRespDTO;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjGetLoginUserStatusRespDTO;
import com.fenbeitong.openapi.plugin.customize.wawj.service.IWawjAuthService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author lizhen
 * @date 2020/10/24
 */
@ServiceAspect
@Service
@Slf4j
public class WawjAuthServiceImpl implements IWawjAuthService {

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Override
    public WawjAuthRespDTO auth(String code, String companyId) {
        String wawjHost = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.WAWJ_HOST.getCode());
        String wawjAppKey = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.WAWJ_APP_KEY.getCode());
        String wawjEncryptCode = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.WAWJ_ENCRYPT_CODE.getCode());
        String wawjAesKey = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.WAWJ_AES_KEY.getCode());

        String url = wawjHost + "/ucs-api/router";
        String sign = null;
        try {
            sign = AesUtils.encryptAES(wawjEncryptCode + "," + System.currentTimeMillis(), wawjAesKey);
        } catch (Exception e) {
            log.error("aes 加密失败", e);
        }
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("appkey", wawjAppKey);
        param.add("clienttype", "0");
        param.add("sign", sign);
        param.add("v", "2.0");
        param.add("method", "info.getloginuserstatusapi");
        param.add("sessioncode", code);
        //String res = "{\"status\":0,\"msg\":\"\",\"emplid\":\"lalal\"}";
        String res = RestHttpUtils.postForm(url, param);
        WawjGetLoginUserStatusRespDTO wawjGetLoginUserStatusRespDTO = JsonUtils.toObj(res, WawjGetLoginUserStatusRespDTO.class);
        if (wawjGetLoginUserStatusRespDTO == null || wawjGetLoginUserStatusRespDTO.getStatus() != 0) {
            throw new FinhubException(1, "人员不存在或会话失效");
        }
        String thirdEmployeeId = wawjGetLoginUserStatusRespDTO.getEmplid();
        return WawjAuthRespDTO.builder().companyId(companyId).thirdEmployeeId(thirdEmployeeId).build();
    }
}
