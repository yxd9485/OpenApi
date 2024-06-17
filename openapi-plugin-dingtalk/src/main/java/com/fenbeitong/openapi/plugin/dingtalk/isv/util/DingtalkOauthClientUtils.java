package com.fenbeitong.openapi.plugin.dingtalk.isv.util;

import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @author xiaohai
 * @date 2021/11/18
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkOauthClientUtils {

    @Value("${dingtalk.isv.suitekey}")
    private String suiteKey;

    @Value("${dingtalk.isv.suiteSecret}")
    private String suiteSecret;

   // @Autowired
  //  private RedisTemplate<String, Object> redisTemplate;

    /**
     * 使用 Token 初始化账号Client
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dingtalkoauth2_1_0.Client createClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }

    /**
     * 通过授权码获取token
     * code：临时授权码
     */
    public String getUserAccessToken(String code) throws Exception {
        com.aliyun.dingtalkoauth2_1_0.Client client = createClient();
        GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest()
            .setClientId(suiteKey)
            .setClientSecret(suiteSecret)
            .setCode(code)
            .setGrantType("authorization_code");
        try {
            GetUserTokenResponse userToken = client.getUserToken(getUserTokenRequest);
            String accessToken = userToken.getBody().getAccessToken();
            return accessToken;
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                log.error("获取用户token出错：", err);
                throw new OpenApiDingtalkException( NumericUtils.obj2int( err.code ), err.message);
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                log.error("获取用户token出错：", err);
                throw new OpenApiDingtalkException( NumericUtils.obj2int( err.code ), err.message);
            }
        }
        return null;
    }

}
