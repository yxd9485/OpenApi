package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.support.common.dao.OpenCompanyVoucherConfigDao;
import com.fenbeitong.openapi.plugin.support.common.entity.OpenCompanyVoucherConfig;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.voucher.service.IVoucherCreateService;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieApiResponseCode;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResponseUtils;
import com.fenbeitong.openapi.plugin.yiduijie.constant.BusinessType;
import com.fenbeitong.openapi.plugin.yiduijie.dao.YiDuiJieConfDao;
import com.fenbeitong.openapi.plugin.yiduijie.dao.YiduijieCreateVoucherRecordDao;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJiePreviewVoucherResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieSendMessageReq;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieSendMessageResp;
import com.fenbeitong.openapi.plugin.yiduijie.entity.YiDuiJieConf;
import com.fenbeitong.openapi.plugin.yiduijie.entity.YiduijieCreateVoucherRecord;
import com.fenbeitong.openapi.plugin.yiduijie.exception.OpenApiYiDuiJieException;
import com.fenbeitong.openapi.plugin.yiduijie.model.voucher.CreateVoucherByApplyReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.voucher.CreateVoucherByBillReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.voucher.CreateVoucherReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.sdk.YiDuiJieMessageApi;
import com.fenbeitong.openapi.plugin.yiduijie.sdk.YiDuiJieTransformApi;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieInteractService;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieTokenService;
import com.fenbeitong.openapi.plugin.yiduijie.service.voucher.IVoucherService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

/**
 * <p>Title: BaseVoucherServiceImpl</p>
 * <p>Description: 基础凭证服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 6:24 PM
 */
@Slf4j
@ServiceAspect
@Service
public class BaseVoucherServiceImpl extends BaseYiDuiJieService implements IVoucherService {

    @Autowired
    private IYiDuiJieTokenService yiDuiJieTokenService;

    @Autowired
    private IYiDuiJieInteractService yiDuiJieInteractService;

    @Autowired
    private YiDuiJieConfDao yiDuiJieConfDao;

    @Autowired
    private YiduijieCreateVoucherRecordDao yiduijieCreateVoucherRecordDao;

    @Autowired
    private YiDuiJieMessageApi yiDuiJieMessageApi;

    @Autowired
    private YiDuiJieTransformApi yiDuiJieTransformApi;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OpenCompanyVoucherConfigDao companyVoucherConfigDao;

    @Autowired
    private IVoucherCreateService voucherCreateService;

    @Value("${host.openplus}")
    private String openPlusHost;

    @Async
    @Override
    public void createVoucher(CreateVoucherReqDTO req) {
        //检查参数
        checkReqParam(Lists.newArrayList(req), CreateVoucherReqDTO.CreateVoucherGroup.class);
        //加锁防止重复提交
        String lockKey = "createVoucher_" + req.getBusinessType() + "_" + req.getBatchId();
        Long lockTime = RedisDistributionLock.lock(lockKey, redisTemplate);
        try {
            createVoucher(BusinessType.getBusinessType(req.getBusinessType()), req.getCompanyId(), req.getBatchId(), req.getCallBackUrl(), req.getOperator(), req.getOperatorId(), lockTime);
        } finally {
            RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
        }
    }

    @Override
    public void createVoucherByApply(CreateVoucherByApplyReqDTO req) {
        //检查参数
        checkReqParam(Lists.newArrayList(req), CreateVoucherByApplyReqDTO.CreateVoucherGroup.class);
        //加锁防止重复提交
        String lockKey = "createVoucher_apply_" + req.getBatchId();
        Long lockTime = RedisDistributionLock.lock(lockKey, redisTemplate);
        try {
            createVoucher(BusinessType.APPLY, req.getCompanyId(), req.getBatchId(), req.getCallBackUrl(), req.getOperator(), null, lockTime);
        } finally {
            RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
        }
    }

