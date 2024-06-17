package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuAuthenRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.*;

/**
 * 飞书auth service
 *
 * @author lizhen
 * @date 2020/1/25
 */
@ServiceAspect
@Service
@Slf4j
public abstract class AbstractFeiShuAuthService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    /**
     * 获取FeiShuHttpUtils
     *
     * @return
     */
    protected abstract AbstractFeiShuHttpUtils getFeiShuHttpUtils();

    /**
     * 免登code校验(h5/web)
     *
     * @param code
     * @return
     */
    protected FeiShuAuthenRespDTO.FeiShuAuthenData webLoginValidate(String code) {
        String url = feishuHost + FeiShuConstant.LOGIN_VALIDATE_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("grant_type", "authorization_code");
        param.put("code", code);
        String res = getFeiShuHttpUtils().postJsonWithAppAccessToken(url, param);
        FeiShuAuthenRespDTO feiShuAuthenRespDTO = JsonUtils.toObj(res, FeiShuAuthenRespDTO.class);
        if (feiShuAuthenRespDTO == null || 0 != feiShuAuthenRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.WEB_LOGIN_VALIDATE_FAILED);
        }
        return feiShuAuthenRespDTO.getData();
    }

}
