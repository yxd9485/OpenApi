package com.fenbeitong.openapi.plugin.dingtalk.common.util;

import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiMessageCorpconversationSendbytemplateRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationStatusBarUpdateRequest;
import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvMeassgeDataDTO;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/8/6 下午3:23
 */

public class DingtalkMessageUtil {

    /**
     * 推送OA消息体数据
     * @param kafkaPushMsg
     * @param thirdEmployeeId
     * @param agentId
     * @param uri
     * @return
     */
    public static OapiMessageCorpconversationAsyncsendV2Request setOaMsg(KafkaPushMsg kafkaPushMsg , String thirdEmployeeId , long agentId ,String uri){
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(thirdEmployeeId);
        request.setAgentId(agentId);
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setOa( new OapiMessageCorpconversationAsyncsendV2Request.OA());
        msg.getOa().setHead( new OapiMessageCorpconversationAsyncsendV2Request.Head());
        msg.getOa().setBody( new OapiMessageCorpconversationAsyncsendV2Request.Body());
        msg.getOa().setStatusBar( new OapiMessageCorpconversationAsyncsendV2Request.StatusBar());
        String messageUrl = MessagePushUtils.messageUrlTransfer(kafkaPushMsg, uri);
        msg.getOa().setMessageUrl(messageUrl);
        msg.getOa().setPcMessageUrl(messageUrl);
        msg.getOa().getHead().setText(title);
        msg.getOa().getHead().setBgcolor("FFBBBBBB");
        msg.getOa().getBody().setTitle(content);
        msg.getOa().getStatusBar().setStatusValue(title);
        msg.getOa().getStatusBar().setStatusBg("0xFF130c0e");
        msg.setMsgtype("oa");
        request.setMsg(msg);
        return request;
    }

    //修改消息消息体
    public static OapiMessageCorpconversationStatusBarUpdateRequest setUpdateMsg( long taskId , long agentId ){
        OapiMessageCorpconversationStatusBarUpdateRequest request = new OapiMessageCorpconversationStatusBarUpdateRequest();
        request.setAgentId(agentId);
        request.setTaskId(taskId);
        request.setStatusValue("已处理");
        request.setStatusBg("0xFF78C06E");
        return request;
    }

    /**
     *
     * @param kafkaPushMsg
     * @param thirdEmployeeId
     * @param agentId
     * @param uri
     * @return
     */
    public static OapiMessageCorpconversationSendbytemplateRequest setTemplateMessage(KafkaPushMsg kafkaPushMsg , String thirdEmployeeId , long agentId ,
                                                                                      String uri,String messageTemplateId){
        OapiMessageCorpconversationSendbytemplateRequest req = new OapiMessageCorpconversationSendbytemplateRequest();
        req.setAgentId( agentId );
        req.setUseridList( thirdEmployeeId );
        req.setTemplateId( messageTemplateId );
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        String messageUrl = MessagePushUtils.messageUrlTransfer(kafkaPushMsg, uri);
        DingtalkIsvMeassgeDataDTO meassgeData = DingtalkIsvMeassgeDataDTO.builder().title(title).content(content).link_pc(messageUrl).link_mobile(messageUrl).build();
        req.setData(JsonUtils.toJson( meassgeData ));
        return req;
    }


}
