package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.impl;

import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto.OrderApplyAgreeReqDTO;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto.OrderApplyRepluseReqDTO;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.ApplyNotifyService;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.support.apply.dto.AirPresentPriceDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.BaseDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.SceneServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName ApplyNotifyServiceImpl
 * @Description 申请单审批通过或拒绝
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/8/13 下午12:00
 **/
@Service
@Slf4j
public class ApplyNotifyServiceImpl implements ApplyNotifyService {

    @Autowired
    private FuncEmployeeService employeeService;

    @Autowired
    private CommonAuthService commonAuthService;

    @Autowired
    private ThirdCallbackRecordDao recordDao;

    @Autowired
    private SceneServiceImpl sceneService;

    @Autowired
    private RestHttpUtils restHttpUtils;

    @Value("${host.appgate}")
    private String appgateHost;

    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Override
    public void applyNotifyAgree(ApiRequestBase request) throws IOException {
        String companyId =commonAuthService.checkSign(request);
        OrderApplyAgreeReqDTO notifyReq = JsonUtils.toObj(request.getData(), OrderApplyAgreeReqDTO.class);
        @NotBlank(message = "审批人id[approver_id]不可为空") String approverId = notifyReq.getApproverId();//三方审批人id
        String token = employeeService.getEmployeeFbToken(companyId, approverId, "1");

        ThirdCallbackRecord record = recordDao.getApplyByApplyId(notifyReq.getApplyId(), CallbackType.APPLY_ORDER_REVERSE_PUSH.getType());
        if (record != null) {
            int applyType =NumericUtils.obj2int(MapUtils.getValueByExpress(JsonUtils.toObj(StringUtils.obj2str(record.getCallbackData()),Map.class),"apply_type"));
            int type = NumericUtils.obj2int(MapUtils.getValueByExpress(JsonUtils.toObj(StringUtils.obj2str(record.getCallbackData()),Map.class),"apply_order_detail:type"));
            BigDecimal applyAmount =  NumberUtils.createBigDecimal(StringUtils.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(StringUtils.obj2str(record.getCallbackData()),Map.class),"apply_order_detail:apply_amount")));
            //申请单类型  6.国内机票  7.国际机票  8.酒店  9.火车
            if (applyType==2 && type == 6) {
                AirPresentPriceDTO airPresentPrice = sceneService.getAirPresentPrice(token, record.getOrderId());
                notifyReq.setSeatItem(airPresentPrice == null ? null : airPresentPrice.getSeatItem());
            }
            if(ObjectUtils.isEmpty(notifyReq.getPrice())){
                notifyReq.setPrice(applyAmount);
            }
            agreeOrderApply(token, notifyReq);
        }
    }

    @Override
    public void applyNotifyRepulse(ApiRequestBase request) throws IOException {
        String companyId =commonAuthService.checkSign(request);
        OrderApplyRepluseReqDTO notifyReq = JsonUtils.toObj(request.getData(), OrderApplyRepluseReqDTO.class);
        @NotBlank(message = "审批人id[approver_id]不可为空") String approverId = notifyReq.getApproverId();//三方审批人id
        String token = employeeService.getEmployeeFbToken(companyId, approverId, "1");

        ThirdCallbackRecord record = recordDao.getApplyByApplyId(notifyReq.getApplyId(), CallbackType.APPLY_ORDER_REVERSE_PUSH.getType());
        if (record != null) {
            BigDecimal applyAmount =  NumberUtils.createBigDecimal(StringUtils.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(StringUtils.obj2str(record.getCallbackData()),Map.class),"apply_order_detail:apply_amount")));
            notifyReq.setPrice(applyAmount);
            repluseOrderApply(token,notifyReq);
        }
    }

    /**
     * 同意订单审批
     *
     * @param token    用户token
     * @param agreeReq 同意订单审批请求
     * @return 同意结果
     */
    private void agreeOrderApply(String token, OrderApplyAgreeReqDTO agreeReq) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        httpHeaders.add("client_version","4.7.0");
        String result = restHttpUtils.postJson(appgateHost + "/saas_plus/apply/approve", httpHeaders, JsonUtils.toJson(agreeReq));
        log.info("审批同意返回："+result);
        BaseDTO agreeResult = JsonUtils.toObj(result, BaseDTO.class);
        if (agreeResult == null || !agreeResult.success()) {
            String msg = agreeResult == null ? "" : Optional.ofNullable(agreeResult.getMsg()).orElse("");
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.THIRD_ORDER_APPLY_AGREE_FAILED), ":" + msg);
        }
    }

    /**
     * 拒绝订单审批
     *
     * @param token    用户token
     * @param agreeReq 拒绝订单审批请求
     * @return 拒绝结果
     */
    private void repluseOrderApply(String token, OrderApplyRepluseReqDTO agreeReq) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        httpHeaders.add("client_version","4.7.0");
        String result = restHttpUtils.postJson(appgateHost + "/saas_plus/apply/repulse", httpHeaders, JsonUtils.toJson(agreeReq));
        log.info("审批拒绝返回："+result);
        BaseDTO agreeResult = JsonUtils.toObj(result, BaseDTO.class);
        if (agreeResult == null || !agreeResult.success()) {
            String msg = agreeResult == null ? "" : Optional.ofNullable(agreeResult.getMsg()).orElse("");
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.THIRD_ORDER_APPLY_REPULSE_FAILED), ":" + msg);
        }
    }
}
