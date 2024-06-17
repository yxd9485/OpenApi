package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkFreeLoginDto;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUserPageResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiUserService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.auth.constant.FreeLoginConstant;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcFetchEmployInfoReqDto;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.FreeLoginProcessUtil;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: ApiUserServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 2:27 PM
 */
@Slf4j
@ServiceAspect
@Service
public class ApiUserServiceImpl implements IApiUserService {

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    public OapiUserGetResponse getUserWithOriginal(String corpId, String userId) {
        log.info("调用钉钉用户详情接口，参数: corpId: {}, userId: {}", corpId, userId);
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/user/get");
        OapiUserGetRequest request = new OapiUserGetRequest();
        request.setUserid(userId);
        request.setHttpMethod("GET");
        try {
            OapiUserGetResponse response = client.execute(request, accessToken);
            log.info("调用钉钉用户详情接口完成, 返回结果: {}", response.getBody());
            return response;
        } catch (ApiException e) {
            log.error("调用钉钉用户详情接口异常", e);
        }
        return null;
    }

    /**
     * 根据钉钉部门ID，获取其下的所有用户
     *
     * @param departmentId 钉钉部门ID
     * @param corpId       corpId
     */
    @Override
    public List<DingtalkUser> getAllUserByDepartment(long departmentId, String corpId) {
        List<DingtalkUser> allUsers = new ArrayList<>();
        OapiUserListbypageResponse listResponse;
        long pageSize = 100L;
        long offset = 0;
        do {
            listResponse = getUserByDepartmentWithPage(departmentId, corpId, offset, pageSize);
            if (listResponse != null) {
                DingtalkUserPageResponse pageResponse = JsonUtils.toObj(listResponse.getBody(), DingtalkUserPageResponse.class);
                if (pageResponse != null && !ObjectUtils.isEmpty(pageResponse.getUserlist())) {
                    allUsers.addAll(pageResponse.getUserlist());
                }
                offset += pageSize;
            }
        } while (listResponse != null && listResponse.getHasMore());
        return allUsers;
    }

    /**
     * 获取钉钉部门下的用户-分页查询
     *
     * @param departmentId 钉钉部门ID
     * @param corpId       corpId
     * @param offset       偏移量
     * @param pageSize     每页数据
     * @return
     */
    private OapiUserListbypageResponse getUserByDepartmentWithPage(long departmentId, String corpId, long offset, long pageSize) {
        log.info("调用钉钉部门用户列表接口， 参数：departmentId: {}, corpId: {}, offset: {}, pageSize: {}", departmentId, corpId, offset, pageSize);
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/user/listbypage");
        OapiUserListbypageRequest request = new OapiUserListbypageRequest();
        request.setDepartmentId(departmentId);
        request.setOffset(offset);
        request.setSize(pageSize);
        request.setOrder("entry_desc");
        request.setHttpMethod("GET");
        try {
            OapiUserListbypageResponse response = client.execute(request, accessToken);
            log.info("调用钉钉部门用户列表接口完成， 返回结果：{}", response.getBody());
            if (response.isSuccess()) {
                return response;
            }
        } catch (ApiException e) {
            log.error("调用钉钉部门用户列表异常", e);
        }
        return null;
    }

    @Override
    public String getAuthUserId(String corpId, String authCode) {
        log.info("调用钉钉获取登录用户ID接口，参数: corpId: {}, authCode: {}", corpId, authCode);
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/user/getuserinfo");
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(authCode);
        request.setHttpMethod("GET");
        OapiUserGetuserinfoResponse response;
        try {
            response = client.execute(request, accessToken);
            log.info("调用钉钉获取登录用户ID接口完成，参数: corpId: {}, authCode: {}，result: {}", corpId, authCode, response.getBody());
            if (response.isSuccess()) {
                return response.getUserid();
            }
        } catch (Exception e) {
            log.error("调用钉钉获取用户ID接口异常：", e);
        }
        return null;
    }

