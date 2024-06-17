package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonApiConstant;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccessTokenReq;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonThirdpartyPendingRequest;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonThirdpartyPendingStateRequest;
import com.fenbeitong.openapi.plugin.seeyon.dto.WorkRecordData;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.enums.TaskStautsEnum;
import com.fenbeitong.openapi.plugin.seeyon.service.*;
import com.fenbeitong.openapi.plugin.seeyon.utils.AESUtils;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcFetchEmployInfoReqDto;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.webhook.dto.WebHookOrderDTO;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * 致远待办
 * @Auther xiaohai
 * @Date 2022/09/26
 */
@Slf4j
@ServiceAspect
@Service
public class SeeyonWorkRecordServiceImpl implements SeeyonWorkRecordService {

    @Value("${host.webapp}")
    private String webappUrl;

    @Autowired
    SeeyonClientService seeyonClientService;

    @Autowired
    SeeyonAccessTokenService seeyonAccessTokenService;

    @Override
    public void syncWorkRecord(WorkRecordData workRecordData) {
        log.info("接收致远待办数据：{}" , JsonUtils.toJson( workRecordData ));
        WebHookOrderDTO webhookOrder = workRecordData.getWebhookOrder();
        //判断是否是流程结束数据，如果是流程结束数据过滤
        Long processEndTime = webhookOrder.getProcessEndTime();
        Integer viewType = webhookOrder.getViewType();
        if(processEndTime != null || viewType == 3){
            log.info("流程结束和抄送数据过滤 ！！！" );
            return ;
        }
        String companyId = webhookOrder.getCompanyId();
        String taskStatus = webhookOrder.getTaskStatus();
        SeeyonClient seeyonClient = seeyonClientService.getSeeyonClientByCompanyId(companyId);
        if(TaskStautsEnum.PRNFING.getTaskStatus().equals( NumericUtils.obj2int( taskStatus ) )){
            //待处理调用待办新增接口
            addThirdpartyPending( seeyonClient , webhookOrder );
            return ;
        }
        //调用待办更新接口
        updateThirdpartyPendingState(  seeyonClient ,   NumericUtils.obj2int( taskStatus )   , webhookOrder.getTaskId() , webhookOrder.getApplyOrderId() );
    }

    /**
     * 新增待办
     * @param seeyonClient
     * @param webhookOrder
     */
    private void addThirdpartyPending( SeeyonClient seeyonClient , WebHookOrderDTO webhookOrder){

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SeeyonConstant.TOKEN_HEADER, getAccessToken( seeyonClient ));
        //待处理调用待办新增接口
        SeeyonThirdpartyPendingRequest thirdpartyPendingRequest = SeeyonThirdpartyPendingRequest.builder()
            .registerCode(seeyonClient.getRegisterCode())
            .taskId(webhookOrder.getTaskId())
            .title( webhookOrder.getStarterName() + "提交的" + webhookOrder.getApplyOrderDetail().getApplyOrderTypeName() )
            .senderName(webhookOrder.getStarterName())
            .state("0")
            .noneBindingSender(webhookOrder.getStarterPhone())
            .noneBindingReceiver(webhookOrder.getApproverPhone())
            .creationDate(DateUtils.parseDate1Str(webhookOrder.getTaskCreateTime()))
            .h5url( getJumpUrl( webhookOrder ))
            .url( getJumpUrl( webhookOrder ))
            .build();
        log.info("推送致远待办数据：{} , 审批单ID：{} , taskId：{}" , JsonUtils.toJson(thirdpartyPendingRequest) , webhookOrder.getApplyOrderId() , webhookOrder.getTaskId());
        String result = RestHttpUtils.postJson(seeyonClient.getSeeyonSysUri() + SeeyonApiConstant.SEEYON_THIRD_PARTY_RENDING,
            httpHeaders,
            JsonUtils.toJson(thirdpartyPendingRequest));
        log.info("推送致远待办返回结果：{} , 审批单ID：{} , taskId：{}" , result ,  webhookOrder.getApplyOrderId() , webhookOrder.getTaskId());

    }

    private String getJumpUrl( WebHookOrderDTO webhookOrder ){
        UcFetchEmployInfoReqDto userInfo = new UcFetchEmployInfoReqDto();
        userInfo.setCompanyId(webhookOrder.getCompanyId());
        userInfo.setPhone(webhookOrder.getApproverPhone());
        String encrypt = AESUtils.encrypt(JsonUtils.toJson(userInfo), SeeyonConstant.ENCRPT_KEY);
        Map eventMsgMap = new HashMap();
        // 申请单号
        eventMsgMap.put("id" , webhookOrder.getApplyOrderId() );
        // 申请单类型
        eventMsgMap.put("setting_type" , webhookOrder.getApplyOrderType() );
        //二级申请单类型
        eventMsgMap.put("apply_type" , webhookOrder.getApplyType() );
        // 2:审批人
        eventMsgMap.put("view_type" , "2");
        // order_type不能为空，后续跳转判断不能为空，没有用该字段
        eventMsgMap.put("order_type" , "7");
        //虚拟卡自定义页面
        eventMsgMap.put("isVirtualCustomForm" , webhookOrder.getApplyOrderDetail().getIsVirtualCustomForm());
        String url = MessagePushUtils.initApplicationUrl(eventMsgMap, "");
        try {
            url = url.replace("url=","");
            url = URLEncoder.encode(url, "utf-8");
            url = "url=" + url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return webappUrl + SeeyonApiConstant.SEEYON_LOGIN_URL + encrypt  + "&" + url ;
    }


    private void updateThirdpartyPendingState( SeeyonClient seeyonClient ,  Integer taskStatus  , String taskId , String applyId){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SeeyonConstant.TOKEN_HEADER, getAccessToken( seeyonClient ));
        //调用待办更新接口
        SeeyonThirdpartyPendingStateRequest seeyonThirdpartyPendingStateRequest = SeeyonThirdpartyPendingStateRequest.builder()
            .registerCode(seeyonClient.getRegisterCode())
            .taskId( taskId )
            .state("1")
            .subState(StringUtils.obj2str(TaskStautsEnum.parse(NumericUtils.obj2int(taskStatus)).getSeeyonTaskStatus()))
            .build();
        log.info("推送致远更新待办数据：{} , 审批单ID：{} , taskId：{}" , JsonUtils.toJson(seeyonThirdpartyPendingStateRequest) , taskId , applyId);
        String result = RestHttpUtils.postJson(seeyonClient.getSeeyonSysUri() + SeeyonApiConstant.SEEYON_THIRD_PARTY_UPDATE_RENDINGSTATE,
            httpHeaders,
            JsonUtils.toJson(seeyonThirdpartyPendingStateRequest));
        log.info("推送致远更新待办返回结果：{} , 审批单ID：{} , taskId：{} " , result ,  taskId , applyId);

    }

    private String getAccessToken( SeeyonClient seeyonClient ){

        String seeyonUsername = seeyonClient.getSeeyonUsername();
        String seeyonPassword = seeyonClient.getSeeyonPassword();
        SeeyonAccessTokenReq build = SeeyonAccessTokenReq.builder()
            .userName(seeyonUsername)
            .password(seeyonPassword)
            .build();
        String seeyonSysUri = seeyonClient.getSeeyonSysUri();
        String accessToken = seeyonAccessTokenService.getAccessToken(build, seeyonSysUri);
        return accessToken;

    }



}
