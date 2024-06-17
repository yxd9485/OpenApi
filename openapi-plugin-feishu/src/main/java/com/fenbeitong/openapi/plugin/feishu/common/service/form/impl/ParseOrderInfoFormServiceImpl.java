package com.fenbeitong.openapi.plugin.feishu.common.service.form.impl;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyFormFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.ParseApplyFormService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenOrderApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Maps;
import com.luastar.swift.base.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 订单信息表单解析
 * @author xiaohai
 * @Date 2022/06/30
 */
@ServiceAspect
@Service
@Slf4j
public class ParseOrderInfoFormServiceImpl implements ParseApplyFormService<String> {

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Override
    public void parseFormInfo(List<FeiShuApprovalSimpleFormDTO> approvalDefines , String reqData) {
        //接收分贝通订单审批数据
        if (StringUtils.isBlank(reqData)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        Map reqMap = com.fenbeitong.openapi.plugin.util.JsonUtils.toObj(reqData, Map.class);
        if (ObjectUtils.isEmpty(reqMap)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        //解析分贝通审批数据
        Map<String, String> apply = commonApplyService.parseFbtOrderApplyNotice(reqMap);
        String companyId = apply.get("companyId");
        Map applyDetail = MapUtils.newHashMap();
        applyDetail.put("companyId", companyId);
        try{
            applyDetail = commonApplyService.parseFbtOrderApplyDetail(reqMap, applyDetail);
        }catch (ParseException e){
            log.warn("解析数据出错 map: {} " , JsonUtils.toJson( reqMap ));
        }
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {
            //具体字段名称 例：出发地
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenOrderApplyConstant.ORDER_PERSON)) {
                //下单人
                value = StringUtils.obj2str(applyDetail.get("orderPerson"));
            } else if (name.equals(OpenOrderApplyConstant.ORDER_TYPE)) {
                //场景类型
                value = StringUtils.obj2str(applyDetail.get("orderType"));
            } else if (name.equals(OpenOrderApplyConstant.BEGIN_DATE)) {
                //开始日期
                Object beginDate = applyDetail.get("beginDate");
                value = DateUtils.toStr(DateUtils.toDate(NumericUtils.obj2long(StringUtils.obj2str(beginDate))), DateUtils.FORMAT_DATE_PATTERN_T_1) + "+08:00";
            } else if (name.equals(OpenOrderApplyConstant.END_DATE)) {
                //结束日期
                Object endDate = applyDetail.get("endDate");
                value = DateUtils.toStr(DateUtils.toDate(NumericUtils.obj2long(StringUtils.obj2str(endDate))), DateUtils.FORMAT_DATE_PATTERN_T_1) + "+08:00";
            } else if (name.equals(OpenOrderApplyConstant.DEPARTURE_NAME)) {
                //出发地
                value = StringUtils.obj2str(applyDetail.get("departureName"));
            } else if (name.equals(OpenOrderApplyConstant.DESTINATION_NAME)) {
                //目的地
                value = StringUtils.obj2str(applyDetail.get("destinationName"));
            } else if (name.equals(OpenOrderApplyConstant.ORDER_PRICE)) {
                //订单金额
                value = StringUtils.obj2str(applyDetail.get("orderPrice"));
            } else if (name.equals(OpenOrderApplyConstant.GUEST_NAME)) {
                //使用人
                value = StringUtils.obj2str(applyDetail.get("guestName"));
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
    }


    @Override
    public void afterPropertiesSet() {
        ApplyFormFactory.registerHandler(FeiShuServiceTypeConstant.ORDER , this);
    }


}
