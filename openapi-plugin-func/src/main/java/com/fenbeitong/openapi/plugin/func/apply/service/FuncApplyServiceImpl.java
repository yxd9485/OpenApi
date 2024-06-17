package com.fenbeitong.openapi.plugin.func.apply.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.constant.ApplyType;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.util.BeanCopierUtil;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlStrategyService;
import com.fenbeitong.openapi.plugin.func.apply.dto.*;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyRepulseDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyTripInfoDTO;
import com.fenbeitong.openapi.plugin.func.common.FuncApplyCategoryEnum;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenApplyTypeEnum;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.saasplus.api.model.dto.apply.ThirdApplyCancelDTO;
import com.fenbeitong.saasplus.api.service.apply.IApplyOrderService;
import com.fenbeitong.saasplus.api.service.apply.IApplyOrderThirdRpcService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncCarApplyServiceImpl</p>
 * <p>Description: 申请单通用 </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/7 11:53 AM
 */
@ServiceAspect
@Service
@Slf4j
public class FuncApplyServiceImpl {

    //    private static final Long COMPANY_APPLY_LIST_CONFIGID = 2518L;
    private static final Long COMPANY_APPLY_LIST_CONFIGID = 2601L;
    private static final Long COMPANY_APPLY_DETAIL_CONFIGID = 2602L;
    private static final Integer NO_TRAVEL = 23;
    private static final String TRIP_LIST = "trip_list";
    private static final String multitrip_type = "multitrip_type";
    private static final String multitrip_cities = "multitrip_cities";

    private static final String APPLY = "apply";

    @Autowired
    private IEtlService etlService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private OpenApplyServiceImpl openApplyService;

    @Autowired
    private IEtlStrategyService etlStrategyService;

    @Autowired
    private FuncTripApplyServiceImpl tripApplyService;

    @DubboReference(check = false)
    private ICommonService commonService;
    @Value("${host.saas_plus}")
    private String saasplusUrl;
    @Autowired
    private RestHttpUtils restHttpUtils;

    @Autowired
    private CommonAuthService commonAuthService;

    @Autowired
    private FuncEmployeeService employeeService;

    @Autowired
    private CommonAuthService signService;

    @DubboReference(check = false)
    private IApplyOrderThirdRpcService iApplyOrderThirdRpcService;

    @DubboReference(check = false)
    private IApplyOrderService applyOrderService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @DubboReference(check = false)
    private ICommonService iCommonService;


    public Map<String, Object> getCompanyApproveList(String companyId, FuncCompanyApplyListReqDTO funcCompanyApplyListReqDTO) {
        Map<String, Object> result = new HashMap<>();
        Integer count = 0;
        List<Map<String, Object>> dataList = new ArrayList<>();
        String token = userCenterService.getUcSuperAdminToken(companyId);
        CompanyApplyListReqDTO companyApplyListReqDTO = new CompanyApplyListReqDTO();
        //1:差旅   11：用餐  23:非行程
        if (funcCompanyApplyListReqDTO.getType() != 1 && funcCompanyApplyListReqDTO.getType() != 11 && !Objects.equals(funcCompanyApplyListReqDTO.getType(), OpenApplyTypeEnum.MULTI_TRIP.getValue())) {
            return result;
        }
        BeanCopierUtil.copy(funcCompanyApplyListReqDTO, companyApplyListReqDTO);
        if (funcCompanyApplyListReqDTO.getType().equals(OpenApplyTypeEnum.MULTI_TRIP.getValue())) {
            ApplyNoTravelResponseDTO companyApprove = openApplyService.getCompanyApprove(token, companyApplyListReqDTO);
            if (!ObjectUtils.isEmpty(companyApprove)){
                return this.getNoTravel(companyApprove,funcCompanyApplyListReqDTO);
            }
        }
        Map<String, Object> companyApproveList = openApplyService.getCompanyApproveList(token, companyApplyListReqDTO);

        if (!ObjectUtils.isEmpty(companyApproveList)) {
            count = NumericUtils.obj2int(companyApproveList.get("totalCount"), 0);
            if (count > 0) {
                dataList = (List<Map<String, Object>>) companyApproveList.get("results");
                List<Map<String, Object>> transform = etlStrategyService.transfer(COMPANY_APPLY_LIST_CONFIGID, dataList);
                //result.put("results", etlService.transform(COMPANY_APPLY_LIST_CONFIGID, dataList));
                result.put("results", transform);
                Integer pageIndex = funcCompanyApplyListReqDTO.getPageIndex();
                Integer pageSize = funcCompanyApplyListReqDTO.getPageSize();
                result.put("total_count", count);
                result.put("total_pages", (count + pageSize - 1) / pageSize);
                result.put("page_index", pageIndex);
                result.put("page_size", pageSize);
            }
        }
        return result;
    }

