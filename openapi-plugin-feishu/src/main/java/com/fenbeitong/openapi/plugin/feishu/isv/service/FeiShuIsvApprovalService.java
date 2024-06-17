package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuTripType;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeishuFormFieldTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.util.FeiShuParseTimeUtils;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuUserInfoDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuApprovalService;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuEmployeeService;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.common.util.FeishuTaxiRuleUtils;
import com.fenbeitong.openapi.plugin.feishu.isv.util.FeiShuIsvHttpUtils;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyGuest;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyTrip;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenApplyService;
import com.fenbeitong.openapi.plugin.support.city.constants.CityRelationType;
import com.fenbeitong.openapi.plugin.support.common.constant.OrderCategoryEnum;
import com.fenbeitong.openapi.plugin.support.common.service.CommonService;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author lizhen
 */
@ServiceAspect
@Service
public class FeiShuIsvApprovalService extends AbstractFeiShuApprovalService {
    @Autowired
    private FeiShuIsvHttpUtils feiShuIsvHttpUtils;

    @Autowired
    private FeiShuIsvEmployeeService feiShuIsvEmployeeService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private IOpenApplyService openApplyService;

    @Override
    protected AbstractFeiShuHttpUtils getFeiShuHttpUtils() {
        return feiShuIsvHttpUtils;
    }

    @Override
    protected AbstractFeiShuEmployeeService getFeiShuEmployeeService() {
        return feiShuIsvEmployeeService;
    }


