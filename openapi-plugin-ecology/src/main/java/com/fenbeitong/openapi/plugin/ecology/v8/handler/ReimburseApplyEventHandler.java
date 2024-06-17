package com.fenbeitong.openapi.plugin.ecology.v8.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.finhub.common.constant.saas.ApplyType;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.saas.dto.ApplyPushEvents;
import com.fenbeitong.openapi.plugin.support.reimbursement.dto.ReimburseDataDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.reimbursement.dto.ReimburseFormDataDetailDTO;
import com.fenbeitong.openapi.plugin.support.reimbursement.service.ReimbursementService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description 自定义报销单处理
 * @Author 张鹏
 * @Date 2022/1/25
 **/
@Component
@Slf4j
public class ReimburseApplyEventHandler extends EventHandler<ApplyPushEvents> {

    @Autowired
    private ReimbursementService reimbursementService;

    private static final String REVOKE = "revoke";

    @Override
    public boolean process(ApplyPushEvents event , Object... args) {
        log.info("ReimburseApplyEvent msg : {} ",JsonUtils.toJson(event));
        if (null == event || StringUtils.isBlank(event.getMsg()) ) {
            log.info("报销单消息为空 , 不执行 , eventData {}", JsonUtils.toJson(event));
            return false;
        }
        if (REVOKE.equals(event.getMsgType())){
            log.info("该审批为撤销状态 , 不执行 : {}");
            return false;
        }
        Msg msg = JsonUtils.toObj(event.getMsg(),Msg.class);
        if (!(ApplyType.CustomReimburse.getValue()+"").equals(msg.getApplyType())){
            log.info("非报销单消息 , 不执行 , eventData {}", JsonUtils.toJson(event));
            return false;
        }
        // 查询自定义报销单详情
        ReimburseDataDetailReqDTO dataDetailReqDTO = new ReimburseDataDetailReqDTO();
        dataDetailReqDTO.setApplyId(event.getApplyId());
        dataDetailReqDTO.setCompanyId(event.getCompanyId());
        dataDetailReqDTO.setUserId(event.getUserId());
        Map<String, Object> reimburseData = reimbursementService.getFormApproveDataInfo(dataDetailReqDTO);
        if (null == reimburseData) {
            log.info("报销单详情数据为空");
            return false;
        }
        ReimburseFormDataDetailDTO reimburseFormDataDetailDTO = JsonUtils.toObj(JsonUtils.toJson(reimburseData), ReimburseFormDataDetailDTO.class);
        if (null == reimburseFormDataDetailDTO) {
            log.info("报销单详情 data 为空");
            return false;
        }
        // 填充数据 , 并落库
        dataDetailReqDTO.setFormId(null == reimburseFormDataDetailDTO.getFormInfo() ? "" : reimburseFormDataDetailDTO.getFormInfo().getId());
        reimbursementService.filterData(dataDetailReqDTO, reimburseFormDataDetailDTO, event.getUserId());
        return true;
    }

    @Data
    public static class Msg{

        @JsonProperty("apply_type")
        private String applyType;

    }
}
