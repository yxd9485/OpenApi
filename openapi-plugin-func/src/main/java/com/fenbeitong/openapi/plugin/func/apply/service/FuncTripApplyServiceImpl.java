package com.fenbeitong.openapi.plugin.func.apply.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyCostAttributionDto;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyDetailThirdInfoDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyOrderDetailDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyTripDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.common.FuncIdTypeEnums;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncValidService;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.order.dto.FuncThirdInfoExpressDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncThirdInfoServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenApplyRecordDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractTripApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.saasplus.api.service.apply.IApplyOrderService;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncTripApplyServiceImpl</p>
 * <p>Description: 行程审批</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/11 2:33 PM
 */
@ServiceAspect
@Service
@Slf4j
public class FuncTripApplyServiceImpl extends AbstractTripApplyService {

    @Autowired
    private CommonAuthService signService;

    @Autowired
    CommonApplyServiceImpl commonApplyService;

    @Value("${host.saas_plus}")
    private String saasplusUrl;

    @Autowired
    private RestHttpUtils restHttpUtils;

    @DubboReference(check = false)
    private IApplyOrderService applyOrderService;

    @Autowired
    private FuncThirdInfoServiceImpl thirdInfoService;

    @Autowired
    private FuncEmployeeService employeeService;

    @Autowired
    private CommonAuthService commonAuthService;

    @DubboReference(check = false)
    private ICommonService iCommonService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService iBaseEmployeeExtService;
    @Autowired
    private OpenApplyRecordDao openApplyRecordDao;

    @Autowired
    private ThirdCallbackRecordDao recordDao;

    @Autowired
    private FuncValidService validService;

    public Object createTripApply(ApiRequest request) throws Exception {
        //获取token
        String token = signService.checkSign(request);
        TripApproveCreateReqDTO createReq = JsonUtils.toObj(request.getData(), TripApproveCreateReqDTO.class);
        @Valid @NotNull(message = "审批单申请内容[apply]不可为空") TripApproveApply apply = createReq.getApply();
        if (StringUtils.isNotBlank(apply.getApplyReason())) {
            validService.lengthValid(apply.getApplyReason().trim(), "apply_reason");
        }
        String appId = signService.getAppId(request);
        apply.setEmployeeId(request.getEmployeeId());
        apply.setCompanyId(appId);
        return createTripApprove(token, createReq);
    }

    public Object changeTripApply(ApiRequest request) throws Exception {
        String token = signService.checkSign(request);
        TripApproveChangeReqDTO changeReq = JsonUtils.toObj(request.getData(), TripApproveChangeReqDTO.class);
        // 获取appId
        TripApproveChangeApply apply = changeReq.getApply();
        Assert.notNull(apply, "审批单修改内容[apply]不可为空");
        if (StringUtils.isNotBlank(apply.getApplyReason())) {
            validService.lengthValid(apply.getApplyReason().trim(),"apply_reason");
        }
        apply.setCompanyId(signService.getAppId(request));
        apply.setEmployeeId(request.getEmployeeId());
        return changeTripApprove(token, changeReq);
    }

    /**
     * 查询审批单详情
     *
     * @param apiRequest
     * @return
     * @throws Exception
     */
    public Object getDetailByApplyId(ApiRequest apiRequest) throws Exception {
        String token = signService.checkSign(apiRequest);
        ApplyTripDetailReqDTO applyTripDetailReqDTO = JsonUtils.toObj(apiRequest.getData(), ApplyTripDetailReqDTO.class);
        ValidatorUtils.validateBySpring(applyTripDetailReqDTO);
        // 审批单号
        Integer applyType = applyTripDetailReqDTO.getApplyType();
        String applyId = applyTripDetailReqDTO.getApplyId();
        if (!ObjectUtils.isEmpty(applyType) && !com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(applyId) && FuncIdTypeEnums.THIRD_ID.getKey() == applyType) {
            String appId = signService.getAppId(apiRequest);
            applyId = applyOrderService.queryApplyId(appId, applyId);
            if (ObjectUtils.isEmpty(applyId)) {
                log.info("三方审批单不存在，直接返回");
                return MapUtils.obj2map(FuncResponseUtils.success(new HashMap<>()), false);
            } else {
                applyTripDetailReqDTO.setApplyId(applyId);
            }
        }
        StringBuilder url = new StringBuilder(saasplusUrl);
        url.append("/saas_plus/apply/web/detail?apply_id=").append(applyTripDetailReqDTO.getApplyId());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        String responseBody = restHttpUtils.get(url.toString(), httpHeaders, new HashMap<>());
        log.info("审批单详情查询结束，返回数据为{}", responseBody);
        Map<String, Object> map = analyzeResponse(responseBody);
        if (NumericUtils.obj2int(map.get("code")) == 0) {
            ApplyOrderDetailDTO applyOrderDetailDTO = JSON.parseObject(JSON.toJSONString(map.get("data")), ApplyOrderDetailDTO.class);
            try {
                Map<String, Object> thirdMap = setThirdInfo(applyOrderDetailDTO.getApply().getCompany_id(), applyOrderDetailDTO);
                if (thirdMap != null) {
                    ApplyDetailThirdInfoDTO applyDetailThirdInfoDTO = JSON.parseObject(JSON.toJSONString(thirdMap), new TypeReference<ApplyDetailThirdInfoDTO>() {
                    });
                    applyOrderDetailDTO.setThirdInfo(applyDetailThirdInfoDTO);
                }
            } catch (Exception e) {
            }
            return applyOrderDetailDTO;
        }
        return null;
    }