    /**
     * 飞书审批使用自带出差套件
     *
     * @param corpId
     * @param approvalId
     * @return
     */
    public CommonApplyReqDTO parseFeiShuTripApprovalForm(String companyId, String corpId, String approvalId, FeiShuApprovalFormDTO.Value feiShuApprovalFormDTO) {
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        List<CommonApplyTrip> tripList = new ArrayList();
        if (ObjectUtils.isEmpty(feiShuApprovalFormDTO)) {
            return null;
        }
        //申请事由
        String reason = feiShuApprovalFormDTO.getReason();
        List<FeiShuApprovalFormDTO.Schedule> schedules = feiShuApprovalFormDTO.getSchedule();
        if (ObjectUtils.isEmpty(schedules)) {
            return null;
        }
        //同行人信息
        List<String> peers = feiShuApprovalFormDTO.getPeerOpenIds();
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
                departure = departure.substring(0, departure.length());
            }
            if (destination.endsWith("市")) {
                destination = destination.substring(0, destination.length());
            }
            String planeKey = FeiShuTripType.PLANE.getKey();
            String trainKey = FeiShuTripType.TRAIN.getKey();
            String hotelKey = FeiShuTripType.OTHER.getKey();
            //汽车票
            String carKey = FeiShuTripType.CAR.getKey();
            //国际机票暂时不考虑
            int tripType = 0;
            if (transport.equals(planeKey)) {
                tripType = OrderCategoryEnum.Air.getKey();
            } else if (transport.equals(trainKey)) {
                tripType = OrderCategoryEnum.Train.getKey();
            } else if (transport.equals(hotelKey)) {
                tripType = OrderCategoryEnum.Hotel.getKey();
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
            if (OrderCategoryEnum.Hotel.getKey() == tripType) {//酒店，城市全部取目的城市
                // commonApplyTrip.setStartCityName(destination);
                commonApplyTrip.setStartCityId(destination);
            } else {
                // commonApplyTrip.setStartCityName(departure);
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
        //commonApply.setApplyReasonDesc(reason);
        //commonApply.setThirdRemark(reason);
        commonApply.setThirdId(approvalId);
        commonApply.setType(SaasApplyType.ChaiLv.getValue());
        commonApply.setFlowType(4);
        commonApply.setBudget(0);
        commonApply.setCompanyId(companyId);
        commonApplyReqDTO.setApply(commonApply);
        //获取同行人信息
        List<CommonApplyGuest> guests = new ArrayList<>();
        for (String peer : peers) {
            //调用飞书查询人员数据
            FeiShuUserInfoDTO userInfo = feiShuIsvEmployeeService.getUserInfo(FeiShuConstant.ID_TYPE_EMPLOYEE_ID, peer, corpId);
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
        return commonApplyReqDTO;
    }


    /**
     * 飞书用车审批
     *
     * @param approvalId 三方申请单id
     * @param form 申请单表单数据
     * @return
     */
    public CommonApplyReqDTO parseFeiShuCarApprovalForm(String approvalId, List<Map> form , String companyId , String thirdEmployeeId) {
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        String applyReason = "";
        String applyBeginDate = "";
        String applyEndDate = "";
        String applyCarCity = "";
        String carUseCount = "";
        String carAmount = "";
        for (int i = 0; i < form.size(); i++) {
            Map formMap = form.get(i);
            Object formName = formMap.get("name");
            Object value = formMap.get("value");
            if (FeiShuConstant.APPROVAL_FORM_REASON.equals(formName)) {
                //申请事由
                applyReason =  StringUtils.obj2str( value ) ;
            } else if (FeiShuConstant.APPROVAL_FORM_CAR_CITY.equals(formName)) {
                //用车城市
                applyCarCity = getCarCity(formMap);
            } else if (FeiShuConstant.APPROVAL_FORM_DATE.equals(formName)) {
                //行程日期
                Map dateMap = (Map) value;
                applyBeginDate =  StringUtils.obj2str( dateMap.get("start") );
                applyEndDate =  StringUtils.obj2str( dateMap.get("end") );
            } else if (FeiShuConstant.APPROVAL_FORM_CAR_USE_COUNT.equals(formName)) {
                carUseCount =  StringUtils.obj2str( value );
            } else if (FeiShuConstant.APPROVAL_FORM_CAR_AMOUNT.equals(formName)) {
                carAmount = StringUtils.obj2str( value );
            }
        }
        //构建公用审批数据
        CommonApply commonApply = new CommonApply();
        commonApply.setApplyReason(applyReason);
        //commonApply.setThirdRemark(applyReason);
        //commonApply.setApplyReasonDesc(applyReason);
        commonApply.setThirdId(approvalId);
        commonApply.setType(SaasApplyType.ApplyTaxi.getValue());
        commonApply.setFlowType(4);
        commonApplyReqDTO.setApply(commonApply);
        CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
        commonApplyTrip.setType(OrderCategoryEnum.Taxi.getKey());
        commonApplyTrip.setStartCityName(applyCarCity);
        commonApplyTrip.setArrivalCityName(applyCarCity);
        //设置日期格式
        applyBeginDate = applyBeginDate.substring(0, applyBeginDate.indexOf("T"));
        applyEndDate = applyEndDate.substring(0, applyEndDate.indexOf("T"));
        commonApplyTrip.setStartTime(applyBeginDate);
        commonApplyTrip.setEndTime(applyEndDate);
        List<CommonApplyTrip> commonApplyTripList = new ArrayList<>();
        commonApplyTripList.add(commonApplyTrip);
        commonApplyReqDTO.setTripList(commonApplyTripList);

        Map<String,Object> param = new HashMap<>();
        param.put("userId" , commonService.getEmployeeId( companyId,  thirdEmployeeId ));
        param.put("companyId" , companyId);
        Map<String, Object> queryDetail = openApplyService.getQueryDetail(param);
        commonApplyReqDTO.setApplyTaxiRuleInfo(FeishuTaxiRuleUtils.buildUseCarRuleList(carUseCount , carAmount , queryDetail));
        return commonApplyReqDTO;
    }

    /**
     * 获取申请单城市，兼容飞书input 和 fieldList 字段类型
     * @param formMap 用车城市控件
     * @return
     */
    @SuppressWarnings("unchecked")
    private static String getCarCity(Map<String, Object> formMap) {
        final ApplyCity applyCity = new ApplyCity();
        if (MapUtils.isBlank(formMap)) {
            return applyCity.getApplyCity();
        }
        Object cityFieldValue = formMap.get("value");
        if (FeishuFormFieldTypeConstant.INPUT.equals(formMap.get("type"))) {
            applyCity.applyCity = Optional.of(cityFieldValue).map(Object::toString).orElse(applyCity.getApplyCity());
        } else if (FeishuFormFieldTypeConstant.FIELD_LIST.equals(formMap.get("type"))) {
            Optional.ofNullable((List<List<HashMap>>) cityFieldValue).ifPresent(cityList ->
                cityList.subList(0, cityList.size() <= 10 ? cityList.size() : 10)
                    .forEach(city -> city.stream()
                        .findFirst()
                        .map(cityValue -> (String) cityValue.get("value"))
                        .map(value -> value.split("/"))
                        .filter(cityArray -> cityArray.length > 2)
                        .ifPresent(cityArray -> {
                            if (StringUtils.isBlank(applyCity.getApplyCity())) {
                                applyCity.applyCity = applyCity.getApplyCity() + cityArray[2].replace("\"", "");
                            } else {
                                applyCity.applyCity = applyCity.getApplyCity() + "," + cityArray[2].replace("\"", "");
                            }
                        })
                    ));
        }
        return applyCity.getApplyCity();
    }
    @Data
    private static class ApplyCity {
        private String  applyCity = "";
    }
}
