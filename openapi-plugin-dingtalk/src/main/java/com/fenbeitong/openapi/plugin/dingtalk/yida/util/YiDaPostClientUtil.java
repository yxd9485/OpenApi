package com.fenbeitong.openapi.plugin.dingtalk.yida.util;

import com.alibaba.xxpt.gateway.shared.client.http.ExecutableClient;
import com.alibaba.xxpt.gateway.shared.client.http.PostClient;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dao.DingtalkYidaCorpAppDao;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.entity.DingtalkYidaCorpApp;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * <p>Title: YiDaClientUtil</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/12 3:41 下午
 */
@Slf4j
@Component
public class YiDaPostClientUtil {

    @Autowired
    private YiDaExecutableClientUtil yiDaExecutableClientUtil;

    @Autowired
    private DingtalkYidaCorpAppDao dingtalkYidaCorpAppDao;

    @Autowired
    private SuperAdminUtils superAdminUtils;

    public String post(String api, Map<String, String> params, String corpId) {
        DingtalkYidaCorpApp dingtalkYidaCorpApp = dingtalkYidaCorpAppDao.getDingtalkYidaCorpAppByCorpId(corpId);
        if (ObjectUtils.isEmpty(dingtalkYidaCorpApp)) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.CORP_INALID);
        }
        params.put("appType", dingtalkYidaCorpApp.getAppKey());
        params.put("systemToken", dingtalkYidaCorpApp.getAppSecret());
        params.put("userId", superAdminUtils.superAdminThirdEmployeeId(dingtalkYidaCorpApp.getCompanyId()));
        log.info("调用易搭,corpId:{},url:{},参数:{}", corpId, api, JsonUtils.toJson(params));
        ExecutableClient instance = yiDaExecutableClientUtil.getInstance(corpId);
        PostClient postClient = instance.newPostClient(api);
        if (!ObjectUtils.isEmpty(params)) {
            for (String key : params.keySet()) {
                postClient.addParameter(key, params.get(key));
            }
        }
        String result = postClient.post();
        log.info("调用易搭,corpId:{},url:{},result:{}", corpId, api, result);
        YiDaRespDTO yiDaRespDTO = JsonUtils.toObj(result, YiDaRespDTO.class);
        if (!yiDaRespDTO.isSuccess()) {
            log.info("调用易搭接口异常：", yiDaRespDTO.getMessage() + yiDaRespDTO.getErrorMsg());
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, yiDaRespDTO.getMessage() + yiDaRespDTO.getErrorMsg());
        }
        return JsonUtils.toJson(yiDaRespDTO.getResult());
    }

}