    public Map<String, Object> getCompanyApproveDetail(String companyId, FuncCompanyApplyDetailReqDTO funcCompanyApplyDetailReqDTO) {
        Map<String, Object> result = new HashMap<>();
        String token = userCenterService.getUcSuperAdminToken(companyId);

        if (StringUtils.isEmpty(funcCompanyApplyDetailReqDTO.getApplyId())) {
            String thirdApplyId = funcCompanyApplyDetailReqDTO.getThirdApplyId();
            if (StringUtils.isEmpty(thirdApplyId)) {
                throw new OpenApiArgumentException("审批单id【apply_id】和三方审批单id【third_apply_id】至少一个必填，请检查参数");
            }
            //三方审批单id转换
            String applyId = applyOrderService.queryApplyId(companyId, thirdApplyId);
            if (ObjectUtils.isEmpty(applyId)) {
                log.info("三方审批单id转换失败，companyId:{},thirdApplyId{}", companyId, thirdApplyId);
                throw new OpenApiArgumentException("三方审批单id有误，请检查");
            }
            funcCompanyApplyDetailReqDTO.setApplyId(applyId);
        }

        CompanyApplyDetailReqDTO companyApplyDetailReqDTO = new CompanyApplyDetailReqDTO();
        BeanCopierUtil.copy(funcCompanyApplyDetailReqDTO, companyApplyDetailReqDTO);
        Map<String, Object> companyApproveDetail = openApplyService.getCompanyApproveDetail(token, companyApplyDetailReqDTO);
        ApplyOrderDetailDTO applyOrderDetailDto = JsonUtils.toObj(JsonUtils.toJson(companyApproveDetail), ApplyOrderDetailDTO.class);
        Map<String, Object> thirdInfo = tripApplyService.setThirdInfo(companyId, applyOrderDetailDto);
        if (thirdInfo != null) {
            ApplyDetailThirdInfoDTO thirdInfoDto = JsonUtils.toObj(JsonUtils.toJson(thirdInfo), ApplyDetailThirdInfoDTO.class);
            applyOrderDetailDto.setThirdInfo(thirdInfoDto);
        }
        List<ApplyCostAttributionDto> attributionList = applyOrderDetailDto.getApply().getCost_attribution_list();
        if (!ObjectUtils.isEmpty(attributionList)) {
            attributionList.forEach(ca -> {
                // 三方费用归属id查询
                Integer category = ca.getCost_attribution_category();
                String thirdId = covertCostInfo(ca, companyId, category);
                ca.setThird_cost_attribution_id(thirdId);
            });
        }
        List<UserContactDTO> guestList = applyOrderDetailDto.getGuest_list();
        if (!ObjectUtils.isEmpty(guestList)) {
            List<String> employeeIdList = guestList.stream().filter(UserContactDTO::getIs_employee).map(UserContactDTO::getId).collect(Collectors.toList());
            List<CommonIdDTO> userIdDtoList = ObjectUtils.isEmpty(employeeIdList) ? null : commonService.queryIdDTO(companyId, employeeIdList, 1, 3);
            Map<String, CommonIdDTO> commonIdDtoMap = ObjectUtils.isEmpty(userIdDtoList) ? null : userIdDtoList.stream().collect(Collectors.toMap(CommonIdDTO::getId, Function.identity()));
            for (UserContactDTO userContactDTO : guestList) {
                String selectedEmployeeId = userContactDTO.getId();
                CommonIdDTO commonIdDto = commonIdDtoMap == null ? null : commonIdDtoMap.get(selectedEmployeeId);
                if (commonIdDto != null) {
                    userContactDTO.setThird_employee_id(commonIdDto.getThirdId());
                }
            }
        }
        //三方部门id
        String orgUnitId = applyOrderDetailDto.getApply().getOrg_unit_id();
        List<CommonIdDTO> userIdDtoList = ObjectUtils.isEmpty(orgUnitId) ? null : commonService.queryIdDTO(companyId, Lists.newArrayList(orgUnitId), 1, 1);
        if (!ObjectUtils.isEmpty(userIdDtoList)) {
            applyOrderDetailDto.getApply().setThird_department_id(userIdDtoList.get(0).getThirdId());
        }

        if (!ObjectUtils.isEmpty(companyApproveDetail)) {
            List<Map<String, Object>> travelTimelist = JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(companyApproveDetail, "apply:travelDetails")), List.class);
            if (!ObjectUtils.isEmpty(travelTimelist)) {
                List<Map<String, Object>> travelMap = travelTimelist.stream().map(p -> {
                    Object travelType = p.get("travelType");
                    Object travelTime = p.get("travelTime");
                    p.clear();
                    p.put("travel_type", travelType);
                    p.put("travel_time", travelTime);
                    return p;
                }).collect(Collectors.toList());
                if (!ObjectUtils.isEmpty(applyOrderDetailDto) && !ObjectUtils.isEmpty(applyOrderDetailDto.getApply())) {
                    applyOrderDetailDto.getApply().setTravel_time_details(travelMap);
                }
            }
        }
        if (!ObjectUtils.isEmpty(applyOrderDetailDto.getTrip_list()) && !ObjectUtils.isEmpty(applyOrderDetailDto.getTrip_list().get(0))) {
            String estimated_left = applyOrderDetailDto.getTrip_list().get(0).getEstimated_left();
            if (!StringUtils.isEmpty(estimated_left)) {
                String usableStr = estimated_left.substring(estimated_left.indexOf("￥") + 1);
                BigDecimal usableBudget = new BigDecimal(usableStr.replaceAll(",", ""));
                applyOrderDetailDto.getTrip_list().get(0).setUseable_budget(usableBudget);
            }
        }


        if (!ObjectUtils.isEmpty(companyApproveDetail)) {
            List<Map<String, Object>> transform = etlStrategyService.transfer(COMPANY_APPLY_DETAIL_CONFIGID, Lists.newArrayList(JsonUtils.toObj(JsonUtils.toJson(applyOrderDetailDto), Map.class)));
            result = transform.get(0);
            ApplyOrderContractDto app = applyOrderDetailDto.getApply();
            if (!ObjectUtils.isEmpty(app) && app.getType().equals(NO_TRAVEL)) {
                this.addTravel(result,applyOrderDetailDto);
            }
            Object apply = result.get(APPLY);
            Map<String, Object> newApply = JsonUtils.toObj(JsonUtils.toJson(apply), new TypeReference<Map<String, Object>>() {
            });
            String rootApplyId = applyOrderDetailDto.getApply().getRoot_apply_order_id();
            String parentApplyId = applyOrderDetailDto.getApply().getParent_apply_order_id();
            if (!ObjectUtils.isEmpty(rootApplyId)) {
                newApply.put("root_id", rootApplyId);
                newApply.put("third_root_id", this.applyIdTran(companyId, rootApplyId));
            }
            if (!ObjectUtils.isEmpty(parentApplyId)) {
                newApply.put("parent_id", parentApplyId);
                newApply.put("third_parent_id", this.applyIdTran(companyId, parentApplyId));
            }
            result.put(APPLY, JSON.parse(JSON.toJSONString(newApply)));
        }
        return result;
    }

    public void addTravel(Map<String, Object> result,ApplyOrderDetailDTO applyOrderDetailDto) {
        if (!ObjectUtils.isEmpty(result) && !ObjectUtils.isEmpty(applyOrderDetailDto)) {
            if (!ObjectUtils.isEmpty(result.get(TRIP_LIST)) && !ObjectUtils.isEmpty(applyOrderDetailDto.getTrip_list())) {
                try {
                    ApplyTripInfoDTO applyTripInfoDTO = applyOrderDetailDto.getTrip_list().get(0);
                    List list = (List) result.get(TRIP_LIST);
                    Object o = list.get(0);
                    Map<String, Object> map = JsonUtils.toObj(JsonUtils.toJson(o), new TypeReference<Map<String, Object>>() {
                    });
                    List<ApplyTripInfoDTO.MultiTripCity> multi_trip_city = applyTripInfoDTO.getMulti_trip_city();
                    List<ApplyTripInfoDTO.MultiTripCityOutput> multi_trip_city_out = Lists.newArrayList();
                    if (!ObjectUtils.isEmpty(multi_trip_city)) {
                        multi_trip_city.forEach(e->{
                            ApplyTripInfoDTO.MultiTripCityOutput multiTripCityOutput = new ApplyTripInfoDTO.MultiTripCityOutput();
                            multiTripCityOutput.setId(e.getKey());
                            multiTripCityOutput.setName(e.getValue());
                            multi_trip_city_out.add(multiTripCityOutput);
                        });
                    }
                    map.put(multitrip_type,applyTripInfoDTO.getMulti_trip_scene());
                    map.put(multitrip_cities,multi_trip_city_out);
                    List objects = Lists.newArrayList();
                    objects.add(JSON.parseObject(JSON.toJSONString(map)));
                    result.put(TRIP_LIST,objects);
                } catch (Exception e) {
                    log.info("兼容非行程审批异常 异常信息->>>",e);
                }
            }
        }




    }

    //三方新审批终审驳回
    public void applyRepulse(ApiRequestBase apiRequest) {
        ApplyRepulseDTO applyRepulseDTO = JsonUtils.toObj(apiRequest.getData(), ApplyRepulseDTO.class);
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(applyRepulseDTO);
        if (openApiBindException != null) {
            throw openApiBindException;
        }
        //将场景类型进行转换
        if (!StringUtil.isEmpty(FuncApplyCategoryEnum.getCategoryByType(applyRepulseDTO.getCategory()))) {
            applyRepulseDTO.setCategory(FuncApplyCategoryEnum.getCategoryByType(applyRepulseDTO.getCategory()));
        } else {
            throw new OpenApiArgumentException("参数【category】错误");
        }
        String companyId = signService.getAppId(apiRequest);
        String token = employeeService.getEmployeeFbToken(companyId, applyRepulseDTO.getEmployeeId(), applyRepulseDTO.getEmployeeType());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setConnection("Keep-Alive");

        String repluseRes = RestHttpUtils.postJson(saasplusUrl + "/saas_plus/apply/third/repulse", httpHeaders, JsonUtils.toJson(applyRepulseDTO));
        log.info("审批单详情查询结束，返回数据为{}", repluseRes);
        BaseDTO<Map<String, String>> repulseResult = JsonUtils.toObj(repluseRes, BaseDTO.class);
        if (repulseResult == null || !repulseResult.success()) {
            String msg = repulseResult == null ? "" : Optional.ofNullable(repulseResult.getMsg()).orElse("");
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.REPULSE_APPLY_FAILED), ":" + msg);
        }
    }

    //三方新审批终审撤回
    public void applyRevoke(ApiRequestBase apiRequest) {
        ApplyRevokeDTO applyRevokeDTO = JsonUtils.toObj(apiRequest.getData(), ApplyRevokeDTO.class);
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(applyRevokeDTO);
        if (openApiBindException != null) {
            throw openApiBindException;
        }
        //将场景类型进行转换
        if (!StringUtil.isEmpty(FuncApplyCategoryEnum.getCategoryByType(applyRevokeDTO.getCategory()))) {
            applyRevokeDTO.setCategory(FuncApplyCategoryEnum.getCategoryByType(applyRevokeDTO.getCategory()));
        } else {
            throw new OpenApiArgumentException("参数【category】错误");
        }
        String companyId = signService.getAppId(apiRequest);
        String token = employeeService.getEmployeeFbToken(companyId, applyRevokeDTO.getEmployeeId(), applyRevokeDTO.getEmployeeType());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setConnection("Keep-Alive");

        String revokeRes = RestHttpUtils.postJson(saasplusUrl + "/saas_plus/apply/third/revoke", httpHeaders, JsonUtils.toJson(applyRevokeDTO));
        log.info("三方新审批终审撤回，返回数据为{}", revokeRes);

        BaseDTO<Map<String, String>> revokeResult = JsonUtils.toObj(revokeRes, BaseDTO.class);
        if (revokeResult == null || !revokeResult.success()) {
            String msg = revokeResult == null ? "" : Optional.ofNullable(revokeResult.getMsg()).orElse("");
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.REVERT_APPLY_FAILED), ":" + msg);
        }
    }

    public boolean applyCancle(ApiRequest apiRequest) {
        ApplyCancelDTO applyCancelDTO = JsonUtils.toObj(apiRequest.getData(), ApplyCancelDTO.class);
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(applyCancelDTO);
        if (openApiBindException != null) {
            throw openApiBindException;
        }
        String companyId = signService.getAppId(apiRequest);
        LoginResVO loginResVO = employeeService.loginAuthInit(companyId, apiRequest.getEmployeeId(), apiRequest.getEmployeeType().toString());
        String applyId = applyOrderService.queryApplyId(companyId, applyCancelDTO.getThirdId());
        if (StringUtil.isEmpty(applyId)) {
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.THIRD_APPLY_CANCEL_FAILED), ":" + "三方审批单ID不存在");
        }
        ThirdApplyCancelDTO thirdApplyCancelDTO = new ThirdApplyCancelDTO();
        thirdApplyCancelDTO.setApply_id(applyId);
        thirdApplyCancelDTO.setThird_id(applyCancelDTO.getThirdId());
        thirdApplyCancelDTO.setCancel_reason(applyCancelDTO.getCancelReason());
        thirdApplyCancelDTO.setCancel_reason_desc(applyCancelDTO.getCancelReasonDesc());
        return iApplyOrderThirdRpcService.thirdCancel(thirdApplyCancelDTO, loginResVO.getUser_info().getId(), companyId, loginResVO.getLogin_info().getToken());
    }

    //三方新审批终审同意
    public void applyAgree(String companyId, ApiRequestBase apiRequest) throws BindException {
        ApplyAgreeDTO applyAgreeDTO = JsonUtils.toObj(apiRequest.getData(), ApplyAgreeDTO.class);
        ValidatorUtils.validateBySpring(applyAgreeDTO);
        String token = employeeService.getEmployeeFbToken(companyId, applyAgreeDTO.getEmployeeId(), applyAgreeDTO.getEmployeeType());
        commonApplyService.applyAgree(token, applyAgreeDTO);
    }

    private String covertCostInfo(ApplyCostAttributionDto ca, String companyId, Integer type) {
        // 费用归属三方id转换成分贝通ID
        Integer businessType = null;
        if (1 == type) {
            businessType = 1;
            // 项目ID
        } else if (2 == type) {
            businessType = 2;
            // 自定义档案项目
        } else if (3 == type) {
            businessType = 5;
        }

        String fbtId = ca.getCost_attribution_id();
        List<CommonIdDTO> commonIdDTOs = iCommonService.queryIdDTO(companyId, Lists.newArrayList(fbtId), 1, businessType);
        if (commonIdDTOs != null && commonIdDTOs.size() > 0) {
            CommonIdDTO commonIdDTO = commonIdDTOs.get(0);
            String fbId = "";
            if (!ObjectUtils.isEmpty(commonIdDTO.getId()) && commonIdDTO.getId().equals(fbtId)) {
                fbId = commonIdDTO.getThirdId();
            }
            if (!ObjectUtils.isEmpty(commonIdDTO.getThirdId()) && commonIdDTO.getThirdId().equals(fbtId)) {
                fbId = commonIdDTO.getId();
            }
            return fbId;
        }
        return null;
    }


    private String applyIdTran(String companyId, String applyId){
        String thirdId = null;
        if (ObjectUtils.isEmpty(companyId) || ObjectUtils.isEmpty(applyId)) {
            return null;
        }
        SaasApplyCustomFieldRespDTO applyCustomFields = commonApplyService.getApplyCustomFields(companyId, applyId);
        if (!ObjectUtils.isEmpty(applyCustomFields) && !ObjectUtils.isEmpty(applyCustomFields.getData())) {
             SaasApplyCustomFieldRespDTO.SaasApplyCustomFieldResult data = applyCustomFields.getData();
             thirdId = data.getThirdApplyId();
        }
        return thirdId;
    }






    /**
     * 兼容非行程审列表
     * @param companyApprove 获取主版本 审批单列表
     * @param funcCompanyApplyListReqDTO 审批单列表入参
     * @return
     */
    private Map<String, Object> getNoTravel(ApplyNoTravelResponseDTO companyApprove,FuncCompanyApplyListReqDTO funcCompanyApplyListReqDTO) {
        ApplyNoTravelResDTO applyNoTravelResDTO = new ApplyNoTravelResDTO();
        List<ApplyNoTravelResDTO.ResultData> result = Lists.newArrayList();
        Integer totalCount = ObjectUtils.isEmpty(companyApprove.getTotalCount()) ? 0 : companyApprove.getTotalCount();
        Integer pageIndex = funcCompanyApplyListReqDTO.getPageIndex();
        Integer pageSize = funcCompanyApplyListReqDTO.getPageSize();
        applyNoTravelResDTO.setTotalCount(totalCount);
        applyNoTravelResDTO.setPageIndex(pageIndex);
        applyNoTravelResDTO.setPageSize(funcCompanyApplyListReqDTO.getPageSize());
        applyNoTravelResDTO.setTotalPages((totalCount + pageSize - 1) / pageSize);
        if (ObjectUtils.isEmpty(companyApprove.getResults())) {
            List<ApplyNoTravelResponseDTO.ApplyListResults> results = companyApprove.getResults();
            results.forEach(data->{
                ApplyNoTravelResDTO.ResultData build = ApplyNoTravelResDTO.ResultData.builder()
                    .applyId(data.getId())
                    .phone(data.getPhone())
                    .proposer(data.getProposer())
                    .createTime(data.getCreate_time())
                    .type(data.getType())
                    .applyOrderTypeName(data.getApply_order_type_name())
                    .state(data.getState())
                    .budget(data.getBudget())
                    .travelDay(data.getTravelDay())
                    .build();
                result.add(build);
            });
        }
        applyNoTravelResDTO.setResults(result);
        return JsonUtils.toObj(JsonUtils.toJson(applyNoTravelResDTO), new TypeReference<Map<String, Object>>() {
        });
    }


}
