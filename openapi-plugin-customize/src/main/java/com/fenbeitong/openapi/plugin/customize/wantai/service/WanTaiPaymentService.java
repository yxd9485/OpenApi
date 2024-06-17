package com.fenbeitong.openapi.plugin.customize.wantai.service;

import com.alibaba.fastjson.JSONObject;
import com.fenbei.billpay.api.payment.ThirdCustomPublicPaymentFacade;
import com.fenbei.billpay.api.payment.dto.custom.CustomPublicPaymentDetailResDTO;
import com.fenbei.billpay.api.payment.dto.custom.PaymentDetailDTO;
import com.fenbei.material.api.supplier.ThirdCustomSupplierFacade;
import com.fenbei.material.api.supplier.dto.custom.CustomTransformIdResDTO;
import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.customize.wantai.constant.WanTaiArchiveConstant;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.ERPTokenRespDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.PaymentDataReqDTO;
import com.fenbeitong.openapi.plugin.func.callback.dto.PaymentRecordDTO;
import com.fenbeitong.openapi.plugin.support.common.service.CommonService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.payment.dto.PaymentCustomReqDTO;
import com.fenbeitong.openapi.plugin.support.payment.dto.PaymentCustomRespDTO;
import com.fenbeitong.openapi.plugin.support.payment.service.IPaymentCustomService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author machao
 * @date 2022/9/27
 */
@ServiceAspect
@Service
@Slf4j
public class WanTaiPaymentService {
    private static final Integer SUCCESS = 80;

    @Value("${wantai.erp.host}")
    private String host;

    @Value("${wantai.erp.accessKey}")
    private String accessKey;

    @Value("${wantai.erp.secretKey}")
    private String secretKey;

    @DubboReference(check = false)
    private ThirdCustomSupplierFacade thirdCustomSupplierFacade;

    @DubboReference(check = false)
    private ThirdCustomPublicPaymentFacade thirdCustomPublicPaymentFacade;

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private IPaymentCustomService paymentCustomService;

