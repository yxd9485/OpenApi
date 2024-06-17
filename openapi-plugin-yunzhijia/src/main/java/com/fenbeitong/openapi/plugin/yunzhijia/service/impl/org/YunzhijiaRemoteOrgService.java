package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.org;

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
public class YunzhijiaRemoteOrgService {

    @Value("${yunzhijia.api-host}")
    private String yunzhijiaHost;
    @Autowired
    private RestHttpUtils httpUtil;
    @Autowired
    YunzhijiaTokenService yunzhijiaTokenService;

    /**
     * 获取云之家源部门详情，该接口为resGroupSecret级别
     *
     * @param yunzhijiaAccessTokenReqDTO
     * @return
     */
    public YunzhijiaResponse<List<YunzhijiaOrgDTO>> getYunzhijiaRemoteOrgDetail(YunzhijiaAccessTokenReqDTO yunzhijiaAccessTokenReqDTO, YunzhijiaOrgReqDTO yunzhijiaOrgReqDTO) {
        //1.置换access_token
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessToken = yunzhijiaTokenService.getYunzhijiaRemoteAccessToken(yunzhijiaAccessTokenReqDTO);
        if (yunzhijiaAccessToken.getErrorCode() != RespCode.SUCCESS) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_ERROR)));
        }
        //2.获取返回的云之家access_token
        String accessToken = yunzhijiaAccessToken.getData().getAccessToken();
        //3.请求url
        String url = yunzhijiaHost + "/gateway/openimport/open/dept/get?accessToken=" + accessToken;
        //4.构建请求参数，根据云之家开放平台文档进行参数构建
        String jsonstr = JsonUtils.toJson(yunzhijiaOrgReqDTO);
        HashMap<String, Object> paramMap = Maps.newHashMap();
        LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
        paramMap.put("eid", yunzhijiaOrgReqDTO.getEid());
        paramMap.put("data", jsonstr);
        linkedMultiValueMap.setAll(paramMap);
        log.info("请求云之家获取部门详情请求参数: {}", JsonUtils.toJson(linkedMultiValueMap));
        String result = "";
        try {
            result = httpUtil.postForm(url, linkedMultiValueMap);
            log.info("请求云之家获取部门详情返回结果: {}", result);
        } catch (Exception e) {
            log.info("请求云之家获取部门详情返回异常 {}", e.getCause());
            return null;
        }
        YunzhijiaResponse<List<YunzhijiaOrgDTO>> yunzhijiaOrgRespDTO = JsonUtils.toObj(result, new TypeReference<YunzhijiaResponse<List<YunzhijiaOrgDTO>>>() {
        });
        return yunzhijiaOrgRespDTO;
    }


    /**
     * 获取云之家当前部门基本信息或部门负责人,该接口为app级别
     *
     * @param yunzhijiaAccessTokenReqDTO
     * @return
     */
    public YunzhijiaResponse<YunzhijiaOrgInChargeDTO> getYunzhijiaRemoteOrgBaseOrLeaderDetail(YunzhijiaAccessTokenReqDTO yunzhijiaAccessTokenReqDTO, String orgId) {
        //1.置换access_token
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessToken = yunzhijiaTokenService.getYunzhijiaRemoteAccessToken(yunzhijiaAccessTokenReqDTO);
        if (yunzhijiaAccessToken.getErrorCode() != RespCode.SUCCESS) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_ERROR)));
        }
        //2.获取返回的云之家access_token
        String accessToken = yunzhijiaAccessToken.getData().getAccessToken();
        //3.请求url
        String url = yunzhijiaHost + "/gateway/opendata-control/data/getorg?accessToken=" + accessToken;
        //4.构建请求参数，根据云之家开放平台文档进行参数构建
        HashMap<String, Object> paramMap = Maps.newHashMap();
        LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
        paramMap.put("eid", yunzhijiaAccessTokenReqDTO.getEid());
        paramMap.put("appId", yunzhijiaAccessTokenReqDTO.getAppId());
        paramMap.put("orgId", orgId);

        linkedMultiValueMap.setAll(paramMap);
        log.info("请求云之家当前部门基本信息或部门负责人请求参数: {}", JsonUtils.toJson(linkedMultiValueMap));
        String result = "";
        try {
            result = httpUtil.postForm(url, linkedMultiValueMap);
            log.info("请求云之家当前部门基本信息或部门负责人返回结果: {}", result);
        } catch (Exception e) {
            log.info("请求云之家当前部门基本信息或部门负责人返回异常 {}", e.getCause());
            return null;
        }
        YunzhijiaResponse<YunzhijiaOrgInChargeDTO> yunzhijiaOrgRespDTO = JsonUtils.toObj(result, new TypeReference<YunzhijiaResponse<YunzhijiaOrgInChargeDTO>>() {
        });
        return yunzhijiaOrgRespDTO;
    }


    /**
     * 根据公司ID查询部门集合列表,app级别
     *
     * @param yunzhijiaToken
     * @param corpId
     * @return
     */
    public YunzhijiaOrgRespDTO getAllDepByCorpId(String yunzhijiaToken, String corpId) {
        String url = yunzhijiaHost + "/gateway/openimport/open/dept/getall?accessToken=" + yunzhijiaToken;
        //4.构建请求参数，根据云之家开放平台文档进行参数构建
        HashMap<String, Object> paramMap = Maps.newHashMap();
        LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
        paramMap.put("eid", corpId);
        linkedMultiValueMap.setAll(paramMap);
        log.info("请求云之家获取所有部门请求参数: {}", JsonUtils.toJson(linkedMultiValueMap));
        String result = "";
        try {
            result = httpUtil.postForm(url, linkedMultiValueMap);
            log.info("请求云之家获取所有部门请求参数返回结果: {}", result);
        } catch (Exception e) {
            log.info("请求云之家获取所有部门请求参数返回异常 {}", e.getCause());
            return null;
        }
        //返回所有部门数据
        YunzhijiaOrgRespDTO yunzhijiaOrgRespDTO = JsonUtils.toObj(result, YunzhijiaOrgRespDTO.class);
        return yunzhijiaOrgRespDTO;
    }



    public List<YunzhijiaOrgLeaderDTO> getYunzhijiaRemoteAllOrgLeaders(YunzhijiaAccessTokenReqDTO yunzhijiaAccessTokenReqDTO) {
        //1.置换access_token
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessToken = yunzhijiaTokenService.getYunzhijiaRemoteAccessToken(yunzhijiaAccessTokenReqDTO);
        if (yunzhijiaAccessToken.getErrorCode() != RespCode.SUCCESS) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_ERROR)));
        }
        //2.获取返回的云之家access_token
        String accessToken = yunzhijiaAccessToken.getData().getAccessToken();
        //3.请求url
        String url = yunzhijiaHost + "/gateway/openimport/open/company/queryOrgAdmins?accessToken=" + accessToken;
        YunzhijiaResponse<List<YunzhijiaOrgLeaderDTO>> yunzhijiaResponse = null;
        List<YunzhijiaOrgLeaderDTO> yunzhijiaEmployeeList = Lists.newArrayList();
        int begin = 1;
        int count = 500;
        do {
            Map dataMap = Maps.newHashMap();
            dataMap.put("eid", yunzhijiaAccessTokenReqDTO.getEid());
            dataMap.put("begin", (begin - 1) * count);
            dataMap.put("count", count);
            HashMap<String, Object> paramMap = Maps.newHashMap();
            LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
            paramMap.put("eid", yunzhijiaAccessTokenReqDTO.getEid());
            paramMap.put("data", JsonUtils.toJson(dataMap));
            linkedMultiValueMap.setAll(paramMap);
            log.info("请求云之家获取所有部门负责人请求参数: {}", JsonUtils.toJson(linkedMultiValueMap));
            String result = "";
            try {
                result = httpUtil.postForm(url, linkedMultiValueMap);
                log.info("请求云之家获取所有部门负责人返回结果: {}", result);
            } catch (Exception e) {
                log.info("请求云之家获取所有部门负责人返回异常 {}", e.getCause());
                return null;
            }
            //5.返回结果
            yunzhijiaResponse = JsonUtils.toObj(result, new TypeReference<YunzhijiaResponse<List<YunzhijiaOrgLeaderDTO>>>() {
            });
            yunzhijiaEmployeeList.addAll(yunzhijiaResponse.getData());
            begin++;
        } while (!ObjectUtils.isEmpty(yunzhijiaResponse.getData()));
        return yunzhijiaEmployeeList;
    }
}
