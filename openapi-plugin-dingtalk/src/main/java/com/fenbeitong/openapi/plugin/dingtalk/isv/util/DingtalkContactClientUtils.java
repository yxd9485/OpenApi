package com.fenbeitong.openapi.plugin.dingtalk.isv.util;

import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponse;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @author xiaohai
 * @date 2021/11/18
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkContactClientUtils {


    /**
     * 使用 Token 初始化账号Client
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dingtalkcontact_1_0.Client createClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkcontact_1_0.Client(config);
    }

    /**
     * 通过授权码获取token
     * code：临时授权码
     */
    public GetUserResponseBody getUserInfo(String accessToken) throws Exception {
        com.aliyun.dingtalkcontact_1_0.Client client = createClient();
        GetUserHeaders getUserHeaders = new GetUserHeaders();
        getUserHeaders.xAcsDingtalkAccessToken = accessToken;
        try {
            GetUserResponse userResponse = client.getUserWithOptions("me", getUserHeaders, new RuntimeOptions());
            GetUserResponseBody userInfo = userResponse.getBody();
            return userInfo;
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                log.error("获取用户信息出错：", err);
                throw new OpenApiDingtalkException( NumericUtils.obj2int( err.code ), err.message);
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                log.error("获取用户信息出错：", err);
                throw new OpenApiDingtalkException( NumericUtils.obj2int( err.code ), err.message);
            }
        }
        return null;
    }
}