    @Override
    public void createVoucherByBill(CreateVoucherByBillReqDTO req) {
        //检查参数
        checkReqParam(Lists.newArrayList(req));
        //加锁防止重复提交
        String lockKey = "createVoucher_bill_" + req.getBatchId();
        Long lockTime = RedisDistributionLock.lock(lockKey, redisTemplate);
        try {
            createVoucher(BusinessType.BILL, req.getCompanyId(), req.getBatchId(), req.getCallBackUrl(), req.getOperator(), null, lockTime);
        } finally {
            RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
        }
    }

    private void createVoucher(BusinessType businessType, String companyId, String batchId, String callBackUrl, String operator, String operatorId, Long lockTime) {
        if (lockTime > 0) {
            YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
            if (yiDuiJieConf == null) {
                handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.SEND_CREATE_VOUCHER_MSG_ERROR)));
            }
            if (businessType.getType() != BusinessType.VOUCHER.getType()) {
                YiduijieCreateVoucherRecord record = yiduijieCreateVoucherRecordDao.getByBusinessTypeAndBatchId(businessType.getValue(), batchId);
                if (record != null) {
                    return;
                }
                String[] operatorIdName = operator.split(":");
                if (operatorId == null) {
                    operatorId = operatorIdName.length == 2 ? operatorIdName[0] : null;
                    operator = operatorIdName.length == 2 ? operatorIdName[1] : operator;
                }
                //先保存生凭证的数据
                record = new YiduijieCreateVoucherRecord();
                record.setBusinessType(businessType.getValue());
                record.setCompanyId(companyId);
                record.setBatchId(batchId);
                record.setCallbackUrl(callBackUrl);
                record.setOperator(operator);
                record.setOperatorId(operatorId);
                yiduijieCreateVoucherRecordDao.saveSelective(record);
            }
            OpenCompanyVoucherConfig voucherConfig = companyVoucherConfigDao.getByCompanyIdAndType(companyId, businessType.getType());
            if (voucherConfig == null || businessType.getType() == BusinessType.VOUCHER.getType()) {
                String token = yiDuiJieTokenService.getYiDuiJieToken();
                YiDuiJieSendMessageReq sendMessageReq = YiDuiJieSendMessageReq.builder()
                        .operator(operator)
                        .appId(yiDuiJieConf.getAppId())
                        .data(Lists.newArrayList(businessType + ":" + batchId))
                        .build();
                YiDuiJieSendMessageResp sendMessageResp = yiDuiJieMessageApi.sendMessage(token, sendMessageReq);
                if (sendMessageResp == null || !sendMessageResp.success()) {
                    handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.SEND_CREATE_VOUCHER_MSG_ERROR)));
                }
            } else {
                String callbackUrl = "";
                if (voucherConfig.getDockingType() == 1) {
                    callbackUrl = openPlusHost + "/openapi/yiduijie/interact/notifyMsgResult";
                } else if (voucherConfig.getDockingType() == 2) {
                    callbackUrl = openPlusHost + "/openapi/yiduijie/voucher/createVoucher";
                }
                voucherCreateService.createVoucher(companyId, operatorId, operator, businessType.getType(), batchId, callbackUrl);
            }
        } else {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.SEND_CREATE_VOUCHER_MSG_ERROR), "操作中，请稍后重试"));
        }
    }

    @Override
    public String previewCreateVoucherByApply(CreateVoucherByApplyReqDTO req) {
        //检查参数
        checkReqParam(Lists.newArrayList(req));
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(req.getCompanyId());
        if (yiDuiJieConf == null) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.APPLY_PREVIEW_CREATE_VOUCHER_MSG_ERROR)));
        }
        Object body = YiDuiJieResponseUtils.success(yiDuiJieInteractService.queryBusinessData(BusinessType.APPLY.getValue() + ":" + req.getBatchId()));
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJiePreviewVoucherResp transformResp = yiDuiJieTransformApi.transform(token, yiDuiJieConf.getAppId(), JsonUtils.toJson(body));
        if (transformResp == null || !transformResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.APPLY_PREVIEW_CREATE_VOUCHER_MSG_ERROR)));
        }
        String error = transformResp.getError();
        if (ObjectUtils.isEmpty(error)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.APPLY_PREVIEW_CREATE_VOUCHER_MSG_ERROR), error));
        }
        return JsonUtils.toJson(transformResp.getPreviewResult());
    }
}
