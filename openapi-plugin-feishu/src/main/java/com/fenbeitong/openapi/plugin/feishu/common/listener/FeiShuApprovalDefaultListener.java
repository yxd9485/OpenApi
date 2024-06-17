package com.fenbeitong.openapi.plugin.feishu.common.listener;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalFormCommonDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCarFormParseDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.util.FeiShuParseTimeUtils;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuTripType;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuUserInfoDTO;
import com.fenbeitong.openapi.plugin.feishu.common.util.FeishuTaxiRuleUtils;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaEmployeeService;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyGuest;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyTrip;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenApplyService;
import com.fenbeitong.openapi.plugin.support.city.constants.CityRelationType;
import com.fenbeitong.openapi.plugin.support.common.service.CommonService;
import com.fenbeitong.openapi.plugin.support.util.DateUtil;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Primary
@ServiceAspect
@Service
@Slf4j
public class FeiShuApprovalDefaultListener implements FeiShuApprovalListener {
    @Autowired
    private FeiShuEiaEmployeeService feiShuEiaEmployeeService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private IOpenApplyService openApplyService;

    /**
     * 差旅审批模板解析
     */
    @Override
    public CommonApplyReqDTO parseFeiShuBusinessForm(String companyId, String corpId, String approvalId, String form) {
       // String jsonForm = form.replaceAll("\\\\", "");
        List<LinkedHashMap> list = JsonUtils.toObj(form, List.class);
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        List<CommonApplyTrip> tripList = new ArrayList();
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        for (LinkedHashMap map : list) {
            FeiShuApprovalFormDTO feiShuApprovalFormDTO=new FeiShuApprovalFormDTO(map);
            FeiShuApprovalFormDTO.Value value = feiShuApprovalFormDTO.getValue();
            if (ObjectUtils.isEmpty(value)) {
                continue;
            }
            //申请事由
            String reason = value.getReason();
            List<FeiShuApprovalFormDTO.Schedule> schedules = value.getSchedule();
            if (ObjectUtils.isEmpty(schedules)) {
                return null;
            }
            //同行人信息
            List<String> peers = value.getPeer();
            for (FeiShuApprovalFormDTO.Schedule schedule : schedules) {
                //使用场景
                String transport = schedule.getTransport();
                //开始时间
                String start = schedule.getStart();
                //结束时间
                String end = schedule.getEnd();
                //出发地
                String departure = schedule.getDeparture();
                //目的地
                String destination = schedule.getDestination();
                //单程往返
                String oneRound = schedule.getOneRound();
                //日期
                start = start.substring(0, start.indexOf("T"));
                end = FeiShuParseTimeUtils.getEndTime( end );
                //出发和目的城市
                String[] departureSplit = departure.split("/");
                if (departureSplit.length == 2) {
                    departure = departureSplit[1];
                } else if (departureSplit.length > 2) {
                    departure = departureSplit[2];
                }
                String[] destinationSplit = destination.split("/");
                if (destinationSplit.length == 2) {
                    destination = destinationSplit[1];
                } else if (destinationSplit.length > 2) {
                    destination = destinationSplit[2];
                }
                if (departure.endsWith("市")) {
                   // departure = departure;
                    departure = departure;
                }
                if (destination.endsWith("市")) {
                  //  destination = destination.substring(0, destination.length() - 1);
                    destination = destination;
                }
                String planeKey = FeiShuTripType.PLANE.getKey();
                String trainKey = FeiShuTripType.TRAIN.getKey();
                String hotelKey = FeiShuTripType.OTHER.getKey();
                //国际机票暂时不考虑
                int tripType = 0;
                if (transport.equals(planeKey)) {
                    tripType = 7;
                } else if (transport.equals(trainKey)) {
                    tripType = 15;
                } else if (transport.equals(hotelKey)) {
                    tripType = 11;
                }else {
                    //如果是汽车票或者其他过滤
                    continue;
                }
                //单程往返
                int rountTrip = 1;
                rountTrip = oneRound.equals("往返") ? 2 : 1;

                CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
                commonApplyTrip.setType(tripType);
                commonApplyTrip.setTripType(rountTrip);
                commonApplyTrip.setEstimatedAmount(0);
                if (11 == tripType) {//酒店，城市全部取目的城市
                  //  commonApplyTrip.setStartCityName(destination);
                    commonApplyTrip.setStartCityId(destination);
                } else {
                  //  commonApplyTrip.setStartCityName(departure);
                    commonApplyTrip.setStartCityId(departure);
                }
               // commonApplyTrip.setArrivalCityName(destination);
                commonApplyTrip.setArrivalCityId(destination);
                commonApplyTrip.setStartTime(start);
                commonApplyTrip.setEndTime(end);
                commonApplyTrip.setEstimatedAmount(0);
                commonApplyTrip.setCityRelationType(CityRelationType.FEISHU.getCode());
                tripList.add(commonApplyTrip);
            }

            CommonApply commonApply = new CommonApply();
            commonApply.setApplyReason(reason);
           // commonApply.setApplyReasonDesc(reason);
            //commonApply.setThirdRemark(reason);
            commonApply.setThirdId(approvalId);
            commonApply.setType(1);
            commonApply.setFlowType(4);
            commonApply.setBudget(0);
            commonApplyReqDTO.setApply(commonApply);
            //获取同行人信息
            List<CommonApplyGuest> guests = new ArrayList<>();
            for (String peer : peers) {
                //调用飞书查询人员数据
                FeiShuUserInfoDTO userInfo = feiShuEiaEmployeeService.getUserInfo(FeiShuConstant.ID_TYPE_EMPLOYEE_ID, peer, corpId);
                if (!ObjectUtils.isEmpty(userInfo)) {//调用飞书查询查询用户详情
                    String name = userInfo.getName();
                    String mobile = userInfo.getMobile();
                    CommonApplyGuest guest = new CommonApplyGuest();
                    guest.setId(peer);
                    guest.setName(name);
                    guest.setPhoneNum(mobile);
                    guest.setIsEmployee(true);
                    guests.add(guest);
                }
            }
            commonApplyReqDTO.setTripList(tripList);
            commonApplyReqDTO.setGuestList(guests);
        }

        return commonApplyReqDTO;
    }

