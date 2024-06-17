package com.fenbeitong.openapi.plugin.yunzhijia.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessType;
import com.fenbeitong.openapi.plugin.support.apply.constant.TripApproveType;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveDetail;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractApplyService;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiResponseDTO;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.company.service.impl.CommonPluginCorpAppDefinitionService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyEventDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaEmployeeDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.notice.sender.YunzhijiaNoticeSender;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.uc.YunzhijiaFbEmployeeService;
import com.fenbeitong.openapi.sdk.dto.air.AirPortCityDTO;
import com.fenbeitong.openapi.sdk.dto.approve.*;
import com.fenbeitong.openapi.sdk.dto.city.CityByNameRespDTO;
import com.fenbeitong.openapi.sdk.dto.city.CityIntlAirRespDTO;
import com.fenbeitong.openapi.sdk.dto.common.OpenApiRespDTO;
import com.fenbeitong.openapi.sdk.webservice.approve.FbtApproveService;
import com.fenbeitong.openapi.sdk.webservice.common.FbtCommonService;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class YunzhijiaTripProcessApply extends AbstractApplyService implements IYunzhijiaProcessApply {
    @Autowired
    CommonPluginCorpAppDefinitionService commonPluginCorpAppDefinitionService;
    @Autowired
    YunzhijiaNoticeSender yunzhijiaNoticeSender;
    @Autowired
    FbtApproveService fbtApproveService;
    @Autowired
    CommonAuthService commonAuthService;
    @Autowired
    YunzhijiaFbEmployeeService yunzhijiaFbEmployeeService;
    @Autowired
    FbtCommonService fbtCommonService;
    @Value("${host.saas}")
    private String saasHost;

    @Override
    public TaskResult processApply(Task task, ThirdApplyDefinition thirdApplyDefinition, PluginCorpDefinition pluginCorp, YunzhijiaApplyEventDTO.YunzhijiaApplyData yunzhijiaApplyData) {
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //basicInfo信息
        String employeeId = yunzhijiaApplyData.getBasicInfo().getYunzhijiaEmployeeDTO().getOid();
        //detailMap数据
        YunzhijiaApplyEventDTO.YunzhijiaApplyDetailInfoDTO detailInfo = yunzhijiaApplyData.getDetailInfo();
        YunzhijiaApplyEventDTO.WidgetMapDTO widgetMap = detailInfo.getWidgetMap();
        //同行人
        YunzhijiaApplyEventDTO.WidgetDTO guestInfo = widgetMap.getContracts();
        //具体同行人ID集合
        List<String> guestValueList = (List) guestInfo.getValue();
        List<YunzhijiaEmployeeDTO> guestInfoEmployeeDTOS = guestInfo.getEmployeeDTOS();
        //联系人姓名人员ID集合
        List<Map<String, String>> guestNameIdList = guestInfoEmployeeDTOS.stream().map(e -> {
            Map<String, String> nameIdMap = Maps.newHashMap();
            nameIdMap.put(e.getOid(), e.getName());
            return nameIdMap;
        }).collect(Collectors.toList());

        YunzhijiaApplyEventDTO.DetailMapDTO detailMapDTO = detailInfo.getDetailMapDTO();
        YunzhijiaApplyEventDTO.DetailDTO detailDTO = detailMapDTO.getDetailDTO();
        //审批单标题
        String title = detailDTO.getTitle();
        //widgetValue数据
        YunzhijiaApplyEventDTO.WidgetVoMapDTO widgetVos = detailDTO.getWidgetVos();
        //取出出发，目的城市，出发和目的城市三字码
        //根据类型匹配出真正的出发城市，目的城市
        YunzhijiaApplyEventDTO.WidgetDTO virtualStartCity = widgetVos.getStartCity();
        //实际获取的字段名称，是否为出发城市，需要根据title来进行判断，
        // 如果为目的城市，则需要进行其他映射关系进行处理
        String virtualStartCityTitle = virtualStartCity.getTitle();

        YunzhijiaApplyEventDTO.WidgetDTO virtualEndCity = widgetVos.getEndCity();
        YunzhijiaApplyEventDTO.WidgetDTO virtualStartCityCodeC = widgetVos.getStartCityCodeC();
        YunzhijiaApplyEventDTO.WidgetDTO virtualEndCityCodeC = widgetVos.getEndCityCodeC();
        //TODO,场景类型和单程往返也需要获取真实字段类型
        YunzhijiaApplyEventDTO.WidgetDTO tripType = widgetVos.getTripType();
        //场景类型数据
        List<YunzhijiaApplyEventDTO.Option> options = tripType.getOptions();
        //场景类型map
        HashMap<String, String> tripTypeMap = Maps.newHashMap();
        options.stream().forEach(e -> tripTypeMap.put(e.getKey(), e.getValue()));
        //单程往返标识
        YunzhijiaApplyEventDTO.WidgetDTO single = widgetVos.getSingle();
        //虚拟单程往返标识，有可能不是真正的单程往返字段数据
        String virtualSingleTitle = single.getTitle();
        List<YunzhijiaApplyEventDTO.Option> singleOptions = single.getOptions();
        HashMap<String, String> singleOptionMap = Maps.newHashMap();
        singleOptions.stream().forEach(s -> {
            singleOptionMap.put(s.getKey(), s.getValue());
        });
        //widgetValue数据
        List<YunzhijiaApplyEventDTO.WidgetValueDTO> widgetValue = detailDTO.getWidgetValue();
        TripApproveApply build = TripApproveApply.builder()
                .thirdId(dataId)
                .type(ProcessType.Trip.getKey())
                .budget(0)
                .flowType(4)
                .thirdRemark(title)
                .applyReasonDesc(title)
                .build();
        List<TripApproveDetail> tripApproveDetails = Lists.newArrayList();
        String token = yunzhijiaFbEmployeeService.getCreateTripApproveToken(pluginCorp.getAppId(), employeeId);
        List<Guest> guestList = Lists.newArrayList();
        List<Map<String, String>> unFbGuestList = Lists.newArrayList();
        guestValueList.stream().forEach(guestId -> {
            ThirdEmployeeRes fbEmployeeInfo = yunzhijiaFbEmployeeService.getFbEmployeeInfo(pluginCorp.getAppId(), guestId);
            if (ObjectUtils.isEmpty(fbEmployeeInfo)) {//如果同行人为空，则消息通知
                for (Map<String, String> idName : guestNameIdList) {
                    String guestName = idName.get(guestId);
                    //加入到
                    Map<String, String> unFbGuestMap = Maps.newHashMap();
                    unFbGuestMap.put(guestId, guestName);
                    unFbGuestList.add(unFbGuestMap);
                }
            } else {
                Guest build1 = Guest.builder()
                        .isEmployee(true)
                        .id(fbEmployeeInfo.getEmployee().getThirdEmployeeId())
                        .name(fbEmployeeInfo.getEmployee().getName())
                        .phoneNum(fbEmployeeInfo.getEmployee().getPhone_num())
                        .build();
                guestList.add(build1);
            }
        });

        widgetValue.stream().forEach(widgetValueDTO -> {
            log.info("widgetValue size {}", widgetValue.size());
            //构建具体行程数据信息
            //差旅类型具体名称
            int fbTripType = 0;
            String tripTypeName = tripTypeMap.get(widgetValueDTO.getTripType());

            if (tripTypeName.equals("火车")) {
                fbTripType = TripApproveType.Train.getValue();
            } else if (tripTypeName.equals("国内机票")) {
                fbTripType = TripApproveType.National_Flight.getValue();
            } else if (tripTypeName.equals("酒店")) {
                fbTripType = TripApproveType.Hotel.getValue();
            } else if (tripTypeName.equals("国际机票")) {
                fbTripType = TripApproveType.Intl_Flight.getValue();
            }
            //出发城市名称
            String startCityName = widgetValueDTO.getStartCity().trim();
            //目的城市名称
            String endCityName = widgetValueDTO.getEndCity().trim();
            //出发城市三字码
            String startCityCodeC = StringUtils.isNotBlank(widgetValueDTO.getStartCityCodeC())?widgetValueDTO.getStartCityCodeC().trim().toUpperCase():"";
            //目的城市三字码
            String endCityCodeC = StringUtils.isNotBlank(widgetValueDTO.getEndCityCodeC())?widgetValueDTO.getEndCityCodeC().trim().toUpperCase():"";
            List<Long> businessTime = widgetValueDTO.getBusinessTime();
            if (businessTime.size() < 2) {//出发时间和结束时间必须非空
                //TODO 抛异常提示
            }
            Date beginDate = DateUtils.toDate(businessTime.get(0));
            Date endDate = DateUtils.toDate(businessTime.get(1));
            if (fbTripType == TripApproveType.Intl_Flight.getValue()) {//国际机票单独处理
                //国际机票审批单创建，其中因为国际城市存在名称重复的问题，所以需要有国际城市的三字码来进行辅助填写准确城市信息
                //1.如果只填写的城市名称，那就根据城市名称进行分贝城市code查询，如果没有查询到具体信息，则发送消息提示信息错误
                //如果查询到城市信息，而且包含一个以上，说明城市名称存在相同值，则会把相应的国家城市三字码信息以消息通知的形式发送给
                //用户，然后用户会再次填写审批单，这时用户重新填写的审批单中就会带有城市三字码相关信息，如果有城市三字码信息
                //我们会先根据城市名称查询，然后根据三字码来查询，最终匹配出对应的分贝城市code，如果最终都没有查询到，则通知用户
                //
                TripApproveDetail intlFlightTripApproveDetail = TripApproveDetail.builder().build();
                if (!(StringUtils.isNotBlank(startCityCodeC) && StringUtils.isNotBlank(endCityCodeC))) {//出发城市和目的城市三字码为空的情况，则根据城市名称进行查询城市信息
                    Call<OpenApiRespDTO<CityIntlAirRespDTO>> intelAirStartCityCall = fbtCommonService.getIntelAirCityByName(startCityName);
                    Call<OpenApiRespDTO<CityIntlAirRespDTO>> intelAirEndCityCall = fbtCommonService.getIntelAirCityByName(endCityName);
                    try {
                        OpenApiRespDTO<CityIntlAirRespDTO> startBody = intelAirStartCityCall.execute().body();
                        List<CityIntlAirRespDTO.CityIntlAirResult> startCityList = startBody.getData().getCityList();
                        if (ObjectUtils.isEmpty(startCityList)) {
                            if (StringUtils.isNotBlank(startCityCodeC)) {
                                Call<OpenApiRespDTO<List<AirPortCityDTO>>> intlAirCityCode = fbtCommonService.getIntlAirCityCode(token, startCityCodeC);
                                OpenApiRespDTO<List<AirPortCityDTO>> body = intlAirCityCode.execute().body();
                                List<AirPortCityDTO> data = body.getData();
                                if (!ObjectUtils.isEmpty(data)) {//根据城市三字码查询得到城市code
                                    AirPortCityDTO airPortCityDTO = data.get(0);
                                    String startCityCode = airPortCityDTO.getFbAreaInfos().get(0).getFbAreaCode();
                                    intlFlightTripApproveDetail.setStartCityId(startCityCode);
                                }
                            } else {
                                String msg = "通知：您创建的" + DateUtils.toSimpleStr(beginDate, true) + "到" + DateUtils.toSimpleStr(endDate, true) + "的" + startCityName + "城市到" + endCityName + "城市的分贝通差旅审批单，因审批单中的城市不符合分贝通标准创建失败，请您准确填写城市并提交审批";
                                yunzhijiaNoticeSender.sender(corpId, employeeId, msg);
                            }
                        } else {
                            if (startCityList.size() > 1) {//说明城市名称有重名，需要用城市码来进行查询
                                if (StringUtils.isNotBlank(startCityCodeC)) {//判断是否传递了城市三字码，如果传递了三字码，根据三字码再次查询
                                    Call<OpenApiRespDTO<List<AirPortCityDTO>>> intlAirCityCode = fbtCommonService.getIntlAirCityCode(token, startCityCodeC);
                                    OpenApiRespDTO<List<AirPortCityDTO>> body = intlAirCityCode.execute().body();
                                    List<AirPortCityDTO> data = body.getData();
                                    if (!ObjectUtils.isEmpty(data)) {//根据城市三字码查询得到城市code
                                        AirPortCityDTO airPortCityDTO = data.get(0);
                                        String startCityCode = airPortCityDTO.getFbAreaInfos().get(0).getFbAreaCode();
                                        intlFlightTripApproveDetail.setStartCityId(startCityCode);
                                    }
                                } else {
                                    //1.相应城市的三字码以消息的形式通知客户
                                    List<Map<String, String>> mapList = genMsgListStr(startCityList);
                                    String msg = "因国际机票场景填写的城市存在重名，因此需要您根据国际城市三字码来辅助城市的确认：" + JsonUtils.toJson(mapList);
                                    yunzhijiaNoticeSender.sender(corpId, employeeId, msg);
                                }
                            } else {
                                CityIntlAirRespDTO.CityIntlAirResult cityIntlAirResult = startCityList.get(0);
                                String startCityCode = cityIntlAirResult.getId();
                                intlFlightTripApproveDetail.setStartCityId(startCityCode);
                            }
                        }
                    } catch (IOException e) {
                        log.info("国际机票出发城市code获取失败");
                        e.printStackTrace();
                    }
                    try {
                        OpenApiRespDTO<CityIntlAirRespDTO> endBody = intelAirEndCityCall.execute().body();
                        List<CityIntlAirRespDTO.CityIntlAirResult> cityList = endBody.getData().getCityList();
                        if (ObjectUtils.isEmpty(cityList)) {//根据城市名称查询为空，判断是否传递城市三字码，根据城市三字码进行查询
                            //根据国际城市三字码查询
                            //2.如果客户传递了城市三字码，需要根据三字码来查询分贝城市code信息
                            if (StringUtils.isNotBlank(endCityCodeC)) {
                                Call<OpenApiRespDTO<List<AirPortCityDTO>>> intlAirCityCode = fbtCommonService.getIntlAirCityCode(token, endCityCodeC);
                                OpenApiRespDTO<List<AirPortCityDTO>> body = intlAirCityCode.execute().body();
                                List<AirPortCityDTO> data = body.getData();
                                if (!ObjectUtils.isEmpty(data)) {//根据城市三字码查询得到城市code
                                    AirPortCityDTO airPortCityDTO = data.get(0);
                                    String arriveCityCode = airPortCityDTO.getFbAreaInfos().get(0).getFbAreaCode();
                                    intlFlightTripApproveDetail.setArrivalCityId(arriveCityCode);
                                }
                            } else {
                                String msg = "通知：您创建的" + DateUtils.toSimpleStr(beginDate, true) + "到" + DateUtils.toSimpleStr(endDate, true) + "的" + startCityName + "城市到" + endCityName + "城市的分贝通差旅审批单，因审批单中的城市不符合分贝通标准创建失败，请您准确填写城市并提交审批";
                                yunzhijiaNoticeSender.sender(corpId, employeeId, msg);
                            }
                        } else {
                            if (cityList.size() > 1) {//说明城市名称有重名，需要用城市码来进行查询
                                if (StringUtils.isNotBlank(endCityCodeC)) {//判断是否传递了城市三字码，如果传递了三字码，根据三字码再次查询
                                    Call<OpenApiRespDTO<List<AirPortCityDTO>>> intlAirCityCode = fbtCommonService.getIntlAirCityCode(token, endCityCodeC);
                                    OpenApiRespDTO<List<AirPortCityDTO>> body = intlAirCityCode.execute().body();
                                    List<AirPortCityDTO> data = body.getData();
                                    if (!ObjectUtils.isEmpty(data)) {//根据城市三字码查询得到城市code
                                        AirPortCityDTO airPortCityDTO = data.get(0);
                                        String arriveCityCode = airPortCityDTO.getFbAreaInfos().get(0).getFbAreaCode();
                                        intlFlightTripApproveDetail.setArrivalCityId(arriveCityCode);
                                    }
                                } else {
                                    List<Map<String, String>> mapList = genMsgListStr(cityList);
                                    String msg = "因国际机票场景填写的城市存在重名，因此需要您根据国际城市三字码来辅助城市的确认：" + JsonUtils.toJson(mapList);
                                    yunzhijiaNoticeSender.sender(corpId, employeeId, msg);
                                }
                            } else {
                                CityIntlAirRespDTO.CityIntlAirResult cityIntlAirResult = cityList.get(0);
                                String arriveCityCode = cityIntlAirResult.getId();
                                intlFlightTripApproveDetail.setArrivalCityId(arriveCityCode);
                            }
                        }
                    } catch (Exception e) {
                        log.info("国际机票目的城市code获取失败");
                        e.printStackTrace();
                    }
                } else {//根据城市三字码查询城市名称
                    if (StringUtils.isNotBlank(endCityCodeC)) {
                        Call<OpenApiRespDTO<List<AirPortCityDTO>>> intlAirCityCode = fbtCommonService.getIntlAirCityCode(token, endCityCodeC);
                        OpenApiRespDTO<List<AirPortCityDTO>> body = null;
                        try {
                            body = intlAirCityCode.execute().body();
                        } catch (IOException e) {
                            log.info("根据目的城市三字码查询分贝城市code异常");
                            e.printStackTrace();
                        }
                        List<AirPortCityDTO> data = body.getData();
                        if (!ObjectUtils.isEmpty(data)) {//根据城市三字码查询得到城市code
                            AirPortCityDTO airPortCityDTO = data.get(0);
                            String arriveCityCode = airPortCityDTO.getFbAreaInfos().get(0).getFbAreaCode();
                            intlFlightTripApproveDetail.setArrivalCityId(arriveCityCode);
                        }
                    }

                    if (StringUtils.isNotBlank(startCityCodeC)) {
                        Call<OpenApiRespDTO<List<AirPortCityDTO>>> intlAirCityCode = fbtCommonService.getIntlAirCityCode(token, startCityCodeC);
                        OpenApiRespDTO<List<AirPortCityDTO>> body = null;
                        try {
                            body = intlAirCityCode.execute().body();
                        } catch (IOException e) {
                            log.info("根据出发城市三字码查询分贝城市code异常");
                            e.printStackTrace();
                        }
                        List<AirPortCityDTO> data = body.getData();
                        if (!ObjectUtils.isEmpty(data)) {//根据城市三字码查询得到城市code
                            AirPortCityDTO airPortCityDTO = data.get(0);
                            String startCityCode = airPortCityDTO.getFbAreaInfos().get(0).getFbAreaCode();
                            intlFlightTripApproveDetail.setStartCityId(startCityCode);
                        }
                    }
                }

                intlFlightTripApproveDetail.setType(fbTripType);
                intlFlightTripApproveDetail.setTripType(singleOptionMap.get(widgetValueDTO.getSingle()).equals("单程") ? 1 : 2);
                intlFlightTripApproveDetail.setEstimatedAmount(0);
                intlFlightTripApproveDetail.setStartTime(beginDate);
                intlFlightTripApproveDetail.setEndTime(endDate);
                if (!singleOptionMap.get(widgetValueDTO.getSingle()).equals("单程")) {//国际机票单程往返单独处理
                    intlFlightTripApproveDetail.setBackStartTime(beginDate);
                    intlFlightTripApproveDetail.setBackEndTime(endDate);
                }

                if (StringUtils.isNotBlank(intlFlightTripApproveDetail.getStartCityId()) && StringUtils.isNotBlank(intlFlightTripApproveDetail.getArrivalCityId())) {//为空则说明城市名称错误
                    tripApproveDetails.add(intlFlightTripApproveDetail);
                }
                //非分贝用户的同行人信息，发送消息通知
                if (!ObjectUtils.isEmpty(unFbGuestList)) {
                    unFbGuestList.stream().forEach(unfbGuest -> {
                        Collection<String> values = unfbGuest.values();
                        values.removeAll(Collections.singleton(null));
                        values.stream().forEach(unfbGuestValue -> {
                            String msg = "通知：您创建的" + DateUtils.toSimpleStr(beginDate, true) + "到" + DateUtils.toSimpleStr(endDate, true) + "的" + startCityName + "城市到" + endCityName + "城市的分贝通差旅审批单，因出行人中的" + unfbGuestValue + "不存在分贝通创建失败，请您联系管理员处理后再次提交审批";
                            yunzhijiaNoticeSender.sender(corpId, employeeId, msg);
                        });
                    });
                }
            } else {//国内机票，酒店，火车场景
                TripApproveDetail tripApproveDetail = TripApproveDetail.builder().build();
                //先获取目的城市，在酒店场景中，出发城市和目的城市都填写目的城市的城市code
                //根据城市名称查询分贝城市code
                String endFbCityId = "";
                Call<OpenApiRespDTO<CityByNameRespDTO>> cityCodeByName = fbtCommonService.getCityCodeByName(endCityName.trim());
                try {
                    OpenApiRespDTO<CityByNameRespDTO> body = cityCodeByName.execute().body();
                    CityByNameRespDTO data = body.getData();
                    if (ObjectUtils.isEmpty(data)) {//返回城市名称为空，则无法查询到分贝城市code
                        String msg = "通知：您创建的" + DateUtils.toSimpleStr(beginDate, true) + "到" + DateUtils.toSimpleStr(endDate, true) + "的" + startCityName + "城市到" + endCityName + "城市的分贝通差旅审批单，因审批单中的城市不符合分贝通标准创建失败，请您准确填写城市并提交审批";
                        yunzhijiaNoticeSender.sender(corpId, employeeId, msg);
                    } else {
                        endFbCityId = data.getId().trim();
                        String endFbCityName = data.getName();
                        tripApproveDetail.setArrivalCityId(endFbCityId);
                    }
                } catch (IOException e) {
                    log.info("目的城市code获取失败");
                    e.printStackTrace();
                }
                //出发城市
                Call<OpenApiRespDTO<CityByNameRespDTO>> startCityCodeByName = fbtCommonService.getCityCodeByName(startCityName.trim());
                String startFbCityId = "";
                try {
                    OpenApiRespDTO<CityByNameRespDTO> body = startCityCodeByName.execute().body();
                    CityByNameRespDTO data = body.getData();
                    if (ObjectUtils.isEmpty(data)) {//返回城市名称为空，则无法查询到分贝城市code
                        String msg = "通知：您创建的" + DateUtils.toSimpleStr(beginDate, true) + "到" + DateUtils.toSimpleStr(endDate, true) + "的" + startCityName + "城市到" + endCityName + "城市的分贝通差旅审批单，因审批单中的城市不符合分贝通标准创建失败，请您准确填写城市并提交审批";
                        yunzhijiaNoticeSender.sender(corpId, employeeId, msg);
                    } else {
                        startFbCityId = data.getId().trim();
                        String startFbCityName = data.getName();
                        if (fbTripType == TripApproveType.Hotel.getValue()) {//酒店场景，需要同时设置为目的城市code
                            tripApproveDetail.setStartCityId(endFbCityId);
                        } else {
                            tripApproveDetail.setStartCityId(startFbCityId);
                        }
                    }
                } catch (IOException e) {
                    log.info("出发城市code获取失败");
                    e.printStackTrace();
                }

                tripApproveDetail.setStartTime(beginDate);
                tripApproveDetail.setEndTime(endDate);
                tripApproveDetail.setEstimatedAmount(0);
                //单程往返标识，单程往返需要单独进行设置.往返行程只需要再创建一个返程信息
                tripApproveDetail.setTripType(1);
                tripApproveDetail.setType(fbTripType);

                if (!singleOptionMap.get(widgetValueDTO.getSingle()).equals("单程")) {//如果是往返，需要添加一个返程信息
                    TripApproveDetail tripApproveDetail1 =
                            TripApproveDetail.builder()
                                    .tripType(1)
                                    .startCityId(tripApproveDetail.getArrivalCityId())
                                    .arrivalCityId(tripApproveDetail.getStartCityId())
                                    .startTime(beginDate)
                                    .endTime(endDate)
                                    .type(fbTripType)
                                    .estimatedAmount(0)
                                    .build();
                    tripApproveDetails.add(tripApproveDetail1);
                }
                if (StringUtils.isNotBlank(endFbCityId) && StringUtils.isNotBlank(startFbCityId)) {
                    tripApproveDetails.add(tripApproveDetail);
                }
                //非分贝用户的同行人信息，发送消息通知
                if (!ObjectUtils.isEmpty(unFbGuestList)) {
                    unFbGuestList.stream().forEach(unfbGuest -> {
                        Collection<String> values = unfbGuest.values();
                        values.removeAll(Collections.singleton(null));
                        values.stream().forEach(unfbGuestValue -> {
                            String msg = "通知：您创建的" + DateUtils.toSimpleStr(beginDate, true) + "到" + DateUtils.toSimpleStr(endDate, true) + "的" + startCityName + "城市到" + endCityName + "城市的分贝通差旅审批单，因出行人中的" + unfbGuestValue + "不存在分贝通创建失败，请您联系管理员处理后再次提交审批";
                            yunzhijiaNoticeSender.sender(corpId, employeeId, msg);
                        });
                    });
                }
            }
        });
        //构建审批单数据
        CreateTripApproveReqDTO createTripApproveReqDTO = new CreateTripApproveReqDTO();
        if (build != null) {
            CreateTripApply createTripApply = new CreateTripApply();
            BeanUtils.copyProperties(build, createTripApply);
            createTripApproveReqDTO.setApply(createTripApply);
        }
        if (!ObjectUtils.isEmpty(tripApproveDetails)) {
            List<Trip> collect = tripApproveDetails.stream().map(t -> {
                Trip trip = new Trip();
                BeanUtils.copyProperties(t, trip);
                return trip;
            }).collect(Collectors.toList());
            createTripApproveReqDTO.setTripList(collect);
        }
        if (!ObjectUtils.isEmpty(guestList)) {//同行人不为空
            createTripApproveReqDTO.setGuestList(guestList);
        }

        if (StringUtils.isBlank(token)) {//获取到人员token
            log.info("人员不存在，检查人员设置是否出现异常");
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_FB_EMPLOYEE_IS_NOT_EXIST)));
        }
        log.info("云之家插件创建OpenAPI审批单请求参数 {}", JsonUtils.toJson(createTripApproveReqDTO));
        if (!ObjectUtils.isEmpty(tripApproveDetails)) {
            String url = saasHost.concat("/apply/third/create");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-Auth-Token", token);
           String result  = RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(createTripApproveReqDTO));
            OpenApiResponseDTO<CreateApplyRespDTO> tripApprove = JsonUtils.toObj(result, new TypeReference<OpenApiResponseDTO<CreateApplyRespDTO>>(){});
            if (tripApprove == null || !tripApprove.success()) {
                String msg = tripApprove == null || ObjectUtils.isEmpty(tripApprove.getMsg()) ? "" : ("," + tripApprove.getMsg());
                //TODO 调用消息通知，或者根据消息相关通知进行处理
                throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, msg);
            }
            return TaskResult.SUCCESS;
        }
        return TaskResult.ABORT;
    }


    /**
     * 根据集合数据组装成相应格式的发送消息数据
     *
     * @param cityList
     * @return
     */
    public List<Map<String, String>> genMsgListStr(List<CityIntlAirRespDTO.CityIntlAirResult> cityList) {
        List<Map<String, String>> mapList = Lists.newArrayList();
        cityList.stream().forEach(cityIntlAirResult -> {
            //国家
            String country = cityIntlAirResult.getCrName();
            //城市名称
            String cityName = cityIntlAirResult.getName();
            //城市三字码
            String cityCode = cityIntlAirResult.getCityPortCode();
            Map<String, String> msgMap = Maps.newHashMap();
            msgMap.put("国家名称", country);
            msgMap.put("城市名称", cityName);
            msgMap.put("城市三字码", cityCode);
            mapList.add(msgMap);
        });
        return mapList;
    }


    public static void main(String[] args) {


    }

    public static void getValues(String key, String value, Object object) {
        Object returnStr = null;
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field: fields) {
                field.setAccessible(true);
                returnStr = field.get(object);
                if (field.getAnnotation(JsonProperty.class) != null) {
                    JsonProperty annotation = ((JsonProperty) field.getAnnotation(JsonProperty.class));
                    if (annotation != null) {
                        System.out.println(annotation + "");
                        String jsonPropertyValue = annotation.value();
//                        if (key.equals(jsonPropertyValue)) {
//                            field.set(object, value);
//                            break;
//                        }
                    }
                }else{
              String fieldName = field.getName();
//                if(key.equals(fieldName)){
//                   field.set(object,value);
//                        break;
//                }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
