package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdatamedium;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkBizDataDto;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.DingtalkTripType;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.constant.TripApproveType;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyGuest;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyTrip;
import com.fenbeitong.openapi.plugin.support.citycode.dto.CityBaseInfo;
import com.fenbeitong.openapi.plugin.support.citycode.service.CityCodeService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fenbeitong.openapi.plugin.dingtalk.isv.constant.DingtalkTripConstant.*;

@Component
public class DingtalkFormDataTransferDataHandler {

    private static final Map<String,IBuildTripValue> tripServiceMap = new HashMap<>();

    @Autowired
    CityCodeService cityCodeService;

    public DingtalkFormDataTransferDataHandler() {
        tripServiceMap.put(VEHICLE, new TripTypeBuilder());
        tripServiceMap.put(SINGLE_OR_RETURN, new SingleOrReturnBuilder());
        tripServiceMap.put(DEPARTURE, new DepartureBuilder());
        tripServiceMap.put(ARRIVAL, new ArrivalBuilder());
        tripServiceMap.put(START_TIME, new StartTimeBuilder());
        tripServiceMap.put(END_TIME, new EndTimeBuilder());
    }

    public CommonApplyReqDTO convertDingtalkFormData(DingtalkBizDataDto dingtalkBizDataDto,String companyId){
        if ( null == dingtalkBizDataDto ){
            return null;
        }
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        List<DingtalkBizDataDto.FormValueVO> formValueVOList = dingtalkBizDataDto.getFormValueVOS();
        //获取原因
        List<DingtalkBizDataDto.FormValueVO> reasonFormValueVOList = formValueVOList.stream().filter(e->REASON.equals(e.getBizAlias())).collect(Collectors.toList());
        //构建applyInfo
        commonApplyReqDTO.setApply(buildCommonApplyInfo(dingtalkBizDataDto,reasonFormValueVOList,companyId));
        //获取行程表格列内容
        List<DingtalkBizDataDto.FormValueVO> tableReasonFormValueVOList = formValueVOList.stream().filter(e->ITINERARY.equals(e.getBizAlias())).collect(Collectors.toList());
        commonApplyReqDTO.setTripList(buildTripList(tableReasonFormValueVOList));
        //构建同行人信息
        List<DingtalkBizDataDto.FormValueVO> partnerFormValueVOList = formValueVOList.stream().filter(e->PARTNER.equals(e.getBizAlias())).collect(Collectors.toList());
        commonApplyReqDTO.setGuestList(buildGuestInfo(partnerFormValueVOList));
        return commonApplyReqDTO;
    }

    public CommonApply buildCommonApplyInfo(DingtalkBizDataDto bizDataDto,List<DingtalkBizDataDto.FormValueVO> reasonFormValueVOList,String companyId){
        if ( CollectionUtils.isEmpty(reasonFormValueVOList) ){
            return null;
        }
        CommonApply commonApply = new CommonApply();
        String reason = reasonFormValueVOList.get(0).getValue();
        commonApply.setApplyReason(reason);
        commonApply.setApplyReasonDesc(reason);
        commonApply.setThirdRemark(bizDataDto.getTitle());
        commonApply.setThirdId(bizDataDto.getProcessInstanceId());
        commonApply.setType(SaasApplyType.ChaiLv.getValue());
        commonApply.setFlowType(4);
        commonApply.setBudget(0);
        commonApply.setCompanyId(companyId);
        return commonApply;
    }

    public List<CommonApplyTrip> buildTripList(List<DingtalkBizDataDto.FormValueVO> tableReasonFormValueVOList){
        if (CollectionUtils.isEmpty(tableReasonFormValueVOList)){
            return null;
        }
        DingtalkBizDataDto.FormValueVO formValueVO = tableReasonFormValueVOList.get(0);
        if ( null == formValueVO ){
            return null;
        }
        List<DingtalkBizDataDto.FormDetail> formDetails = formValueVO.getDetails();
        if ( CollectionUtils.isEmpty(formDetails) ){
            return null;
        }
        List<CommonApplyTrip> tripList = new ArrayList();
        //构建行程单信息
        formDetails.stream().forEach(formDetail -> {
            //遍历行程单
            List<DingtalkBizDataDto.Detail> detailList = formDetail.getDetails();
            if ( CollectionUtils.isEmpty(detailList) ){
                return;
            }
            CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
            commonApplyTrip.setEstimatedAmount(0);
            //遍历每一项
            detailList.stream().forEach(detail -> {
                if ( null == detail ){
                    return;
                }
                String bizType = detail.getBizAlias();
                //如果行程是汽车,则该条数据不处理
                if ( VEHICLE.equals(bizType) && CAR.equals(detail.getValue())){
                    return;
                }
                IBuildTripValue buildTripService = getTripServiceInstance(bizType);
                if ( null == buildTripService ){
                    return;
                }
                //给每一项赋值
                buildTripService.buildValue(detail.getValue(),commonApplyTrip);
            });
            if ( !StringUtils.isEmpty(commonApplyTrip.getType()) ){
                tripList.add(commonApplyTrip);
            }
        });
        return tripList;
    }