    public void successNotice(PaymentRecordDTO req) {
        PaymentDataReqDTO data = new PaymentDataReqDTO();
        String paymentId = req.getPaymentId();
        String companyId = req.getCompanyId();
        Integer paymentState = req.getPaymentState();
        // 企业id和付款单id不为空 并且付款单状态为80成功
        if (StringUtils.isNotBlank(paymentId) && StringUtils.isNotBlank(companyId) && SUCCESS.equals(paymentState)) {
            // 查询自定义付款单详情
            CustomPublicPaymentDetailResDTO paymentDetailDTO = thirdCustomPublicPaymentFacade.queryPaymentByPaymentId(companyId, getToken(companyId), paymentId);
            if (paymentDetailDTO != null) {
                PaymentDetailDTO payment = paymentDetailDTO.getPayment();
                data.setVarFbtno(payment.getPaymentId());
                data.setFiscalYear(getFiscalYear(payment.getCompleteTime()));
                data.setFiscalPeriod(getFiscalPeriod(payment.getCompleteTime()));
                data.setIdRecorder(getIdRecorder(companyId, payment.getUserId()));
                data.setIdCorr(getIdCorr(companyId, payment.getSupplierBizId()));
                data.setDateOpr(DateUtils.toSimpleStr(payment.getCreateTime()));
                data.setDatePay(DateUtils.toSimpleStr(payment.getCompleteTime()));
                data.setIdSetmth(getCustomizeField(paymentDetailDTO.getFields(), paymentDetailDTO.getObjectData(), "付款类别"));
                data.setIdTrsactype(getCustomizeField(paymentDetailDTO.getFields(), paymentDetailDTO.getObjectData(), "结算方式"));
                data.setDecSamt(payment.getTotalPrice());
                data.setIdCurr(getCustomizeField(paymentDetailDTO.getFields(), paymentDetailDTO.getObjectData(), "币种代码"));
                // 万泰申请单id 对应 付款单三方id
                data.setOriNo(payment.getThirdPaymentId());
                data.setVarBank(payment.getReceiverBank());
                data.setVarBankacct(payment.getReceiverAccount());
                String erpToken = getERPToken();
                String url = host + WanTaiArchiveConstant.URL_ERP_GET_SYNC;
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("token", erpToken);
                String result = RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(data));
                ERPTokenRespDTO erpRespDTO = JsonUtils.toObj(result, ERPTokenRespDTO.class);
                if (erpRespDTO == null || 0 != erpRespDTO.getCode()) {
                    throw new OpenApiCustomizeException(500, "[万泰]调用付款单接收失败：" + result);
                }
            } else {
                log.error("[万泰]付款单详情返回空 req = {}", JSONObject.toJSONString(req));
            }
        } else {
            log.error("[万泰]付款单详推送信息为空 req = {}", JSONObject.toJSONString(req));
        }
    }

    private String getCustomizeField(Object fields, Object objectData, String fieldName) {
        if (fields != null) {
            String jsonString = JSONObject.toJSONString(fields);
            if (StringUtils.isNotBlank(jsonString)) {
                List<Field> fieldList = JSONObject.parseArray(jsonString, Field.class);
                if (!CollectionUtils.isEmpty(fieldList)) {
                    for (Field field : fieldList) {
                        Object mtField = field.getMtField();
                        String mtFieldStr = JSONObject.toJSONString(mtField);
                        JSONObject mtFieldJson = JSONObject.parseObject(mtFieldStr);
                        String fieldDisplayName = mtFieldJson.getString("fieldDisplayName");
                        if (fieldName.equals(fieldDisplayName)) {
                            String fieldCode = mtFieldJson.getString("fieldCode");
                            if (StringUtils.isNotBlank(fieldCode)) {
                                String objectDataStr = JSONObject.toJSONString(objectData);
                                JSONObject objectDataJson = JSONObject.parseObject(objectDataStr);
                                return objectDataJson.getString(fieldCode);
                            }
                        }
                    }
                }

            }
        }
        return null;
    }

    @Data
    public static class Field {
        private Object mtField;
        private Object fieldsConfig;
    }

    public PaymentCustomRespDTO createPaymentCustom(PaymentCustomReqDTO req) {
        return paymentCustomService.createPaymentCustom(req, false);
    }

    private String getIdCorr(String companyId, String supplierBizId) {
        List<String> ids = new ArrayList<>();
        ids.add(supplierBizId);
        CustomTransformIdResDTO customTransformIdResDTO = thirdCustomSupplierFacade.transformIds(companyId, 2, ids);
        return customTransformIdResDTO.getTransformIds().get(0).getTargetId();
    }

    private String getIdRecorder(String companyId, String userId) {
        return commonService.getThirdEmployeeId(companyId, userId);
    }

    private Integer getFiscalPeriod(Date completeTime) {
        if (completeTime != null) {
            String m = DateUtils.toStr(completeTime, "MM");
            if (StringUtils.isNotBlank(m)) {
                return Integer.parseInt(m);
            }
        }
        return null;
    }

    private Integer getFiscalYear(Date completeTime) {
        if (completeTime != null) {
            String y = DateUtils.toStr(completeTime, "yyyy");
            if (StringUtils.isNotBlank(y)) {
                return Integer.parseInt(y);
            }
        }
        return null;
    }

    private String getToken(String companyId) {
        return userCenterService.getUcSuperAdminToken(companyId);
    }

    private String getERPToken() {
        String url = host + WanTaiArchiveConstant.URL_ERP_TOKEN;
        MultiValueMap param = new LinkedMultiValueMap();
        param.add("accessKey", accessKey);
        param.add("secretKey", secretKey);
        String result = RestHttpUtils.postForm(url, param);
        ERPTokenRespDTO erpTokenRespDTO = JsonUtils.toObj(result, ERPTokenRespDTO.class);
        if (erpTokenRespDTO == null || erpTokenRespDTO.getCode() != 0 || StringUtils.isBlank(
            erpTokenRespDTO.getData())) {
            throw new OpenApiCustomizeException(500, "[万泰]获取ERP token失败：" + result);
        }
        return erpTokenRespDTO.getData();
    }

}