    /**
     * 用车审批模板解析
     */
    @Override
    public CommonApplyReqDTO parseFeiShuCarForm(String companyId, String corpId, String approvalId, String form, String thirdEmployeeId) {
        List<FeiShuApprovalFormCommonDTO> formList = JsonUtils.toObj(form, new TypeReference<List<FeiShuApprovalFormCommonDTO>>() {
        });
        if (ObjectUtils.isEmpty(formList)) {
            log.info("表单解析对象失败 companyId:{},dataId:{},jsonForm:{}", companyId, approvalId, form);
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_APPROVAL_FORM_PARSE_ERROR, "飞书申请单解析失败");
        }
        log.info("表单解析后数据 companyId:{},dataId:{},formList:{}",companyId,approvalId,JsonUtils.toJson(formList));
        Map<String, FeiShuApprovalFormCommonDTO> formMap = formList.stream().collect(Collectors.toMap(FeiShuApprovalFormCommonDTO::getName, Function.identity()));
        FeiShuCarFormParseDTO formParseDTO;
        // 有是否用车标识则用外出控件组解析表单
        if (formMap.containsKey(FeiShuConstant.APPROVAL_FORM_CAR_USE_FLAG)) {
            formParseDTO = buildParseFormDTOByOutGroup(formMap);
        } else {
            formParseDTO = buildParseFormDTO(formMap);
        }
        log.info("飞书用车申请单解析后数据 formParseDTO:{}",JsonUtils.toJson(formParseDTO));
        return buildApplyReqDTO(formParseDTO, approvalId, companyId, thirdEmployeeId);
    }

    private CommonApplyReqDTO buildApplyReqDTO(FeiShuCarFormParseDTO formParseDTO, String approvalId, String companyId, String thirdEmployeeId) {
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        //构建公用审批数据
        CommonApply commonApply = new CommonApply();
        commonApply.setApplyReason(formParseDTO.getApplyReason());
        commonApply.setThirdId(approvalId);
        commonApply.setType(12);
        commonApply.setFlowType(4);
        commonApplyReqDTO.setApply(commonApply);
        CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
        commonApplyTrip.setType(3);
        commonApplyTrip.setStartCityName(formParseDTO.getApplyCarCity());
        commonApplyTrip.setArrivalCityName(formParseDTO.getApplyCarCity());
        commonApplyTrip.setStartTime(formParseDTO.getApplyBeginDate());
        commonApplyTrip.setEndTime(formParseDTO.getApplyEndDate());
        List<CommonApplyTrip> commonApplyTripList = new ArrayList<>();
        commonApplyTripList.add(commonApplyTrip);
        commonApplyReqDTO.setTripList(commonApplyTripList);
        Map<String, Object> param = new HashMap<>();
        param.put("userId", commonService.getEmployeeId(companyId, thirdEmployeeId));
        param.put("companyId", companyId);
        Map<String, Object> queryDetail = openApplyService.getQueryDetail(param);
        commonApplyReqDTO.setApplyTaxiRuleInfo(FeishuTaxiRuleUtils.buildUseCarRuleList(formParseDTO.getCarUseCount(), formParseDTO.getCarAmount(), queryDetail));
        return commonApplyReqDTO;
    }

    /**
     * 解析用车申请单
     *
     * @param formMap 控件组map
     * @return 解析后的数据实体
     */
    private FeiShuCarFormParseDTO buildParseFormDTO(Map<String, FeiShuApprovalFormCommonDTO> formMap) {
        FeiShuCarFormParseDTO formParseDTO = new FeiShuCarFormParseDTO();
        checkForm(formMap);
        //申请事由
        formParseDTO.setApplyReason(StringUtils.obj2str(formMap.get(FeiShuConstant.APPROVAL_FORM_REASON).getValue()));
        //日期区间
        FeiShuApprovalFormCommonDTO.DateIntervalValue dateIntervalValue =
            (FeiShuApprovalFormCommonDTO.DateIntervalValue) formMap.get(FeiShuConstant.APPROVAL_FORM_DATE).getValue();
        //开始时间
        formParseDTO.setApplyBeginDate(StringUtils.isBlank(dateIntervalValue.getStart()) ? null : dateIntervalValue.getStart().substring(0, dateIntervalValue.getStart().indexOf("T")));
        //结束时间
        formParseDTO.setApplyEndDate(StringUtils.isBlank(dateIntervalValue.getEnd()) ? null : dateIntervalValue.getEnd().substring(0, dateIntervalValue.getEnd().indexOf("T")));
        //用车城市
        formParseDTO.setApplyCarCity(buildCity(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_CITY)));
        //用车次数
        formParseDTO.setCarUseCount(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_USE_COUNT) == null ? null : StringUtils.obj2str(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_USE_COUNT).getValue()));
        //用车费用
        formParseDTO.setCarAmount(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_AMOUNT) == null ? null : StringUtils.obj2str(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_AMOUNT).getValue()));
        return formParseDTO;
    }
    /**
     * 校验用车申请单组件是否存在
     * @param formMap 组件映射
     */
    private void checkForm(Map<String, FeiShuApprovalFormCommonDTO> formMap) {
        if (ObjectUtils.isEmpty(formMap.get(FeiShuConstant.APPROVAL_FORM_DATE))){
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_CAR_FORM_DATE_INTERVAL_IS_NULL,"飞书用车申请单未配置控件：日期区间(DateInterval)");
        }
        if (ObjectUtils.isEmpty(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_CITY))){
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_CAR_FORM_CITY_IS_NULL,"飞书用车申请单未配置控件：用车城市");
        }
    }

    /**
     * 解析城市，支持明细控件和单行文本
     *
     * @param feiShuApprovalFormCommonDTO 组件实体
     * @return 城市列表，多个城市用"," 隔开
     */
    private String buildCity(FeiShuApprovalFormCommonDTO feiShuApprovalFormCommonDTO) {
        if (ObjectUtils.isEmpty(feiShuApprovalFormCommonDTO)) {
            return null;
        }
        if (FeiShuConstant.APPROVAL_FORM_TYPE_INPUT.equals(feiShuApprovalFormCommonDTO.getType())) {
            return StringUtils.obj2str(feiShuApprovalFormCommonDTO.getValue());
        }
        if (FeiShuConstant.APPROVAL_FORM_TYPE_FIELD_LIST.equals(feiShuApprovalFormCommonDTO.getType())) {
            List<List<FeiShuApprovalFormCommonDTO>> fieldListValue = (List<List<FeiShuApprovalFormCommonDTO>>) feiShuApprovalFormCommonDTO.getValue();
            return fieldListValue.stream().map(formList -> formList.get(0)).map(form -> {
                String fullCityName = (String) form.getValue();
                String[] fullCityNameArr = fullCityName.replace("\"", "").split("/");
                if (fullCityNameArr.length <= 2) {
                    return null;
                }
                return fullCityNameArr[2];
            }).filter(Objects::nonNull).distinct().limit(10).collect(Collectors.joining(","));
        }
        return null;
    }

    /**
     * 解析使用外出控件组的用车申请单
     *
     * @param formMap 控件组map
     * @return 解析后的数据实体
     */
    private FeiShuCarFormParseDTO buildParseFormDTOByOutGroup(Map<String, FeiShuApprovalFormCommonDTO> formMap) {
        boolean useCarFlag = formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_USE_FLAG) != null &&
            FeiShuConstant.APPROVAL_FORM_VALUE_USE_CAR.equals(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_USE_FLAG).getValue());
        if (!useCarFlag) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_APPROVAL_FORM_SKIP, "用了外出控件的飞书用车申请单，未勾选用车，不创建分贝通用车申请单");
        }
        //校验控件是否存在
        checkFormByOutGroup(formMap);
        FeiShuCarFormParseDTO formParseDTO = new FeiShuCarFormParseDTO();
        FeiShuApprovalFormCommonDTO.OutGroupValue outGroupValue =
            (FeiShuApprovalFormCommonDTO.OutGroupValue) formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_OUT_GROUP).getValue();
        //申请事由
        formParseDTO.setApplyReason(outGroupValue.getReason());
        //开始时间
        formParseDTO.setApplyBeginDate(StringUtils.isBlank(outGroupValue.getStart()) ? null : outGroupValue.getStart().substring(0, outGroupValue.getStart().indexOf("T")));
        //结束时间
        formParseDTO.setApplyEndDate(getEndDay(outGroupValue));
        //用车城市
        formParseDTO.setApplyCarCity(buildCity(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_CITY)));
        //用车次数
        formParseDTO.setCarUseCount(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_USE_COUNT) == null ? null : StringUtils.obj2str(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_USE_COUNT).getValue()));
        //用车费用
        formParseDTO.setCarAmount(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_AMOUNT) == null ? null : StringUtils.obj2str(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_AMOUNT).getValue()));
        return formParseDTO;
    }

    /**
     * 得到外出控件组的结束时间
     * 如果时间单位是hour，直接取当天
     * 如果时间单位是day，且时间是00:00:00则取前一天
     *
     * @param outGroupValue 外出控件
     * @return 结束日期
     */
    private String getEndDay(FeiShuApprovalFormCommonDTO.OutGroupValue outGroupValue) {
        String endDateStr = outGroupValue.getEnd();
        if (StringUtils.isBlank(endDateStr)){
            return null;
        }
        if (FeiShuConstant.APPROVAL_FORM_CAR_OUT_GROUP_TIME_UNIT_DAY.equals(outGroupValue.getUnit())) {
            Date srcDate = DateUtils.toDate(endDateStr.replace("T", " ").substring(0, endDateStr.indexOf("+")));
            Date srcBeginDate = DateUtils.toDate(DateUtils.toSimpleStr(srcDate, true));
            if (DateUtils.dateCompare(srcDate, srcBeginDate, 0) == 0) {
                return DateUtils.toSimpleStr(DateUtils.addDay(srcDate, -1), true);
            }
        }
        return endDateStr.substring(0, endDateStr.indexOf("T"));
    }

    /**
     * 校验用车申请单(含外出控件组)组件是否存在
     * @param formMap 组件映射
     */
    private void checkFormByOutGroup(Map<String, FeiShuApprovalFormCommonDTO> formMap) {
        if (ObjectUtils.isEmpty(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_OUT_GROUP))){
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_CAR_FORM_OUT_GROUP_IS_NULL,"飞书用车申请单未配置控件：外出控件组");
        }
        if (ObjectUtils.isEmpty(formMap.get(FeiShuConstant.APPROVAL_FORM_CAR_CITY))){
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_CAR_FORM_CITY_IS_NULL,"飞书用车申请单未配置控件：用车城市");
        }
    }
}

