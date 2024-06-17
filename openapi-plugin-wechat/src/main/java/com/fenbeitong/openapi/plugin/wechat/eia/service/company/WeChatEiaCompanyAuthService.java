package com.fenbeitong.openapi.plugin.wechat.eia.service.company;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.util.xml.XmlUtil;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WeChatRedisKeyConstant;
import com.fenbeitong.openapi.plugin.wechat.eia.dto.WeChatEiaCompanyAuthDecryptBody;
import com.fenbeitong.openapi.plugin.wechat.eia.dto.WeChatEiaSuiteTicketCallbackDecryptBody;
import com.fenbeitong.openapi.plugin.wechat.eia.util.WeChatEiaHttpUtils;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.CompanyAuthRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.CompanyAuthResponse;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.SuiteTokenRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.SuiteTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WeChatEiaCompanyAuthService {

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Value("${wechat.eia.suite-id}")
    private String suiteId;

    @Value("${wechat.eia.suite-secret}")
    private String suiteSecret;

    @Autowired
    private WeChatEiaHttpUtils wechatEiaHttpUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private PluginCorpAppDefinitionDao pluginCorpAppDefinitionDao;

    /**
     * 企业授权,返回授权人三方id
     *
     * @param decryptMsg
     */
    public void companyAuthWithAuthCode(String decryptMsg) {
        WeChatEiaCompanyAuthDecryptBody weChatIsvCompanyAuthDecryptBody = (WeChatEiaCompanyAuthDecryptBody) XmlUtil.xml2Object(decryptMsg, WeChatEiaCompanyAuthDecryptBody.class);
        String authCode = weChatIsvCompanyAuthDecryptBody.getAuthCode();
        log.info("wechat eia companyAuth, 开始处理企业授权");
        // 1.获取企业永久授权码
        CompanyAuthResponse companyAuthResponse = getPermanentCode(authCode);
        // 2.初始化企业授权信息
        initAuth(companyAuthResponse);
    }

    /**
     * 获取永久授权码
     *
     * @param authCode
     * @return
     */
    private CompanyAuthResponse getPermanentCode(String authCode) {
        String getPermanentCodeUrl = wechatHost + "/cgi-bin/service/get_permanent_code?suite_access_token=";
        CompanyAuthRequest companyAuthRequest = new CompanyAuthRequest();
        companyAuthRequest.setAuthCode(authCode);
        String res = wechatEiaHttpUtil.postJsonWithSuiteAccessToken(getPermanentCodeUrl, JsonUtils.toJson(companyAuthRequest));
        log.info("wechat eia companyAuth, getPermanentCode res is {}", res);
        CompanyAuthResponse companyAuthResponse = JsonUtils.toObj(res, CompanyAuthResponse.class);
        if (companyAuthResponse == null || StringUtils.isBlank(companyAuthResponse.getPermanentCode())) {
            log.warn("wechat eia companyAuth, getPermanentCode 失败:{}", res);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_PERMANENT_IS_NULL));
        }
        return companyAuthResponse;
    }

    /**
     * 初始化企业授权信息
     *
     * @param companyAuthResponse
     */
    public void initAuth(CompanyAuthResponse companyAuthResponse) {
        //查看企业是否授权过,未授权过的企业新增， 授权过的更新
        String corpid = companyAuthResponse.getAuthCorpInfo().getCorpid();
        PluginCorpAppDefinition corpAppDefinition = pluginCorpAppDefinitionDao.getByCorpId(corpid);
        if (corpAppDefinition == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        } else {
            corpAppDefinition.setThirdAgentId(NumericUtils.obj2long(companyAuthResponse.getAuthInfo().getAgent().get(0).getAgentid()));
            corpAppDefinition.setThirdAppKey(companyAuthResponse.getPermanentCode());
            corpAppDefinition.setThirdAppSecret(companyAuthResponse.getPermanentCode());
            corpAppDefinition.setUpdateTime(DateUtils.now());
            pluginCorpAppDefinitionDao.updateById(corpAppDefinition);
        }

    }


    /**
     * 获取第三方应用凭证
     *
     * @return
     */
    public String getSuiteAccessToken() {
        // 先尝试从redis查询
        String suiteAccessTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, WeChatRedisKeyConstant.WECHAT_EIA_SUITE_ACCESS_TOKEN);
        String suiteAccessToken = (String) redisTemplate.opsForValue().get(suiteAccessTokenKey);
        if (!StringUtils.isBlank(suiteAccessToken)) {
            return suiteAccessToken;
        }
        // redis未命中， 重新获取
        String getSuiteTokenUrl = wechatHost + "/cgi-bin/service/get_suite_token";
        SuiteTokenRequest suiteTokenRequest = new SuiteTokenRequest();
        suiteTokenRequest.setSuiteId(suiteId);
        suiteTokenRequest.setSuiteSecret(suiteSecret);
        String suiteTicketKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, WeChatRedisKeyConstant.WECHAT_EIA_SUITE_TICKET);
        String suiteTicket = (String) redisTemplate.opsForValue().get(suiteTicketKey);
        if (StringUtils.isBlank(suiteTicket)) {
            log.warn("wechat eia getSuiteAccessToken, suiteTicket获取失败");
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_SUITE_TICKET_IS_NULL));
        }
        suiteTokenRequest.setSuiteTicket(suiteTicket);
        String res = RestHttpUtils.postJson(getSuiteTokenUrl, JsonUtils.toJson(suiteTokenRequest));
        log.info("wechat eia getSuiteAccessToken res is {}", res);
        SuiteTokenResponse suiteTokenResponse = JsonUtils.toObj(res, SuiteTokenResponse.class);
        if (suiteTokenResponse == null || StringUtils.isBlank(suiteTokenResponse.getSuiteAccessToken())) {
            log.warn("wechat eia getSuiteAccessToken失败:{}", res);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_SUITE_ACCESS_TOKEN_IS_NULL));
        }
        suiteAccessToken = suiteTokenResponse.getSuiteAccessToken();
        // 缓存redis
        log.info("wechat eia saveSuiteAccessToken,key={},value={}", suiteAccessTokenKey, suiteAccessToken);
        redisTemplate.opsForValue().set(suiteAccessTokenKey, suiteAccessToken);
        // 有效期7200秒，设置7000秒过期，防止不可用
        redisTemplate.expire(suiteTicketKey, 7000, TimeUnit.SECONDS);
        return suiteAccessToken;
    }

    /**
     * 清除suiteAccessToken，微信返回40082,invalid suite_acccess_token时清除redis，再重新获取
     */
    public void clearSuiteAccessToken() {
        String suiteAccessTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, WeChatRedisKeyConstant.WECHAT_EIA_SUITE_ACCESS_TOKEN);
        redisTemplate.delete(suiteAccessTokenKey);
    }

    /**
     * 缓存SuiteTicket
     *
     * @param decryptMsg
     */
    public void saveSuiteTicket(String decryptMsg) {
        WeChatEiaSuiteTicketCallbackDecryptBody weChatEiaSuiteTicketCallbackDecryptBody = (WeChatEiaSuiteTicketCallbackDecryptBody) XmlUtil.xml2Object(decryptMsg, WeChatEiaSuiteTicketCallbackDecryptBody.class);
        String suiteTicket = weChatEiaSuiteTicketCallbackDecryptBody.getSuiteTicket();
        String suiteTicketKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, WeChatRedisKeyConstant.WECHAT_EIA_SUITE_TICKET);
        log.info("wechat eia saveSuiteTicket,key={},value={}", suiteTicketKey, suiteTicket);
        redisTemplate.opsForValue().set(suiteTicketKey, suiteTicket);
        redisTemplate.expire(suiteTicketKey, 30, TimeUnit.MINUTES);
    }


}
