package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaRedisKeyConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaResourceLevelConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dao.YunzhijiaAddressListDao;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.*;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaAddressList;
import com.fenbeitong.openapi.plugin.yunzhijia.exception.YunzhijiaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@Slf4j
@ServiceAspect
@Service
public class YunzhijiaTokenService {

    @Value("${yunzhijia.api-host}")
    private String yunzhijiaHost;
    @Autowired
    private RestHttpUtils httpUtil;
    @Autowired
    YunzhijiaAddressListDao yunzhijiaAddressListDao;
    @Autowired
    private PluginCorpAppDefinitionDao pluginCorpAppDefinitionDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据企业ID查询通讯录对应的token
     *
     * @param corpId
     * @return
     */
    public YunzhijiaAddressList getYunzhijiaToken(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_INFO_IS_NULL)));
        }
        tk.mybatis.mapper.entity.Example example = new tk.mybatis.mapper.entity.Example(YunzhijiaAddressList.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("corpId", corpId);
        log.info("根据企业eid查询云之家token请求参数 {}", corpId);
        YunzhijiaAddressList byExample = null;
        try {
            byExample = yunzhijiaAddressListDao.getByExample(example);
            log.info("返回云之家企业token: {}", JsonUtils.toJson(byExample));
        } catch (Exception e) {
            log.info("根据企业ID查询云之家通讯录token失败: {}", e.getCause());
        }
        return byExample;
    }


    /**
     * 获取云之家access_token，资源接口授权级别不同，需要根据scope进行级别区分
     *
     * @param reqDTO
     * @return
     */
    public YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> getYunzhijiaRemoteAccessToken(YunzhijiaAccessTokenReqDTO reqDTO) {
        String accessTokenUrl = yunzhijiaHost + "/gateway/oauth2/token/getAccessToken";
//        log.info("转换结果 {}",JsonUtils.toJson(reqDTO));
        String result = httpUtil.postJson(accessTokenUrl, JsonUtils.toJson(reqDTO));
//        log.info("获取云之家access_token返回结果 {}", result);
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessTokenRespDTO = JsonUtils.toObj(result, new TypeReference<YunzhijiaResponse<YunzhijiaAccessTokenRespDTO>>() {
        });
        return yunzhijiaAccessTokenRespDTO;
    }

    /**
     * 刷新token
     *
     * @param reqDTO
     * @return
     */
    public YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> refreshYunzhijiaRemoteAccessToken(YunzhijiaAccessTokenReqDTO reqDTO) {
        String accessTokenUrl = yunzhijiaHost + "/gateway/oauth2/token/refreshToken";
        log.info("刷新token请求数据 {}", JsonUtils.toJson(reqDTO));
        String result = httpUtil.postJson(accessTokenUrl, JsonUtils.toJson(reqDTO));
        log.info("刷新云之家access_token返回结果 {}", result);
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessTokenRespDTO = JsonUtils.toObj(result, new TypeReference<YunzhijiaResponse<YunzhijiaAccessTokenRespDTO>>() {
        });
        return yunzhijiaAccessTokenRespDTO;

    }

    /**
     * 获取云之家AppAccessToken
     *
     * @param corpId
     * @return
     */
    public String getYunzhijiaAppAccessToken(String corpId) {
        PluginCorpAppDefinition corpByThirdCorpId = pluginCorpAppDefinitionDao.getByCorpId(corpId);
        if (corpByThirdCorpId == null) {
            throw new YunzhijiaException(NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_INFO_IS_NULL));
        }
        // 先尝试从redis查询
        String appAccessTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(YunzhijiaRedisKeyConstant.WECHAT_EIA_CONTACT_ACCESS_TOKEN, corpId));
        String contactAccessToken = (String) redisTemplate.opsForValue().get(appAccessTokenKey);
        if (!StringUtils.isBlank(contactAccessToken)) {
            return contactAccessToken;
        }
        // redis未命中， 重新获取
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
                .eid(corpId)
                .appId(StringUtils.obj2str(corpByThirdCorpId.getThirdAgentId()))
                .secret(corpByThirdCorpId.getThirdAppSecret())
                .timestamp(System.currentTimeMillis())
                .scope(YunzhijiaResourceLevelConstant.APP)
                .build();
        String accessTokenUrl = yunzhijiaHost + "/gateway/oauth2/token/getAccessToken";
        String result = RestHttpUtils.postJson(accessTokenUrl, JsonUtils.toJson(build));
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessTokenRespDTO = JsonUtils.toObj(result, new TypeReference<YunzhijiaResponse<YunzhijiaAccessTokenRespDTO>>() {
        });
        if (yunzhijiaAccessTokenRespDTO.getErrorCode() != RespCode.SUCCESS) {
            throw new YunzhijiaException(NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_ERROR));
        }
        contactAccessToken = yunzhijiaAccessTokenRespDTO.getData().getAccessToken();
        // 缓存redis
        log.info("yunzhijia eia save appAccessToken,key={},value={}", appAccessTokenKey, contactAccessToken);
        redisTemplate.opsForValue().set(appAccessTokenKey, contactAccessToken);
        int expiresIn = yunzhijiaAccessTokenRespDTO.getData().getExpireIn() - 200;
        redisTemplate.expire(appAccessTokenKey, expiresIn, TimeUnit.SECONDS);
        return contactAccessToken;
    }


    /**
     * 清除appAccessToken
     * @param corpId
     */
    public void cleanAppAccessToken(String corpId) {
        String appAccessTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(YunzhijiaRedisKeyConstant.WECHAT_EIA_CONTACT_ACCESS_TOKEN, corpId));
        redisTemplate.delete(appAccessTokenKey);
    }

}