    @Override
    public String getAuthFreeLoginLabel(String corpId, String authCode, String companyId, String freeLabel, OpenThirdScriptConfig freeAccountConfig) {
        log.info("调用钉钉获取登录用户免登信息接口，参数: corpId: {}, authCode: {}", corpId, authCode);
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/user/getuserinfo");
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(authCode);
        request.setHttpMethod("GET");
        OapiUserGetuserinfoResponse response;
        try {
            response = client.execute(request, accessToken);
            log.info("调用钉钉获取登录用户免登接口完成，参数: corpId: {}, authCode: {}，result: {}", corpId, authCode, response.getBody());
            if (!response.isSuccess()){
                return null;
            }
            OapiUserGetResponse oapiUserGetResponse = getUserDetailInfo(client,proxyUrl,response.getUserid(),accessToken);
            log.info("调用钉钉用户详情接口完成, 返回结果: {} ", response.getBody());
            if( oapiUserGetResponse != null ) {
                Map<String, Object> jsonObject = JsonUtils.toObj(JsonUtils.toJson(oapiUserGetResponse), new TypeReference<Map<String, Object>>() {
                });
                Map<String,String> extParams = new HashMap<>();
                extParams.put(FreeLoginConstant.FREE_LOGIN,freeLabel);
                return FreeLoginProcessUtil.userInfoProcess(freeAccountConfig,jsonObject,extParams);
            }
            return null;
        } catch (Exception e) {
            log.error("调用钉钉获取用户信息接口异常：", e);
        }
        return null;
    }

    public OapiUserGetResponse getUserDetailInfo( DingTalkClient client , String proxyUrl, String userId , String accessToken){
        try {
            OapiUserGetRequest oapiUserRequest = new OapiUserGetRequest();
            ((DefaultDingTalkClient) client).resetServerUrl(proxyUrl + "/user/get");
            oapiUserRequest.setUserid(userId);
            oapiUserRequest.setHttpMethod("GET");
            return client.execute(oapiUserRequest, accessToken);
        } catch (Exception e){
            log.error("获取用户详情失败 : {}",e.getMessage());
            return null;
        }
    }

    @Override
    public LoginResVO getLoginInfo(DingtalkFreeLoginDto freeLoginDto){
        String companyId = freeLoginDto.getCompanyId();
        String configKey = freeLoginDto.getConfigKey();
        String freeLoginLabelValue = freeLoginDto.getFreeLoginLabelValue();
        boolean hasScriptConfig = freeLoginDto.isHasScriptConfig();
        String userId = freeLoginDto.getUserId();
        UcFetchEmployInfoReqDto ucFetchEmployInfoReqDto = new UcFetchEmployInfoReqDto();
        ucFetchEmployInfoReqDto.setCompanyId(companyId);
        if (hasScriptConfig){
            if ("phone".equals(configKey)){
                ucFetchEmployInfoReqDto.setPhone(freeLoginLabelValue);
            }
        } else {
            ucFetchEmployInfoReqDto.setEmployeeId(userId);
        }
        LoginResVO loginResVO = openEmployeeService.fetchLoginAuthInfoByPhoneNum(ucFetchEmployInfoReqDto,CompanyLoginChannelEnum.DINGTALK_H5);
        return loginResVO;
    }

    @Override
    public String getDingtalkUserIdByPhoneNum(String corpId, String phoneNum) {
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/user/get_by_mobile");
        OapiUserGetByMobileRequest request = new OapiUserGetByMobileRequest();
        request.setMobile(String.valueOf(phoneNum));
        request.setHttpMethod("GET");
        try {
            log.info("根据手机号获取钉钉 userid 接口开始，参数: {}", JsonUtils.toJson(request));
            OapiUserGetByMobileResponse response = client.execute(request, accessToken);
            log.info("根据手机号获取钉钉 userid 接口完成，返回结果: {}", response.getBody());
            return response.getUserid();
        } catch (Exception e) {
            log.error("调用钉钉查询父部门ID列表接口异常：", e);
        }
        return null;
    }
}
