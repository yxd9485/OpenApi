package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.finhub.common.constant.ApplyStatus;
import com.fenbeitong.finhub.common.constant.saas.ApplyType;
import com.fenbeitong.finhub.kafka.constant.KafkaTopicEnum;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebHookOrderEvent;
import com.fenbeitong.openapi.plugin.func.reimburse.dto.RemiDetailResDTO;
import com.fenbeitong.openapi.plugin.func.reimburse.service.FuncRemiService;
import com.fenbeitong.openapi.plugin.support.apply.dto.CustformApplyDetailDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenCustformApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackConfDao;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackConf;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.saasplus.api.service.bill.IApplyReimburseBillService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 审批表单推送（审批单、报销单）
 */
@Component
@Slf4j
public class ApprovalFormEventHandler extends EventHandler<WebHookOrderEvent> {

    @DubboReference(check = false)
    private IApplyReimburseBillService applyReimburseBillService;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private ThirdCallbackConfDao callbackConfDao;

    @Autowired
    private FuncRemiService funcRemiService;
    @Autowired
    private OpenCustformApplyServiceImpl openCustformApplyService;

    @Override
    public boolean process(WebHookOrderEvent webHookOrderEvent, Object... args) {
        //只推送approval_on_process_completed审批流程结束的单据
        boolean isProcessCompleted = webHookOrderEvent.getType().equals(KafkaTopicEnum.TOPIC_APPROVAL_ON_PROCESS_COMPLETED.getTopic());
        if (!isProcessCompleted) {
            return true;
        }
        //只推送审批通过的单据
        boolean isApproved = webHookOrderEvent.getProcessInstanceStatus().equals(StringUtils.obj2str(ApplyStatus.Approved.getValue()));
        if (!isApproved) {
            return true;
        }
        String companyId = webHookOrderEvent.getCompanyId();
        //企业是否配置回调
        ThirdCallbackConf thirdCallbackConf = companyId == null ? null : callbackConfDao.queryByCompanyIdAndCallBackType(companyId, CallbackType.SAAS_APPROVAL_FORM.getType());
        if (thirdCallbackConf == null) {
            return true;
        }
        //查询申请单类型id，该企业有配置则继续
        List<OpenMsgSetup> msgSetupList = openMsgSetupDao.listByCompanyIdAndItemCodeList(webHookOrderEvent.getCompanyId(), Lists.newArrayList("open_company_approval_form"));
        if (msgSetupList == null || msgSetupList.size() == 0) {
            log.info("该企业未配置消息回传，企业id:{},消息体：{}", webHookOrderEvent.getCompanyId(), JsonUtils.toJson(webHookOrderEvent));
            return true;
        }
        Map applyTypeMap = JsonUtils.toObj(msgSetupList.get(0).getStrVal1(), Map.class);
        String[] applyTypes = StringUtils.obj2str(MapUtils.getValueByExpress(applyTypeMap, "applyType")).split(",");//配置好的申请单类型（一级分类）
        //判断哪种类型的申请单数据推送
        if (!Arrays.asList(applyTypes).contains(StringUtils.obj2str(webHookOrderEvent.getApplyType()))) {
            log.info("企业配置的回传类型不符，企业id:{},事件类型:{}", companyId, JsonUtils.toJson(webHookOrderEvent.getApplyOrderType()));
            return true;
        }
        Integer applyType = webHookOrderEvent.getApplyType();
        ThirdCallbackRecord record = new ThirdCallbackRecord();
        AuthDefinition authInfo = authDefinitionDao.getAuthInfoByAppId(webHookOrderEvent.getCompanyId());
        record.setOrderId(webHookOrderEvent.getApplyOrderId());
        record.setCompanyId(webHookOrderEvent.getCompanyId());
        record.setCompanyName(authInfo.getAppName());
        record.setContactName(webHookOrderEvent.getStarterName());
        record.setUserName(webHookOrderEvent.getStarterName());
        record.setCallbackType(CallbackType.SAAS_APPROVAL_FORM.getType());
        if (applyType.equals(ApplyType.CustomReimburse.getValue())) {
            List<RemiDetailResDTO> remiDetailResDTOS = funcRemiService.queryReimburseBillListByIdList(Lists.newArrayList(webHookOrderEvent.getApplyOrderId()), webHookOrderEvent.getCompanyId());
            if (ObjectUtils.isEmpty(remiDetailResDTOS)) {
                log.info("未查询到报销单,applyOrderId:{0},companyId:{1}", webHookOrderEvent.getApplyOrderId(), webHookOrderEvent.getCompanyId());
                return true;
            }
            record.setApplyType(webHookOrderEvent.getApplyType());
            record.setApplyTypeName(ApplyType.valueOf(webHookOrderEvent.getApplyType()).getDesc());
            record.setCallbackData(JsonUtils.toJson(remiDetailResDTOS.get(0)));
        }else if (applyType.equals(ApplyType.CustomBeforehand.getValue())){
            CustformApplyDetailDTO custformApplyDetail = openCustformApplyService.getCustformApplyDetail(webHookOrderEvent.getApplyOrderId(), webHookOrderEvent.getCompanyId());
            if (ObjectUtils.isEmpty(custformApplyDetail)){
                log.info("未查询到自定义审批单,applyOrderId:{0},companyId:{1}", webHookOrderEvent.getApplyOrderId(), webHookOrderEvent.getCompanyId());
                return true;
            }
            record.setApplyType(webHookOrderEvent.getApplyType());
            record.setApplyTypeName(ApplyType.valueOf(webHookOrderEvent.getApplyType()).getDesc());
            record.setCallbackData(JsonUtils.toJson(custformApplyDetail));

        }
        if (!StringUtils.isBlank(record.getCallbackData())) {
            callbackRecordDao.saveSelective(record);
            businessDataPushService.pushData(record.getCompanyId(), record, 0, 4);
        }
        return true;
    }


}
