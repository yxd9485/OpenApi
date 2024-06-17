package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCreateApprovalInstanceReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuNoticeResultDto;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.FeiShuCommonApplyService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenOrderApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTaxiApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTripApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenOrderApplyDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.ApplyDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author lizhen
 * @date 2020/12/2
 */
public abstract class AbstractFeiShuPushApplyService {

    @Autowired
    private OpenOrderApplyDao openOrderApplyDao;
    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private FeiShuFormDataServiceFactory feiShuFormDataServiceFactory;

    @Autowired
    private FeiShuCommonApplyService feiShuCommonApplyService;

    public boolean pushApply(String object) throws ParseException {
        //接收分贝通订单审批数据
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        Map<String, String> apply = commonApplyService.parseFbtOrderApplyNotice(map);
        String companyId = apply.get("companyId");
        String thirdEmployeeId = apply.get("thirdEmployeeId");
        String thirdProcessCode = apply.get("thirdProcessCode");
        String thirdCorpId = apply.get("thirdCorpId");
        String applyId = apply.get("applyId");
        //解析分贝通审批数据
        Map applyDetail = Maps.newHashMap();
        applyDetail.put("companyId", companyId);
        applyDetail = commonApplyService.parseFbtOrderApplyDetail(map, applyDetail);
        //查询模板详情
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(thirdProcessCode, thirdCorpId);
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {//解析组件详情
            //具体字段名称 例：出发地
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenOrderApplyConstant.ORDER_PERSON)) {//下单人
                value = StringUtils.obj2str(applyDetail.get("orderPerson"));
            } else if (name.equals(OpenOrderApplyConstant.ORDER_TYPE)) {//场景类型
                value = StringUtils.obj2str(applyDetail.get("orderType"));
            } else if (name.equals(OpenOrderApplyConstant.BEGIN_DATE)) {//开始日期
                Object beginDate = applyDetail.get("beginDate");
                value = DateUtils.toStr(DateUtils.toDate(NumericUtils.obj2long(StringUtils.obj2str(beginDate))), DateUtils.FORMAT_DATE_PATTERN_T_1) + "+08:00";
            } else if (name.equals(OpenOrderApplyConstant.END_DATE)) {//结束日期
                Object endDate = applyDetail.get("endDate");
                value = DateUtils.toStr(DateUtils.toDate(NumericUtils.obj2long(StringUtils.obj2str(endDate))), DateUtils.FORMAT_DATE_PATTERN_T_1) + "+08:00";
            } else if (name.equals(OpenOrderApplyConstant.DEPARTURE_NAME)) {//出发地
                value = StringUtils.obj2str(applyDetail.get("departureName"));
            } else if (name.equals(OpenOrderApplyConstant.DESTINATION_NAME)) {//目的地
                value = StringUtils.obj2str(applyDetail.get("destinationName"));
            } else if (name.equals(OpenOrderApplyConstant.ORDER_PRICE)) {//订单金额
                value = StringUtils.obj2str(applyDetail.get("orderPrice"));
            } else if (name.equals(OpenOrderApplyConstant.GUEST_NAME)) {//使用人
                value = StringUtils.obj2str(applyDetail.get("guestName"));
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
        FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO = new FeiShuCreateApprovalInstanceReqDTO();
        feiShuCreateInstanceReqDTO.setApprovalCode(thirdProcessCode);
        if (OpenType.FEISHU_EIA.getType() == getOpenType()) {
            feiShuCreateInstanceReqDTO.setUserId(thirdEmployeeId);
        } else if (OpenType.FEISHU_ISV.getType() == getOpenType()) {
            feiShuCreateInstanceReqDTO.setOpenId(thirdEmployeeId);
        } else {
            throw new OpenApiPluginException(SupportRespCode.OPEN_TYPE_ERROR);
        }
        feiShuCreateInstanceReqDTO.setForm(JsonUtils.toJson(approvalDefines));
        String approvalInstance = getFeiShuApprovalService().createApprovalInstance(feiShuCreateInstanceReqDTO, thirdCorpId);
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, approvalInstance, getOpenType());
    }

    public boolean pushTripApply(String object) {
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map<String, String> map1 = commonApplyService.parseFbtTripApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        String thirdCorpId = map1.get("thirdCorpId");
        String applyId = map1.get("applyId");
        ApplyDTO applyDTO = JsonUtils.toObj(object, ApplyDTO.class);
        if (ObjectUtils.isEmpty(applyDTO)
                || ObjectUtils.isEmpty(applyDTO.getTrip_list())) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        commonApplyService.parseFbtTripApplyDetail(applyDTO);
        // 查询模板详情
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(thirdProcessCode, thirdCorpId);
        // 解析组件详情
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenTripApplyConstant.APPLY_PERSON)) {
                value = applyDTO.getApplyReasonStr();
            }
            if (name.equals(OpenTripApplyConstant.TRIP_LIST)) {
                value = applyDTO.getTripListStr();
            }
            if (name.equals(OpenTripApplyConstant.GUEST_LIST)) {
                value = applyDTO.getGuestListStr();
            }
            if (name.equals(OpenTripApplyConstant.TRAVEL_TIME)) {
                value = applyDTO.getTravelTimeStr();
            }
            if (name.equals(OpenTripApplyConstant.WHERE_IS)) {
                value = applyDTO.getCost_attribution_name();
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
        FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO = new FeiShuCreateApprovalInstanceReqDTO();
        feiShuCreateInstanceReqDTO.setApprovalCode(thirdProcessCode);
        feiShuCreateInstanceReqDTO.setUserId(thirdEmployeeId);
        feiShuCreateInstanceReqDTO.setForm(JsonUtils.toJson(approvalDefines));
        String approvalInstance = getFeiShuApprovalService().createApprovalInstance(feiShuCreateInstanceReqDTO, thirdCorpId);
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, approvalInstance, getOpenType());
    }

    public boolean pushTaxiApply(String object) {
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map<String, String> map1 = commonApplyService.parseFbtTaxiApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        String thirdCorpId = map1.get("thirdCorpId");
        String applyId = map1.get("applyId");
        ApplyDTO applyDTO = JsonUtils.toObj(object, ApplyDTO.class);
        if (ObjectUtils.isEmpty(applyDTO)
                || ObjectUtils.isEmpty(applyDTO.getTrip_list())) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        commonApplyService.parseFbtTaxiApplyDetail(applyDTO);
        //查询模板详情
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(thirdProcessCode, thirdCorpId);
        // 解析组件详情
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenTaxiApplyConstant.APPLY_PERSON)) {
                value = applyDTO.getApplyReasonStr();
            }
            if (name.equals(OpenTaxiApplyConstant.APPLY_TAXI_INFO)) {
                value = applyDTO.getTaxiInfoStr();
            }
            if (name.equals(OpenTaxiApplyConstant.RULE_INFO)) {
                value = applyDTO.getRuleInfoStr();
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
        FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO = new FeiShuCreateApprovalInstanceReqDTO();
        feiShuCreateInstanceReqDTO.setApprovalCode(thirdProcessCode);
        feiShuCreateInstanceReqDTO.setUserId(thirdEmployeeId);
        feiShuCreateInstanceReqDTO.setForm(JsonUtils.toJson(approvalDefines));
        String approvalInstance = getFeiShuApprovalService().createApprovalInstance(feiShuCreateInstanceReqDTO, thirdCorpId);
        // 存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, approvalInstance, getOpenType());
    }

    public boolean pushOrderChangeApply(String object) {
        //接收分贝通订单审批数据
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        Map<String, String> apply = commonApplyService.parseFbtOrderChangeApplyNotice(map);
        String companyId = apply.get("companyId");
        String thirdEmployeeId = apply.get("thirdEmployeeId");
        String thirdProcessCode = apply.get("thirdProcessCode");
        String thirdCorpId = apply.get("thirdCorpId");
        String applyId = apply.get("applyId");
        //解析分贝通审批数据
        commonApplyService.parseChangeOrderInfo(map);
        //查询模板详情
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(thirdProcessCode, thirdCorpId);
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {//解析组件详情
            //具体字段名称 例：出发地
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenOrderApplyConstant.CHANGE_TYPE)) {
                value = StringUtils.obj2str(map.get("changeType"));
            }
            if (name.equals(OpenOrderApplyConstant.CHANGE_REASON)) {
                value = StringUtils.obj2str(map.get("changeReason"));
                // 申请详情
                if (map.get("changeDetail") != null) {
                    value = value + map.get("changeDetail").toString();
                }
            }
            if (name.equals(OpenOrderApplyConstant.OLD_ORDER_INFO)) {
                value = StringUtils.obj2str(map.get("oldOrderInfo"));
            }
            if (name.equals(OpenOrderApplyConstant.ORDER_INFO)) {
                value = StringUtils.obj2str(map.get("orderInfo"));
            }
            if (name.equals(OpenOrderApplyConstant.ORDER_CHANGE_FEE)) {
                value = StringUtils.obj2str(map.get("orderChangeFee"));
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
        FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO = new FeiShuCreateApprovalInstanceReqDTO();
        feiShuCreateInstanceReqDTO.setApprovalCode(thirdProcessCode);
        feiShuCreateInstanceReqDTO.setUserId(thirdEmployeeId);
        feiShuCreateInstanceReqDTO.setForm(JsonUtils.toJson(approvalDefines));
        String approvalInstance = getFeiShuApprovalService().createApprovalInstance(feiShuCreateInstanceReqDTO, thirdCorpId);
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, approvalInstance, getOpenType());
    }

    public boolean pushOrderRefundApply(String object) {
        //接收分贝通订单审批数据
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        Map<String, String> apply = commonApplyService.parseFbtOrderRefundApplyNotice(map);
        String companyId = apply.get("companyId");
        String thirdEmployeeId = apply.get("thirdEmployeeId");
        String thirdProcessCode = apply.get("thirdProcessCode");
        String thirdCorpId = apply.get("thirdCorpId");
        String applyId = apply.get("applyId");
        //解析分贝通审批数据
        commonApplyService.parseRefundOrderInfo(map);
        //查询模板详情
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(thirdProcessCode, thirdCorpId);
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {//解析组件详情
            // 具体字段名称 例：出发地
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenOrderApplyConstant.CHANGE_TYPE)) {
                value = map.get("changeType").toString();
            }
            if (name.equals(OpenOrderApplyConstant.REFUND_REASON)) {
                value = map.get("refundReason").toString();
                // 申请详情
                if (map.get("refundDetail") != null) {
                    value = value + map.get("refundDetail").toString();
                }
            }
            if (name.equals(OpenOrderApplyConstant.OLD_ORDER_INFO)) {
                value = map.get("oldOrderInfo").toString();
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
        FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO = new FeiShuCreateApprovalInstanceReqDTO();
        feiShuCreateInstanceReqDTO.setApprovalCode(thirdProcessCode);
        feiShuCreateInstanceReqDTO.setUserId(thirdEmployeeId);
        feiShuCreateInstanceReqDTO.setForm(JsonUtils.toJson(approvalDefines));
        String approvalInstance = getFeiShuApprovalService().createApprovalInstance(feiShuCreateInstanceReqDTO, thirdCorpId);
        // 存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, approvalInstance, getOpenType());
    }

    // 通用反向审批
    public boolean pushCommonReverseApply(String object , String serviceType) {
        // 转换参数
        Map map = feiShuCommonApplyService.checkParam(object);
        // 获取消息通知结果
        FeiShuNoticeResultDto noticeResultDto = feiShuCommonApplyService.buildNoticeDto(serviceType,map);
        // 转换Dto
        FenbeitongApproveDto fenbeitongApproveDto = feiShuCommonApplyService.buildApproveDto(object);
        // 查询模板详情
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getApprovalDefines(noticeResultDto);
        // 填充模板
        feiShuCommonApplyService.fillFormData(approvalDefines,fenbeitongApproveDto,serviceType);
        // 存储分贝通审批单ID和第三方审批单ID关系
        return saveFbtOrderApplyInfo(approvalDefines,noticeResultDto);
    }

    public List<FeiShuApprovalSimpleFormDTO> getApprovalDefines(FeiShuNoticeResultDto noticeResultDto){
        //查询模板详情
        return getFeiShuApprovalService().getApprovalDefine(noticeResultDto.getThirdProcessCode(), noticeResultDto.getThirdCorpId());
    }

    public boolean saveFbtOrderApplyInfo(List<FeiShuApprovalSimpleFormDTO> approvalDefines,FeiShuNoticeResultDto noticeResultDto){
        FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO = new FeiShuCreateApprovalInstanceReqDTO();
        feiShuCreateInstanceReqDTO.setApprovalCode(noticeResultDto.getThirdProcessCode());
        feiShuCreateInstanceReqDTO.setUserId(noticeResultDto.getThirdEmployeeId());
        feiShuCreateInstanceReqDTO.setForm(JsonUtils.toJson(approvalDefines));
        String approvalInstance = getFeiShuApprovalService().createApprovalInstance(feiShuCreateInstanceReqDTO, noticeResultDto.getThirdCorpId());
        // 存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(noticeResultDto.getCompanyId(),noticeResultDto.getThirdEmployeeId(), noticeResultDto.getApplyId(), approvalInstance, getOpenType());
    }

    protected abstract AbstractFeiShuApprovalService getFeiShuApprovalService();


    protected abstract int getOpenType();
}
