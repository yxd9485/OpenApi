package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.employee;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.*;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token.YunzhijiaTokenService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
@Slf4j
public class YunzhijiaRemoteEmployeeService {

    @Value("${yunzhijia.api-host}")
    private String yunzhijiaHost;
    @Autowired
    private RestHttpUtils httpUtil;
    @Autowired
    YunzhijiaTokenService yunzhijiaTokenService;

    public YunzhijiaResponse<List<YunzhijiaEmployeeDTO>> getYunzhijiaRemoteEmployeeDetail(YunzhijiaAccessTokenReqDTO yunzhijiaAccessTokenReqDTO, YunzhijiaEmployeeReqDTO yunzhijiaEmployeeReqDTO) {
        //1.置换access_token
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessToken = yunzhijiaTokenService.getYunzhijiaRemoteAccessToken(yunzhijiaAccessTokenReqDTO);
        if (yunzhijiaAccessToken.getErrorCode() != RespCode.SUCCESS) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_ERROR)));
        }
        //2.获取返回的云之家access_token
        String accessToken = yunzhijiaAccessToken.getData().getAccessToken();
        //3.请求url
        String url = yunzhijiaHost + "/gateway/openimport/open/person/get?accessToken=" + accessToken;
        //4.构建请求参数，根据云之家开放平台文档进行参数构建
        String jsonstr = JsonUtils.toJson(yunzhijiaEmployeeReqDTO);
        HashMap<String, Object> paramMap = Maps.newHashMap();
        LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
        paramMap.put("eid", yunzhijiaEmployeeReqDTO.getEid());
        paramMap.put("data", jsonstr);
        linkedMultiValueMap.setAll(paramMap);
        log.info("调用云之家获取员工详情请求参数: {}", JsonUtils.toJson(linkedMultiValueMap));
        String result = "";
        try {
            result = httpUtil.postForm(url, linkedMultiValueMap);
            log.info("调用云之家获取员工详情返回结果: {}", result);
        } catch (Exception e) {
            log.info("调用云之家获取员工详情返回异常 {}", e.getCause());
            return null;
        }
        //5.返回结果
        YunzhijiaResponse<List<YunzhijiaEmployeeDTO>> yunzhijiaResponse = JsonUtils.toObj(result, new TypeReference<YunzhijiaResponse<List<YunzhijiaEmployeeDTO>>>() {
        });
        return yunzhijiaResponse;
    }

    /**
     * 获取云之家全部员工数据
     *
     * @param yunzhijiaAllEmployeeReqDTO
     * @return
     */
    public List<YunzhijiaEmployeeDTO> getYunzhijiaRemoteEmployeeList(String accessToken, YunzhijiaAllEmployeeReqDTO yunzhijiaAllEmployeeReqDTO) {
        String url = yunzhijiaHost + "/gateway/openimport/open/person/getall?accessToken=" + accessToken;
        //4.构建请求参数，根据云之家开放平台文档进行参数构建
        YunzhijiaResponse<List<YunzhijiaEmployeeDTO>> yunzhijiaResponse = null;
        List<YunzhijiaEmployeeDTO> yunzhijiaEmployeeList = Lists.newArrayList();
        int begin = 1;
        int count = 100;
        do {
            Map dataMap = Maps.newHashMap();
            dataMap.put("eid", yunzhijiaAllEmployeeReqDTO.getEid());
            dataMap.put("begin", (begin - 1) * count);
            dataMap.put("count", count);
            HashMap<String, Object> paramMap = Maps.newHashMap();
            LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
            paramMap.put("eid", yunzhijiaAllEmployeeReqDTO.getEid());
            paramMap.put("data", JsonUtils.toJson(dataMap));
            linkedMultiValueMap.setAll(paramMap);
            log.info("调用云之家获取全部员工请求参数: {}", JsonUtils.toJson(linkedMultiValueMap));
            String result = "";
            try {
                result = httpUtil.postForm(url, linkedMultiValueMap);
                log.info("调用云之家获取全部员工返回结果: {}", result);
            } catch (Exception e) {
                log.info("调用云之家获取全部员工返回异常 {}", e.getCause());
                return null;
            }
            //5.返回结果
            yunzhijiaResponse = JsonUtils.toObj(result, new TypeReference<YunzhijiaResponse<List<YunzhijiaEmployeeDTO>>>() {
            });
            yunzhijiaEmployeeList.addAll(yunzhijiaResponse.getData());
            begin++;
        } while (!ObjectUtils.isEmpty(yunzhijiaResponse.getData()));
        return yunzhijiaEmployeeList;
    }


}