    /**
     * 内部调用，查询审批单详情
     *
     * @return
     * @throws Exception
     */
    public ApplyOrderDetailDTO getDetailByApplyIdAndToken(String applyId, String token) throws Exception {
        StringBuilder url = new StringBuilder(saasplusUrl);
        url.append("/saas_plus/apply/web/detail?apply_id=").append(applyId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        String responseBody = restHttpUtils.get(url.toString(), httpHeaders, new HashMap<>());
        log.info("审批单详情查询结束，返回数据为{}", responseBody);
        Map<String, Object> map = analyzeResponse(responseBody);
        if (NumericUtils.obj2int(map.get("code")) == 0) {
            ApplyOrderDetailDTO applyOrderDetailDTO = JSON.parseObject(JSON.toJSONString(map.get("data")), ApplyOrderDetailDTO.class);
            return applyOrderDetailDTO;
        }
        return null;
    }

    /**
     * 校验接口返回数据
     *
     * @param result
     * @return
     */
    public Map<String, Object> analyzeResponse(String result) {
        if (ObjectUtils.isEmpty(result)) {
            log.info("[调用外部接口返回值为NULL]");
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_ERROR));
        }
        Map<String, Object> obj = JsonUtils.toObj(result, HashMap.class);
        if (ObjectUtils.isEmpty(obj)) {
            log.info("[调用外部接口返回值的结果集为]{}", result);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_DATA_ERROR));
        }
        Integer code = NumericUtils.obj2int(obj.get("code"));
        if (code != 0) {
            throw new FinhubException(code, com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(obj.get("msg")));
        }
        return obj;
    }

    public Map<String, Object> setThirdInfo(String companyId, ApplyOrderDetailDTO applyOrderDetailDTO) {
        FuncThirdInfoExpressDTO express = new FuncThirdInfoExpressDTO();
        express.setUserExpressList(Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdCommonExpress.builder().express("apply:employee_id").tgtField("user_id").build()));
        express.setDeptExpressList(Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdCommonExpress.builder().express("apply:org_unit_id").tgtField("unit_id").build()));
        express.setApplyExpressList(Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdCommonExpress.builder().express("apply:id").tgtField("apply_id").group("apply").build()));
        List<ApplyCostAttributionDto> costList = applyOrderDetailDTO.getApply().getCost_attribution_list();
        if (!ObjectUtils.isEmpty(costList)) {
            List<FuncThirdInfoExpressDTO.ThirdCostExpress> thirdCostExpresses = costList.stream()
                .map(cost -> {
                    int costCategory = cost.getCost_attribution_category();
                    return FuncThirdInfoExpressDTO.ThirdCostExpress.builder().id(cost.getCost_attribution_id()).costCategory(costCategory).tgtField(costCategory == 1 ? "cost_dept_id" : "cost_project_id").build();
                }).collect(Collectors.toList());
            express.setCostExpressList(thirdCostExpresses);
        } else {
            int costCategory = applyOrderDetailDTO.getApply().getCost_attribution_category();
            List<FuncThirdInfoExpressDTO.ThirdCostExpress> thirdCostExpresses = Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdCostExpress.builder().id(applyOrderDetailDTO.getApply().getCost_attribution_id()).costCategory(costCategory).
                tgtField(costCategory == 1 ? "cost_dept_id" : "cost_project_id").build());
            express.setCostExpressList(thirdCostExpresses);

        }
        express.setUserPhoneExpressList(Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdUserPhoneExpress.builder()
            .phone(applyOrderDetailDTO.getApply().getApplicant_phone())
            .tgtDept("passenger_unit_id")
            .tgtUser("passenger_user_id")
            .build()));
        return thirdInfoService.setThirdInfoEntity(companyId, JSON.parseObject(JSON.toJSONString(applyOrderDetailDTO)), express);
    }

    public void notifyTripApplyAgree(ApiRequest apiRequest) {
        Map<String, String> signMap = commonAuthService.signCheck(apiRequest);
        String companyId = signMap.get("company_id");
        String employeeId = signMap.get("employee_id");
        //类型，0为分贝用户，1为第三方用户
        String employeeType = signMap.get("employee_type");
        String token = employeeService.getEmployeeFbToken(companyId, employeeId, employeeType);
        TripApplyAgreeReqDTO agreeReq = JsonUtils.toObj(apiRequest.getData(), TripApplyAgreeReqDTO.class);
        ThirdCallbackRecord record = recordDao.getApplyByApplyId(agreeReq.getApplyId(), CallbackType.APPLY_TRIP_REVERSE.getType());
        if (record != null) {
            notifyApplyAgree(token, agreeReq);
        } else {
            throw new OpenApiPluginSupportException(SupportRespCode.FB_APPLY_ID_NOT_EXIST,"根据 applyId 查询订单为空");
        }
    }

    /**
     * 非行程管控差旅审批同意
     *
     * @param apiRequest
     */
    public void notifyMultiTripApplyAgree(ApiRequest apiRequest) throws Exception {
        apiRequest.setEmployeeType(1);
        BeanUtils.copyProperties(apiRequest, apiRequest);
        Map<String, String> signMap = commonAuthService.signCheck(apiRequest);
        String companyId = signMap.get("company_id");
        // 让用户传的就是自己的id
        String employeeId = signMap.get("employee_id");
        String token = employeeService.getEmployeeFbToken(companyId, employeeId, "1");
        TripApplyAgreeReqDTO agreeReq = JsonUtils.toObj(apiRequest.getData(), TripApplyAgreeReqDTO.class);
        ValidatorUtils.validateBySpring(agreeReq);
        ThirdCallbackRecord record = recordDao.getApplyByApplyId(agreeReq.getApplyId(), CallbackType.APPLY_NOT_ITINERARY_TRIP_REVERSE.getType());
        if (record != null) {
            notifyMultiApplyAgree(token, agreeReq);
        } else {
            throw new OpenApiPluginSupportException(SupportRespCode.FB_APPLY_ID_NOT_EXIST," 根据 applyId 查询订单为空 ");
        }
    }

    public String createMultiTripApply(ApiRequestBase request) throws Exception {
        MultiTripApproveCreateReqDTO createReq = JsonUtils.toObj(request.getData(), MultiTripApproveCreateReqDTO.class);
        if (null == createReq) {
            throw new OpenApiArgumentException(" 参数异常, 请检查参数是否符合规范");
        }
        if (!ObjectUtils.isEmpty(createReq.getApply())){
            if (StringUtils.isNotBlank(createReq.getApply().getApplyReason())) {
                validService.lengthValid(createReq.getApply().getApplyReason().trim(),"apply_reason");
            }
        }
        if (!ObjectUtils.isEmpty(createReq.getTrip()) && !ObjectUtils.isEmpty(createReq.getTrip().getEstimatedAmount())) {
            MultiTripDTO trip = createReq.getTrip();
            trip.setEstimatedAmount(BigDecimalUtils.yuan2fen(trip.getEstimatedAmount()));
            createReq.setTrip(trip);
        }
        String appId = signService.getAppId(request);
        String empToken = employeeService.getEmployeeFbToken(appId, createReq.getApply().getEmployeeId(), "1");
        // 调用saas
        return doCreateMultiTripApply(empToken, createReq,appId);
    }



    public String updateMultiTripApply(ApiRequestBase request) throws Exception {

        MultiTripApproveCreateReqDTO updateReq = JsonUtils.toObj(request.getData(), MultiTripApproveCreateReqDTO.class);
        if (null == updateReq) {
            throw new OpenApiArgumentException(" 参数异常, 请检查参数是否符合规范");
        }
        if (!ObjectUtils.isEmpty(updateReq.getApply())){
            if (StringUtils.isNotBlank(updateReq.getApply().getApplyReason())) {
                validService.lengthValid(updateReq.getApply().getApplyReason().trim(),"apply_reason");
            }
        }
        if (!ObjectUtils.isEmpty(updateReq.getTrip()) && !ObjectUtils.isEmpty(updateReq.getTrip().getEstimatedAmount())) {
            MultiTripDTO trip = updateReq.getTrip();
            trip.setEstimatedAmount(BigDecimalUtils.yuan2fen(trip.getEstimatedAmount()));
            updateReq.setTrip(trip);
        }
        // 获取token
        String appId = signService.getAppId(request);
        String empToken = employeeService.getEmployeeFbToken(appId, updateReq.getApply().getEmployeeId(), "1");
        // 调用saas
        return doUpdateMultiTripApply(empToken, updateReq,appId);
    }
}
