package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.enums.FlowStatusTypeEnums;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.enums.ViewTypeEnums;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.todo.request.EcologyCreateToDoReqDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.todo.request.EcologyDeleteToDoReqDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.EcologyPostUtils;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyToDoService;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.support.webhook.dto.WebHookOrderDTO;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;

/**
 * 泛微待办接口
 * @Auther zhang.peng
 * @Date 2021/12/7
 */

@Service
@Slf4j
@ServiceAspect
public class EcologyToDoServiceImpl implements IEcologyToDoService {

    /**
     * 泛微系统待办标识
     */
    private static final String ECOLOGY_CODE = "FBT";

    /**
     * 泛微免登路由(有订单类型)
     */
    private static final String ECOLOGY_APP_HOME = "/panMicroLogin?companyId=%s&phone=%s&";

    @Autowired
    private EcologyPostUtils ecologyPostUtils;

    @Override
    public boolean createEcologyToDo( WebHookOrderDTO webHookOrderDTO ){
        if ( null == webHookOrderDTO ){
            log.info("转换 webHookDTO 为空");
            return false;
        }
        EcologyCreateToDoReqDTO createToDoReqDTO = buildCommonToDoReqDTO(webHookOrderDTO);
        // 审批结束时间为空 = 过程中数据
        createToDoReqDTO.setIsRemark(FlowStatusTypeEnums.TODO.getCode()+"");
        return ecologyPostUtils.createToDo(createToDoReqDTO,webHookOrderDTO.getCompanyId());
    }

    @Override
    public boolean finishEcologyToDo( WebHookOrderDTO webHookOrderDTO ){
        if ( null == webHookOrderDTO ){
            log.info("转换 webHookDTO 为空");
            return false;
        }
        EcologyCreateToDoReqDTO createToDoReqDTO = buildCommonToDoReqDTO(webHookOrderDTO);
        createToDoReqDTO.setIsRemark(FlowStatusTypeEnums.DONE.getCode()+"");
        return ecologyPostUtils.createToDo(createToDoReqDTO,webHookOrderDTO.getCompanyId());
    }

    @Override
    public boolean deleteEcologyToDo( WebHookOrderDTO webHookOrderDTO ){
        if ( null == webHookOrderDTO ){
            log.info("转换 webHookDTO 为空");
            return false;
        }
        EcologyDeleteToDoReqDTO deleteToDoReqDTO = buildDeleteReqDTO(webHookOrderDTO);
        return ecologyPostUtils.deleteToDo(deleteToDoReqDTO,webHookOrderDTO.getCompanyId());
    }

    public EcologyCreateToDoReqDTO buildCommonToDoReqDTO(WebHookOrderDTO webHookOrderDTO){
        String url = buildPcAndAppUrl(webHookOrderDTO.getCompanyId(),webHookOrderDTO.getApproverPhone());
        url = convertUrl(webHookOrderDTO,url);
        try {
            String preUrl = url.substring(0,url.indexOf("url")+4);
            // 对 url等号后面的 Encode
            String encodeUrl = URLEncoder.encode(url.substring(url.indexOf("url")+4,url.length()), "UTF-8");
            url = preUrl + encodeUrl;
        } catch (Exception e){
            log.info("encode 失败 {}",e.getMessage());
        }
        return EcologyCreateToDoReqDTO
            .builder()
            .flowId(webHookOrderDTO.getProcessInstanceId())
            .requestName(webHookOrderDTO.getProcessDefName())
            .workflowName(webHookOrderDTO.getProcessDefName())
            .nodeName(webHookOrderDTO.getNodeName())
            .viewType(ViewTypeEnums.UN_READ.getCode())
            .creator(webHookOrderDTO.getStarterId())
            .createDateTime(null != webHookOrderDTO.getProcessStartTime() ? DateUtils.toSimpleStr(webHookOrderDTO.getProcessStartTime()) : DateUtils.toSimpleStr(new Date()))
            .receiver(webHookOrderDTO.getApproverId())
            .receiveDateTime(null != webHookOrderDTO.getProcessStartTime() ? DateUtils.toSimpleStr(webHookOrderDTO.getProcessStartTime()) : DateUtils.toSimpleStr(new Date()))
            .receivets(String.valueOf(System.currentTimeMillis()))
            .pcUrl(url)
            .appUrl(url)
            .sysCode(ECOLOGY_CODE)
            .build();
    }

    public String convertUrl(WebHookOrderDTO webHookOrderDTO , String url){
        HashMap<String, Object> eventMsgMap = Maps.newHashMap();
        eventMsgMap.put("setting_type",webHookOrderDTO.getApplyOrderType());
        eventMsgMap.put("id",webHookOrderDTO.getApplyOrderId());
        eventMsgMap.put("apply_type",webHookOrderDTO.getApplyType());
        //1.审批人，2申请人，3抄送人
        eventMsgMap.put("view_type",webHookOrderDTO.getViewType());
        //订单类型（具体场景）
        eventMsgMap.put("order_type",webHookOrderDTO.getOrderType());
        return MessagePushUtils.initApplicationUrl(eventMsgMap, url);
    }

    public String buildPcAndAppUrl( String companyId , String phone ){
        //1.行程审批单 2.订单审批单 3.采购审批单 4.分贝券 5.用餐审批 6.退改审批 7.外卖审批 8.虚拟卡额度 9.核销申请 10商务核销 11.付款申请 12.备用金 13.里程
        return String.format(ECOLOGY_APP_HOME, companyId, phone);
    }

    public EcologyDeleteToDoReqDTO buildDeleteReqDTO( WebHookOrderDTO webHookOrderDTO ){
        EcologyDeleteToDoReqDTO deleteToDoReqDTO = new EcologyDeleteToDoReqDTO();
        deleteToDoReqDTO.setFlowId(webHookOrderDTO.getProcessInstanceId());
        deleteToDoReqDTO.setUserId(webHookOrderDTO.getStarterId());
        return deleteToDoReqDTO;
    }

}