    public IBuildTripValue getTripServiceInstance(String bizAlia){
        return tripServiceMap.get(bizAlia);
    }

    public interface IBuildTripValue{
        void buildValue(String value,CommonApplyTrip commonApplyTrip);
    }

    public class TripTypeBuilder implements IBuildTripValue{

        @Override
        public void buildValue(String value,CommonApplyTrip commonApplyTrip) {
            String planeKey = DingtalkTripType.PLANE.getKey();
            String trainKey = DingtalkTripType.TRAIN.getKey();
            String otherKey = DingtalkTripType.OTHER.getKey();
            int tripType = 0;
            if (value.equals(planeKey)) {
                tripType = TripApproveType.National_Flight.getValue();
            } else if (value.equals(trainKey)) {
                tripType = TripApproveType.Train.getValue();
            } else if (value.contains(otherKey)) {
                tripType = TripApproveType.Hotel.getValue();
            }
            commonApplyTrip.setType(tripType);
        }
    }

    public class SingleOrReturnBuilder implements IBuildTripValue{

        @Override
        public void buildValue( String value, CommonApplyTrip commonApplyTrip) {
            //单程往返
            int rountTrip = 1;
            rountTrip = value.equals("往返") ? 2 : 1;
            commonApplyTrip.setTripType(rountTrip);
        }
    }

    //出发地
    public class DepartureBuilder implements IBuildTripValue{

        @Override
        public void buildValue(String value, CommonApplyTrip commonApplyTrip) {
            commonApplyTrip.setStartCityName(value);
            CityBaseInfo city = cityCodeService.getIdByName(value);
            if ( null != city ){
                commonApplyTrip.setStartCityId(city.getId());
            }
        }
    }

    //目的地
    public class ArrivalBuilder implements IBuildTripValue{

        @Override
        public void buildValue(String value, CommonApplyTrip commonApplyTrip) {
            commonApplyTrip.setArrivalCityName(value);
            CityBaseInfo city = cityCodeService.getIdByName(value);
            if ( null != city ){
                commonApplyTrip.setArrivalCityId(city.getId());
            }
        }
    }

    public class StartTimeBuilder implements IBuildTripValue{

        @Override
        public void buildValue(String value, CommonApplyTrip commonApplyTrip) {
            //钉钉的开始时间格式是：2021-04-14 上午
            if (StringUtils.isEmpty(value)){
                return;
            }
            String[] time = value.split(" ");
            commonApplyTrip.setStartTime(time.length == 2 ? time[0] : "");
        }
    }

    public class EndTimeBuilder implements IBuildTripValue{

        @Override
        public void buildValue(String value, CommonApplyTrip commonApplyTrip) {
            //钉钉的结束时间格式是：2021-04-14 上午
            if (StringUtils.isEmpty(value)){
                return;
            }
            String[] time = value.split(" ");
            commonApplyTrip.setEndTime(time.length == 2 ? time[0] : "");
        }
    }

    public List<CommonApplyGuest> buildGuestInfo(List<DingtalkBizDataDto.FormValueVO> partnerFormValueVOList){
        if ( CollectionUtils.isEmpty(partnerFormValueVOList) ){
            return null;
        }
        DingtalkBizDataDto.FormValueVO partnerInfo = partnerFormValueVOList.get(0);
        List<CommonApplyGuest> guests = new ArrayList<>();
        if ( null == partnerInfo ){
            return null;
        }
        String employeeInfo = partnerInfo.getExtValue();
        List<EmpInfo> empInfoList = JsonUtils.toObj(employeeInfo, new TypeReference<List<EmpInfo>>() {
        });
        if ( CollectionUtils.isEmpty(empInfoList) ){
            return null;
        }
        for (EmpInfo empInfo : empInfoList) {
            CommonApplyGuest guest = buildApplyGuest(empInfo);
            guests.add(guest);
        }
        return guests;
    }

    private CommonApplyGuest buildApplyGuest(EmpInfo empInfo){
        CommonApplyGuest guest = new CommonApplyGuest();
        guest.setId(empInfo.getEmplId());
        guest.setName(empInfo.getName());
        guest.setIsEmployee(true);
        // 外部员工填1
        guest.setEmployeeType(1);
        return guest;
    }

    @Data
    public static class EmpInfo{

        private String emplId;

        private String name;

        private String avatar;

    }

}
