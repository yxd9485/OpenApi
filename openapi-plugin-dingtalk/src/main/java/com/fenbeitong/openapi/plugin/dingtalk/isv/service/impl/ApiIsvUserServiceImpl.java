package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.request.OapiUserListbypageRequest;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.dingtalk.api.response.OapiUserListbypageResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUserPageResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiUserService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IApiIsvUserService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: ApiUserServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/04/07 2:27 PM
 */
@Slf4j
@ServiceAspect
@Service
public class ApiIsvUserServiceImpl implements IApiIsvUserService {

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;


    @Value("${dingtalk.host}")
    private String dingtalkHost;

    public OapiUserGetResponse getUserWithOriginal(String corpId, String userId) {
        log.info("调用钉钉用户详情接口，参数: corpId: {}, userId: {}", corpId, userId);
        String accessToken = dingtalkIsvCompanyAuthService.getCorpAccessTokenByCorpId(corpId);
        DingTalkClient client = new DefaultDingTalkClient(dingtalkHost + "/user/get");
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
        String accessToken = dingtalkIsvCompanyAuthService.getCorpAccessTokenByCorpId(corpId);
        DingTalkClient client = new DefaultDingTalkClient(dingtalkHost + "/user/listbypage");
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
        String accessToken = dingtalkIsvCompanyAuthService.getCorpAccessTokenByCorpId(corpId);
        DingTalkClient client = new DefaultDingTalkClient(dingtalkHost + "/user/getuserinfo");
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(authCode);
        request.setHttpMethod("GET");
        OapiUserGetuserinfoResponse response;
        try {
            response = client.execute(request, accessToken);
            log.info("调用钉钉获取登录用户ID接口完成，参数: corpId: {}, authCode: {}，result: {}", corpId, authCode, response.getBody());
            if (response.isSuccess()) {
                //return response.getUserid();
                // 获取企业人员三方id名称配置
                String thirdEmployeeIdName = openSysConfigService.getOpenSysConfigByTypeCode(OpenSysConfigCode.TYPE_THIRD_EMPLOYEE_ID_NAME.getCode(), corpId);
                if (!StringUtils.isBlank(thirdEmployeeIdName)) {
                    OapiUserGetRequest oapiUserRequest = new OapiUserGetRequest();
                    //DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/user/get");
                    ((DefaultDingTalkClient) client).resetServerUrl(dingtalkHost + "/user/get");
                    oapiUserRequest.setUserid(response.getUserid());
                    oapiUserRequest.setHttpMethod("GET");
                    OapiUserGetResponse oapiUserGetResponse = client.execute(oapiUserRequest, accessToken);
                    log.info("调用钉钉用户详情接口完成, 返回结果: {}", response.getBody());
                    if(oapiUserGetResponse!=null) {
                        // 取自定义三方id
                        JSONObject jsonObject = JSONObject.parseObject(oapiUserGetResponse.getExtattr());
                        String thirdEmployeeId = (String) jsonObject.get(thirdEmployeeIdName);
                        if (!StringUtils.isBlank(thirdEmployeeId)) {
                            return thirdEmployeeId;
                        } else {
                            log.info("已配置自定义人员三方id，但未从钉钉获取到值，将使用userId字段。corpId={}, userInfo={}", corpId, JsonUtils.toJson(oapiUserGetResponse));
                            return response.getUserid();
                        }
                    }
                }
                return response.getUserid();
            }
        } catch (Exception e) {
            log.error("调用钉钉获取用户ID接口异常：", e);
        }
        return null;
    }
}
