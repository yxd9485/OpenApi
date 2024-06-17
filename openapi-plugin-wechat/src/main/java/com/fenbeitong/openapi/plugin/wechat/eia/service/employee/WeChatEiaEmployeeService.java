package com.fenbeitong.openapi.plugin.wechat.eia.service.employee;

import com.fenbeitong.finhub.common.utils.FinhubLogger;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatLinkedCorpUserDetailDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatLinkedCorpUserlistDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatUserListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatGetUserResponse;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.luastar.swift.base.net.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Z.H.W on 20/02/18.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatEiaEmployeeService {

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Autowired
    private RestHttpUtils httpUtil;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    /**
     * 根据根部门ID获取企业微信人员数据
     *
     * @param qywxAccessToken token
     * @param deptId          部门id
     * @param fetchChild      是否递归子部门： 1-递归获取，0-只获取本部门
     * @return
     */
    public List<Map<String, Object>> getQywxAllUserByDepId(String qywxAccessToken, String deptId, String fetchChild) {
        //1.调用企业微信API获取token，
        String qywxAllUserUrl = wechatHost + "/cgi-bin/user/list?access_token=" + qywxAccessToken + "&department_id=" + deptId + "&fetch_child=" + fetchChild;
        log.info("根据企业微信部门ID获取用户集合地址 {}", qywxAllUserUrl);
        //3.返回token
        String userListInfo = httpUtil.postJson(qywxAllUserUrl, null);
        log.info("根据企业微信部门ID获取用户集合返回数据 {}", userListInfo);
        if (StringUtils.isBlank(userListInfo)) {
            log.info("企业微信用户集合数据为空");
        }
        Map map = JsonUtils.toObj(userListInfo, Map.class);
        Integer errcode = (Integer) map.get("errcode");
        List userList = null;
        if (errcode == 0) {//返回是否成功标识
            userList = (List) map.get("userlist");
        } else {
            Integer errcode1 = (Integer) map.get("errcode");
            String errmsg = (String) map.get("errmsg");
            throw new OpenApiPluginException(errcode1, errmsg);
        }
        return userList;
    }

    /**
     * 根据根部门ID获取企业微信人员数据
     *
     * @param qywxAccessToken token
     * @param deptId          部门id
     * @param fetchChild      是否递归子部门： 1-递归获取，0-只获取本部门
     * @return
     */
    public WechatUserListRespDTO getAllUserByDepId(String qywxAccessToken, String deptId, String fetchChild) {
        //1.调用企业微信API获取token，
        String qywxAllUserUrl = wechatHost + "/cgi-bin/user/list?access_token=" + qywxAccessToken + "&department_id=" + deptId + "&fetch_child=" + fetchChild;
        log.info("根据企业微信部门ID获取用户集合地址 {}", qywxAllUserUrl);
        //3.返回token
        String userListInfo = httpUtil.postJson(qywxAllUserUrl, null);
        log.info("根据企业微信部门ID获取用户集合返回数据 {}", userListInfo);
        if (StringUtils.isBlank(userListInfo)) {
            log.info("企业微信用户集合数据为空");
        }
        return JsonUtils.toObj(userListInfo, WechatUserListRespDTO.class);
    }

    /**
     * 根据企业微信用户ID查询用户详情
     *
     * @param accessToken
     * @param userId
     * @return
     */
    public WeChatGetUserResponse getQywxUserDetailByUserId(String accessToken, String userId, String corpId) {
        //1.调用企业微信API获取token，
        String qywxAllUserUrl = wechatHost + "/cgi-bin/user/get?access_token=" + accessToken + "&userid=" + userId;
        FinhubLogger.info("根据企业微信用户ID查询用户详情请求参数 {}", qywxAllUserUrl);
        //3.返回token
        String userListInfo = HttpClientUtils.get(qywxAllUserUrl, 3000);
        FinhubLogger.info("根据企业微信用户ID查询用户详情返回结果 {}", userListInfo);
        WeChatGetUserResponse weChatGetUserResponse = JsonUtils.toObj(userListInfo, WeChatGetUserResponse.class);
        if (weChatGetUserResponse == null || (Optional.ofNullable(weChatGetUserResponse.getErrCode()).orElse(-1) != 0)) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_CORP_EMPLOYEE_IS_NULL));
        }
        transThirdEmployeeId(weChatGetUserResponse, corpId);
        return weChatGetUserResponse;
    }

    public void transThirdEmployeeId(WeChatGetUserResponse weChatGetUserResponse, String corpId) {
        // 获取企业人员三方id名称配置
        String thirdEmployeeIdName = openSysConfigService.getOpenSysConfigByTypeCode(OpenSysConfigCode.TYPE_THIRD_EMPLOYEE_ID_NAME.getCode(), corpId);
        if (!StringUtils.isBlank(thirdEmployeeIdName)) {
            // 取自定义三方id
            String thirdEmployeeId = weChatGetUserResponse.getAttrValueByAttrName(thirdEmployeeIdName, "");
            if (!com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(thirdEmployeeId)) {
                weChatGetUserResponse.setUserId(thirdEmployeeId);
            } else {
                log.info("已配置自定义人员三方id，但未从企业微信获取到值，将使用userId字段。corpId={}, userInfo={}", corpId, JsonUtils.toJson(weChatGetUserResponse));
            }
        }
    }

    /**
     * 根据企业微信用户ID查询用户详情，并重置三方id为自定义的三方id
     * 如果有配置并且能获取重置后的userid，则返回重置后的userid，没有则返回原值，
     *
     * @param accessToken
     * @param userId
     * @return
     */
    public String getThirdEmployeeIdByUserId(String accessToken, String userId, String corpId) {
        // 获取企业人员三方id名称配置
        String thirdEmployeeIdName = openSysConfigService.getOpenSysConfigByTypeCode(OpenSysConfigCode.TYPE_THIRD_EMPLOYEE_ID_NAME.getCode(), corpId);
        if (!StringUtils.isBlank(thirdEmployeeIdName)) {
            //1.调用企业微信API获取token，
            String qywxAllUserUrl = wechatHost + "/cgi-bin/user/get?access_token=" + accessToken + "&userid=" + userId;
            FinhubLogger.info("根据企业微信用户ID查询用户详情请求参数 {}", qywxAllUserUrl);
            //3.返回token
            String userListInfo = HttpClientUtils.get( qywxAllUserUrl, 3000);
            FinhubLogger.info("根据企业微信用户ID查询用户详情返回结果 {}", userListInfo);
            WeChatGetUserResponse weChatGetUserResponse = JsonUtils.toObj(userListInfo, WeChatGetUserResponse.class);
            if (weChatGetUserResponse == null || (Optional.ofNullable(weChatGetUserResponse.getErrCode()).orElse(-1) != 0)) {
                throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_CORP_EMPLOYEE_IS_NULL));
            }
            // 取自定义三方id
            String thirdEmployeeId = weChatGetUserResponse.getAttrValueByAttrName(thirdEmployeeIdName, "");
            if (StringUtils.isBlank(thirdEmployeeId)){
                log.info("已配置自定义人员三方id，但未从企业微信获取到值，将使用userId字段。corpId={}, userInfo={}", corpId, JsonUtils.toJson(weChatGetUserResponse));
            }else {
                log.info("userId原值:{}，被重置为thirdEmployeeId:{}",userId,thirdEmployeeId);
                return thirdEmployeeId;
            }
        }
        log.info("userId未被重置，返回原值userId:{}",userId);
        return userId;
    }

    public WeChatGetUserResponse getUserResponse(String accessToken, String userId) {
        try {
            String qywxAllUserUrl = wechatHost + "/cgi-bin/user/get?access_token=" + accessToken + "&userid=" + userId;
            FinhubLogger.info("根据企业微信用户ID查询用户详情请求参数 {}", qywxAllUserUrl);
            //3.返回token
            String userListInfo = HttpClientUtils.get(qywxAllUserUrl, 3000);
            FinhubLogger.info("根据企业微信用户ID查询用户详情返回结果 {}", userListInfo);
            WeChatGetUserResponse weChatGetUserResponse = JsonUtils.toObj(userListInfo, WeChatGetUserResponse.class);
            return weChatGetUserResponse;
        } catch (Exception e) {
            log.info("用户信息查询为空 : {}", e.getMessage());
            return null;
        }

    }

    /**
     * @Description 获取互联企业部门成员详情
     * @Author duhui
     * @Date 2022/1/14
     **/
    public WeChatLinkedCorpUserlistDTO getLinkedCorpUserlist(String accessToken, String departmentId) {
        String url = wechatHost + "/cgi-bin/linkedcorp/user/list?access_token=" + accessToken;
        Map<String, String> map = new HashMap(4) {{
            put("department_id", departmentId);
        }};
        String data = HttpClientUtils.postBody(url, JsonUtils.toJson(map));
        log.info("查询互联企业人员返回结果：departmentId:{},data: {}", departmentId, data);
        WeChatLinkedCorpUserlistDTO weChatLinkedCorpUserlistDTO = new WeChatLinkedCorpUserlistDTO();
        if (!StringUtils.isBlank(data)) {
            weChatLinkedCorpUserlistDTO = JsonUtils.toObj(data, WeChatLinkedCorpUserlistDTO.class);
        }
        return weChatLinkedCorpUserlistDTO;
    }

    /**
     * @Description 获取互联企业成员详情
     * @Author helu
     * @Date 2022/6/29
     **/
    public WeChatLinkedCorpUserDetailDTO getLinkedCorpUserDetail(String accessToken, String userId) {
        String url = wechatHost + "/cgi-bin/linkedcorp/user/get?access_token=" + accessToken;
        Map<String, String> map = new HashMap(4) {{
            put("userid", userId);
        }};
        String data = HttpClientUtils.postBody(url, JsonUtils.toJson(map));
        log.info("查询互联企业人员详情返回结果：userId:{},data: {}", userId, data);
        WeChatLinkedCorpUserDetailDTO weChatLinkedCorpUserlistDTO = new WeChatLinkedCorpUserDetailDTO();
        if (!StringUtils.isBlank(data)) {
            weChatLinkedCorpUserlistDTO = JsonUtils.toObj(data, WeChatLinkedCorpUserDetailDTO.class);
        }
        return weChatLinkedCorpUserlistDTO;
    }

}
