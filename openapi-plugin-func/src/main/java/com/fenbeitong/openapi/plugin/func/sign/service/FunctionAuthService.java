package com.fenbeitong.openapi.plugin.func.sign.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.support.util.ApiJwtTokenTool;
import com.fenbeitong.openapi.plugin.support.util.SignTool;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.sdk.dto.common.OpenApiRespDTO;
import com.fenbeitong.openapi.sdk.dto.employee.QueryThirdEmployeeRespDTO;
import com.fenbeitong.openapi.sdk.webservice.employee.FbtEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import retrofit2.Call;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: ApiSignServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/6 11:43 AM
 */
@Slf4j
@ServiceAspect
@Service
public class FunctionAuthService {

    @Value("${openapi.timestamp.gap}")
    private long gap;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private ApiJwtTokenTool jwtTokenTool;

    @Autowired
    private FbtEmployeeService userCenterService;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    public String checkSign(ApiRequestBase apiRequestBase) throws IOException {
        DecodedJWT jwt = jwtTokenTool.verifyToken(apiRequestBase.getAccessToken());
        String appId = jwt.getClaim("appId").asString();
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(appId);
        if (authDefinition == null) {
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.TOKEN_INFO_IS_ERROR));
        }
        String sign = apiRequestBase.getSign();
        Long timestamp = apiRequestBase.getTimestamp();
        String expectSign = SignTool.genSign(timestamp, apiRequestBase.getData(), authDefinition.getSignKey());
        if (!expectSign.equals(sign)) {
            log.info("expectSign is " + expectSign + ",but request sign is " + sign);
            boolean showSign = apiRequestBase.getShowSign() == null ? false : apiRequestBase.getShowSign();
            //线上环境
            if (profile.toLowerCase().contains("pro") && !showSign) {
                throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.SIGN_ERROR));
            } else {
                throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.SIGN_ERROR_TIP), expectSign, sign);
            }
        }
        OffsetDateTime nowTime = Instant.now().atOffset(ZoneOffset.ofHours(8));
        OffsetDateTime requestTime = Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.ofHours(8));
        //线上才处理 请求的时间戳加上10分钟后还在现在的时间之前,则表明至少十分钟以前的请求了
        if (profile.toLowerCase().contains("pro") && requestTime.plusMinutes(gap).isBefore(nowTime)) {
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.TIMESTAMP_NO_EFFECT));
        }
        if (apiRequestBase instanceof ApiRequest) {
            ApiRequest apiRequest = (ApiRequest) apiRequestBase;
            Map<String, Object> param = new HashMap<>();
            param.put("user_id", apiRequest.getEmployeeId());
            param.put("company_id", appId);
            param.put("appType", apiRequest.getEmployeeType());
            Call<OpenApiRespDTO<QueryThirdEmployeeRespDTO>> result = userCenterService.queryThirdEmployee(param);
            OpenApiRespDTO<QueryThirdEmployeeRespDTO> resp = result.execute().body();
            if (resp == null || !resp.success()) {
                throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.QUERY_THIRD_USER_ERROR));
            }
            QueryThirdEmployeeRespDTO data = resp.getData();
            return data == null ? null : data.getToken();
        }
        return appId;
    }

}
