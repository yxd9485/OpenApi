package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.core.constant.EventConstant;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.saas.dto.SaasPushEvents;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyOrderContractDto;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyOrderDetailDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyTripInfoDTO;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncCarApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncTripApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.support.apply.constant.TripApplyRelationEnum;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.entity.TripApplyRelation;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenTripApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenTripApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.city.constants.CityRelationType;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.util.ThreadUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyNewDto;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TripApplyEventHandler extends EventHandler<SaasPushEvents> {

    @Value("${host.harmony}")
    private String harmonyHost;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private FuncEmployeeService employeeService;

    @Autowired
    private FuncTripApplyServiceImpl tripApplyService;

    @Autowired
    private FuncCarApplyServiceImpl funcCarApplyServiceImpl;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private OpenTripApplyServiceImpl openTripApplyService;

    @Autowired
    private IOpenTripApplyService iOpenTripApplyService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private UcCompanyServiceImpl companyService;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Override
    public boolean process(SaasPushEvents saasPushEvents, Object... args) {
        OpenSysConfig openSysConfig = openSysConfigService.getOpenSysConfigByTypeAndCode(OpenSysConfigType.AUTOMATIC_CREATE_CAR_APPLY.getType(), saasPushEvents.getCompanyId());
        if (ObjectUtils.isEmpty(openSysConfig)) {
            log.info("该企业没有配置自动生成用车:{}", saasPushEvents.getCompanyId());
            return false;
        }
        if (EventConstant.MSG_TYPE_APPLY.equals(saasPushEvents.getMsgType())) {
            String msg = saasPushEvents.getMsg();
            Map map = JsonUtils.toObj(msg, Map.class);
            String url = "";
            CompanyNewDto companyNewDto = companyService.getCompanyService().queryCompanyNewByCompanyId(saasPushEvents.getCompanyId());
            String token = employeeService.getEmployeeFbToken(saasPushEvents.getCompanyId(), saasPushEvents.getUserId(), "0");
            if (map != null) {
                Integer applyType = (Integer) map.get("apply_type");
                String id = StringUtils.obj2str(map.get("id"));
                String settingType = StringUtils.obj2str(map.get("setting_type"));
                String viewType = StringUtils.obj2str(map.get("view_type"));
//                //saas_push的view_type，1申请人，2审批人，3抄送人。 给前端跳转的type，1.审批人，2申请人，3抄送人
//                if ("1".equals(viewType)) {
//                    viewType = "2";
//                } else if ("2".equals(viewType)) {
//                    viewType = "1";
//                }
                //行程审批
                if (("1".equals(settingType) || "15".equals(settingType)) && "1".equals(viewType)) {
                    if (applyType != null && !StringUtils.isBlank(id)) {
                        //差旅审批
                        if (applyType == 1 || applyType == 23) {
                            try {
                                //saas延迟，等3秒
                                ThreadUtils.sleep(3, TimeUnit.SECONDS);
                                ApplyOrderDetailDTO applyOrderDetailDTO = tripApplyService.getDetailByApplyIdAndToken(id, token);
                                if (applyOrderDetailDTO != null && applyOrderDetailDTO.getApply() != null && applyOrderDetailDTO.getApply().getState() == 4) {
                                    //推送消息
                                    addCallBackRecord(saasPushEvents.getCompanyId(), companyNewDto.getCompanyName(), applyOrderDetailDTO.getApply());
                                    if (StringUtil.isNotEmpty(applyOrderDetailDTO.getApply().getParent_apply_order_id())) {
                                        log.info("差旅变更审批单:{}", applyOrderDetailDTO.getApply().getParent_apply_order_id());
                                        // 取消原来关联的用车审批
                                        List<TripApplyRelation> tripApplyRelationList = iOpenTripApplyService.listTripApplyRelation(TripApplyRelation.builder().type(TripApplyRelationEnum.TRIP_WITH_CAR.getValue())
                                                .tripApplyId(applyOrderDetailDTO.getApply().getParent_apply_order_id()).companyId(saasPushEvents.getCompanyId()).build());
                                        TripApproveChangeApply tripApproveChangeApply =new TripApproveChangeApply();
                                        tripApproveChangeApply.setCompanyId(saasPushEvents.getCompanyId());
                                        if (!ObjectUtils.isEmpty(tripApplyRelationList)) {
                                            for (TripApplyRelation tripApplyRelation : tripApplyRelationList) {
                                                TripApproveChangeReqDTO build = TripApproveChangeReqDTO.builder()
                                                        .applyId(tripApplyRelation.getCarApplyThirdId())
                                                        .thirdType(2)
                                                        .apply(tripApproveChangeApply)
                                                        .build();
                                                openTripApplyService.cancelTripApprove(token, build);
                                            }
                                        }
                                    }
                                    //生成新的用车审批
                                    CommonApplyReqDTO commonApplyReqDTO = getCarApplyByDetail(applyOrderDetailDTO);
                                    CarApproveCreateReqDTO carApproveCreateReqDTO = commonApplyService.tripApplyGenerateWithInHotelCarApply(commonApplyReqDTO);
                                    Set<String> startCityIdSet = carApproveCreateReqDTO.getTripList().stream().flatMap(c -> Lists.newArrayList(c.getStartCityId().split(",")).stream()).collect(Collectors.toSet());
                                    String resultJson = RestHttpUtils.postJson(harmonyHost + "/business/common/map/area/ancestors", JsonUtils.toJson(startCityIdSet));
                                    Map<String, Object> resultMap = JsonUtils.toObj(resultJson, Map.class);
                                    Map<String, Object> dataMap = resultMap == null ? null : (Map<String, Object>) resultMap.get("data");
                                    List<CarApproveDetail> tripList = carApproveCreateReqDTO.getTripList();
                                    tripList.forEach(t -> {
                                        String cityIds = t.getStartCityId();
                                        String[] split = cityIds.split(",");
                                        Set<String> newCityIds = new HashSet<>();
                                        for (int i = 0; i < split.length; i++) {
                                            String parentAreaId = (String) dataMap.get(split[i]);
                                            parentAreaId = parentAreaId == null || "-1".equals(parentAreaId) ? split[i] : parentAreaId;
                                            newCityIds.add(parentAreaId);
                                        }
                                        t.setStartCityId(String.join(",", newCityIds));
                                    });
                                    //创建用车时捕获异常，用车创建失败只发告警，保持差旅审批成功且不被重试造成重复
                                    CreateApplyRespDTO feiShuCarApprove = funcCarApplyServiceImpl.createCarApprove(token, carApproveCreateReqDTO);
                                    iOpenTripApplyService.addTripApplyRelation(TripApplyRelation.builder().carApplyId(feiShuCarApprove.getId()).tripApplyId(id).carApplyThirdId(carApproveCreateReqDTO.getApply().getThirdId()).
                                            type(TripApplyRelationEnum.TRIP_WITH_CAR.getValue()).companyId(saasPushEvents.getCompanyId()).createTime(new Date()).build());
                                    if (ObjectUtils.isEmpty(feiShuCarApprove) || StringUtils.isBlank(feiShuCarApprove.getId())) {
                                        log.info("飞书差旅生成用车审批失败, 用车dto:" + JsonUtils.toJson(carApproveCreateReqDTO));
                                    }
                                }
                            } catch (Exception e) {
                                log.warn("行程审批自动生成审批单", e);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }


    /**
     * 根据行程审批详情生成用车审批
     *
     * @param applyOrderDetailDTO
     * @return
     */
    public CommonApplyReqDTO getCarApplyByDetail(ApplyOrderDetailDTO applyOrderDetailDTO) {
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        List<CommonApplyTrip> tripList = new ArrayList<>();
        if (ObjectUtils.isEmpty(applyOrderDetailDTO)) {
            return null;
        }
        //差旅
        List<ApplyTripInfoDTO> peers = applyOrderDetailDTO.getTrip_list();
        for (ApplyTripInfoDTO applyTripInfoDTO : peers) {
            //开始时间
            String start = applyTripInfoDTO.getStart_time();
            //结束时间
            String end = applyTripInfoDTO.getEnd_time();
            //单程往返
            int rountTrip = applyTripInfoDTO.getTrip_type() != null ? applyTripInfoDTO.getTrip_type() : 1;
            //国际机票暂时不考虑
            int tripType = applyTripInfoDTO.getType();
            CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
            commonApplyTrip.setType(tripType);
            commonApplyTrip.setTripType(rountTrip);
            commonApplyTrip.setStartCityName(applyTripInfoDTO.getStart_city_name());
            commonApplyTrip.setArrivalCityName(applyTripInfoDTO.getArrival_city_name());
            commonApplyTrip.setStartTime(StringUtils.isBlank(start) ? end : start);
            commonApplyTrip.setEndTime(StringUtils.isBlank(end) ? start : end);
            commonApplyTrip.setEstimatedAmount(applyTripInfoDTO.getEstimated_amount() != null ? applyTripInfoDTO.getEstimated_amount().intValue() : 0);
            commonApplyTrip.setStartCityId(applyTripInfoDTO.getStart_city_id());
            commonApplyTrip.setArrivalCityId(applyTripInfoDTO.getArrival_city_id());
            commonApplyTrip.setCityRelationType(CityRelationType.FENBEITONG.getCode());
            tripList.add(commonApplyTrip);
        }

        CommonApply commonApply = new CommonApply();
        commonApply.setApplyReason(applyOrderDetailDTO.getApply().getApply_reason());
        commonApply.setApplyReasonDesc(applyOrderDetailDTO.getApply().getApply_reason_desc());
        commonApply.setThirdRemark(applyOrderDetailDTO.getApply().getApply_reason());
        commonApply.setThirdId(applyOrderDetailDTO.getApply().getId());
        commonApply.setType(applyOrderDetailDTO.getApply().getType());
        commonApply.setFlowType(applyOrderDetailDTO.getApply().getFlow_type());
        commonApply.setBudget(applyOrderDetailDTO.getApply().getBudget() != null ? applyOrderDetailDTO.getApply().getBudget().intValue() : 0);
        commonApply.setEmployeeId(applyOrderDetailDTO.getApply().getEmployee_id());
        commonApply.setCompanyId(applyOrderDetailDTO.getApply().getCompany_id());
        commonApplyReqDTO.setApply(commonApply);
        commonApplyReqDTO.setTripList(tripList);
        return commonApplyReqDTO;
    }

    /**
     * 创建推送的消息记录
     *
     * @param companyId
     * @param companyName
     * @param apply
     */
    public void addCallBackRecord(String companyId, String companyName, ApplyOrderContractDto apply) {
        ThirdCallbackRecord record = new ThirdCallbackRecord();
        record.setApplyType(apply.getApply_order_type());
        record.setApplyTypeName("行程审批单通过后自动推送");
        record.setApplyId(apply.getId());
        record.setCompanyId(companyId);
        record.setCompanyName(companyName);
        record.setApplyName(apply.getApplicant_name());
        record.setContactName(apply.getApplicant_name());
        record.setUserName(apply.getUser_name());
        //订单审批单
        record.setCallbackType(CallbackType.APPLY_TRIP_REVERSE.getType());
//        CallbackCommonDTO callbackCommonDTO=new CallbackCommonDTO();
//        callbackCommonDTO.setType(2);
//        callbackCommonDTO.setDataId(apply.getId());
//        Map<String,Object> data=new HashMap<>();
//        data.put("status",apply.getState());
//        if (StringUtil.isNotEmpty(apply.getParent_apply_order_id())) {
//            data.put("originApply",0);
//        }else {
//            data.put("originApply",1);
//        }
//        callbackCommonDTO.setData(data);
        Map<String, Object> data = new HashMap<>();
        data.put("apply_id", apply.getId());
        if (StringUtil.isNotEmpty(apply.getParent_apply_order_id())) {
            data.put("status", 0);
        } else {
            data.put("status", 1);
        }
        record.setCallbackData(JsonUtils.toJson(data));
        callbackRecordDao.saveSelective(record);
        businessDataPushService.pushData(companyId, record, 0, 2);
    }


}
