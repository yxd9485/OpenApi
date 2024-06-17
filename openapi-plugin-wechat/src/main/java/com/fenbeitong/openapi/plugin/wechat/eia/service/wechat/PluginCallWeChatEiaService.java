package com.fenbeitong.openapi.plugin.wechat.eia.service.wechat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.*;
import com.fenbeitong.openapi.plugin.wechat.eia.enums.WeChatApplyContentControl;
import com.fenbeitong.openapi.plugin.wechat.eia.service.employee.WeChatEiaEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatGetUserResponse;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dave.hansins on 19/12/14.
 */
@ServiceAspect
@Service
@Slf4j
public class PluginCallWeChatEiaService {


    private static final String TRIP_USER = "出行人";


    @Value("${wechat.api-host}")
    private String wechatHost;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private WeChatEiaEmployeeService weChatEiaEmployeeService;
    @Autowired
    RestHttpUtils restHttpUtils;

    /**
     * 根据企业ID和secret获取access_token
     *
     * @param corpId
     * @param corpSecret
     * @return
     * @throws IOException
     */
    public WeChatToken getWeChatCorpAccessToken(String corpId, String corpSecret) {
        String url = wechatHost + "/cgi-bin/gettoken?" + "corpid=" + corpId + "&corpsecret=" + corpSecret;
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        final Call call = okHttpClient.newCall(request);
        ResponseBody body = null;
        String result = "";
        try {
            Response response = call.execute();
            log.info("返回结果 {}", JsonUtils.toJson(response));
            body = response.body();
            result = body.string();
            log.info("返回结果解析 {}", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        WeChatToken weChatToken = JsonUtils.toObj(result, WeChatToken.class);
        return weChatToken;
    }

    /**
     * 根据企业ID和secret获取access_token
     *
     * @param corpId
     * @param corpSecret
     * @return
     */
    public String getWeChatToken(String corpId, String corpSecret) {
        WeChatToken weChatToken = getWeChatCorpAccessToken(corpId, corpSecret);
        return weChatToken == null ? null : weChatToken.getAccessToken();
    }


    /**
     * 根据审批单详情查询审批单详情
     *
     * @param accessToken
     * @param spNo
     * @return
     */
    public WeChatApprovalDetail getWeChatApprovalDetailBySpNo(String accessToken, String spNo, String corpId) {
        String url = wechatHost + "/cgi-bin/oa/getapprovaldetail?access_token=" + accessToken;
        OkHttpClient okHttpClient = new OkHttpClient();
        HashMap<String, String> paramMap = Maps.newHashMap();
        paramMap.put("sp_no", spNo);
        MediaType parse = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(parse, JsonUtils.toJson(paramMap));
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        final Call call = okHttpClient.newCall(request);
        ResponseBody body = null;
        String result = "";
        try {
            Response response = call.execute();
            log.info("返回结果 {}", JsonUtils.toJson(response));
            body = response.body();
            result = body.string();
            log.info("返回企业微信审批单详情结果解析 {}", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        WeChatApprovalDetail weChatApprovalDetail = JsonUtils.toObj(result, WeChatApprovalDetail.class);
        transThirdEmployeeId(weChatApprovalDetail, accessToken, corpId);
        return weChatApprovalDetail;
    }


    /**
     * 申请人人员id转自定义三方id
     *
     * @param weChatApprovalDetail
     * @param accessToken
     * @param corpId
     */
    private void transThirdEmployeeId(WeChatApprovalDetail weChatApprovalDetail, String accessToken, String corpId) {
        if (ObjectUtils.isEmpty(weChatApprovalDetail)) {
            throw new OpenApiPluginException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_APPROVOL_IS_NULL));
        }
        // 获取企业人员三方id名称配置，如果配置了自定义人员三方id，要重新接取人员，转换成自定义id
        String thirdEmployeeIdName = openSysConfigService.getOpenSysConfigByTypeCode(OpenSysConfigCode.TYPE_THIRD_EMPLOYEE_ID_NAME.getCode(), corpId);
        if (!com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(thirdEmployeeIdName)) {
            //转申请人
            WeChatApprovalDetail.WeChatApprovalInfo weChatApprovalInfo = weChatApprovalDetail.getWeChatApprovalInfo();
            WeChatApprovalDetail.Applyer applyer = weChatApprovalInfo.getApplyer();
            String applyUserId = applyer.getUserId();
            WeChatGetUserResponse qywxUserDetailByUserId = weChatEiaEmployeeService.getQywxUserDetailByUserId(accessToken, applyUserId, corpId);
            applyer.setUserId(qywxUserDetailByUserId.getUserId());
            //转出行人
            List<WeChatApprovalDetail.Content> contens = weChatApprovalInfo.getApplyData().getContens();
            if (!ObjectUtils.isEmpty(contens)) {
                for (WeChatApprovalDetail.Content content : contens) {
                    String control = content.getControl();
                    //同行人信息
                    if (WeChatApplyContentControl.CONTACT.getValue().equals(control)) {
                        List<WeChatApprovalDetail.Title> titles = content.getTitles();
                        WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                        String text = titles.get(0).getText();
                        if (TRIP_USER.equals(text)) {
                            List<WeChatApprovalDetail.Member> members = contentValue.getMember();
                            for (WeChatApprovalDetail.Member member : members) {
                                //同行人信息
                                String userid = member.getUserid();
                                if (applyUserId.equals(userid)) {
                                    member.setUserid(applyer.getUserId());
                                } else {
                                    qywxUserDetailByUserId = weChatEiaEmployeeService.getQywxUserDetailByUserId(accessToken, userid, corpId);
                                    member.setUserid(qywxUserDetailByUserId.getUserId());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 通过企业微信API创建审批单
     *
     * @param accessToken
     * @param weChatCreateApprovalReqDTO
     * @return
     */
    public WeChatCreateApprovalRespDTO createWechatEiaApproval(String accessToken, WeChatCreateApprovalReqDTO weChatCreateApprovalReqDTO) {
        String url = wechatHost + "/cgi-bin/oa/applyevent?access_token=" + accessToken;
        String reqStr = JsonUtils.toJsonSnake(weChatCreateApprovalReqDTO);
        String s = restHttpUtils.postJson(url, reqStr);
        WeChatCreateApprovalRespDTO weChatCreateApprovalRespDTO = JsonUtils.toObj(s, WeChatCreateApprovalRespDTO.class);
        return weChatCreateApprovalRespDTO;
    }


    /**
     * 根据模板ID查询模板详情
     * @param accessToken
     * @param templateId
     * @return
     */
    public WeChatApprovalTemplateDetailRespDTO getWeChatTemplateDetail(String accessToken, String templateId){
         String url = wechatHost + "/cgi-bin/oa/gettemplatedetail?access_token=" + accessToken;
         Map<String,String> templateMap = Maps.newHashMap();
        templateMap.put("template_id",templateId);
        String s = restHttpUtils.postJson(url, JsonUtils.toJson(templateMap));
        WeChatApprovalTemplateDetailRespDTO weChatApprovalTemplateDetailRespDTO = JsonUtils.toObj(s, new TypeReference<WeChatApprovalTemplateDetailRespDTO>() {
        });
        return weChatApprovalTemplateDetailRespDTO;

    }

    /**
     * 根据手机号获取三方人员id
     * @param accessToken
     * @param phoneNum
     * @return
     */
    public WeChatUserIdGetRespDTO getWeChatUserId(String accessToken, String phoneNum){
        String url = wechatHost + "/cgi-bin/user/getuserid?access_token=" + accessToken;
        Map<String,String> phoneNumMap = Maps.newHashMap();
        phoneNumMap.put("mobile",phoneNum);
        String userIdRes = restHttpUtils.postJson(url, JsonUtils.toJson(phoneNumMap));
        WeChatUserIdGetRespDTO weChatUserIdGetRespDTO = JsonUtils.toObj(userIdRes, new TypeReference<WeChatUserIdGetRespDTO>() {
        });
        return weChatUserIdGetRespDTO;
    }

}
