package com.fenbeitong.openapi.plugin.yunzhijia.process;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessType;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractApplyService;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiResponseDTO;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyEventDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.notice.sender.YunzhijiaNoticeSender;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.uc.YunzhijiaFbEmployeeService;
import com.fenbeitong.openapi.sdk.dto.approve.CreateCarApproveReqDTO;
import com.fenbeitong.openapi.sdk.dto.city.CityCarRespDTO;
import com.fenbeitong.openapi.sdk.dto.common.OpenApiRespDTO;
import com.fenbeitong.openapi.sdk.webservice.approve.FbtApproveService;
import com.fenbeitong.openapi.sdk.webservice.common.FbtCommonService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class YunzhijiaCarProcessApply extends AbstractApplyService implements IYunzhijiaProcessApply {
    @Autowired
    FbtApproveService fbtApproveService;
    @Autowired
    YunzhijiaFbEmployeeService yunzhijiaFbEmployeeService;
    @Autowired
    FbtCommonService fbtCommonService;
    @Autowired
    YunzhijiaNoticeSender yunzhijiaNoticeSender;
    @Value("${host.saas}")
    private String saasHost;

    @Override
    public TaskResult processApply(Task task, ThirdApplyDefinition thirdApplyDefinition, PluginCorpDefinition pluginCorp, YunzhijiaApplyEventDTO.YunzhijiaApplyData yunzhijiaApplyData) {
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        YunzhijiaApplyEventDTO.YunzhijiaApplyDetailInfoDTO detailInfo = yunzhijiaApplyData.getDetailInfo();
        YunzhijiaApplyEventDTO.WidgetMapDTO widgetMap = detailInfo.getWidgetMap();
        //获取标题数据
        YunzhijiaApplyEventDTO.WidgetDTO title = widgetMap.getTitle();
        String titleCodeId = title.getCodeId();
        String titleType = title.getType();
        Object titleValue = title.getValue();
        //获取流水号
        YunzhijiaApplyEventDTO.WidgetDTO serialNo = widgetMap.getSerialNo();
        Object serialNoValue = serialNo.getValue();
        //提交人数据
        YunzhijiaApplyEventDTO.WidgetDTO applyUser = widgetMap.getApplyUser();
        //人员ID
        List<String> userValue = (List) applyUser.getValue();
        //获取用车城市
        YunzhijiaApplyEventDTO.WidgetDTO city = widgetMap.getCity();
        Object cityNameValue = city.getValue();
        //TODO 根据城市名称获取分贝城市code
        //获取用车申请时间
        YunzhijiaApplyEventDTO.WidgetDTO data = widgetMap.getDate();
        Object applyDate = data.getValue();
        //用车使用时间
        YunzhijiaApplyEventDTO.WidgetDTO dateList = widgetMap.getDateList();
        //包含开始时间和结束时间
        List<Long> dateValue = (List) dateList.getValue();
        //获取用车次数
        YunzhijiaApplyEventDTO.WidgetDTO count = widgetMap.getCount();
        Object countValue = count.getValue();
        //获取用车金额
        YunzhijiaApplyEventDTO.WidgetDTO money = widgetMap.getMoney();
        Object moneyValue = money.getValue();
        //获取申请事由
        YunzhijiaApplyEventDTO.WidgetDTO reason = widgetMap.getReason();
        Object reasonValue = reason.getValue();
        //用车审批参数组装
        //创建分贝通用车审批单
        CreateCarApproveReqDTO createCarApproveReqDTO = new CreateCarApproveReqDTO();
        //人员信息设置
        createCarApproveReqDTO.setEmployeeId(userValue.get(0));
        //类型为第三方
        createCarApproveReqDTO.setEmployeeType("1");
        //审批单基本信息设置
        CreateCarApproveReqDTO.CreateCarApplyReqApply createCarApplyReqApply = new CreateCarApproveReqDTO.CreateCarApplyReqApply();
        createCarApplyReqApply.setFlowType(4);
        createCarApplyReqApply.setType(ProcessType.Car.getKey());
        createCarApplyReqApply.setThirdId(dataId);
        createCarApplyReqApply.setBudget(Double.valueOf((String)moneyValue)*100);
        //具体行程信息
        CreateCarApproveReqDTO.CreateCarApplyReqTrip createCarApplyReqTrip = new CreateCarApproveReqDTO.CreateCarApplyReqTrip();
        //TODO 测试暂时写死
        //根据城市名称获取分贝城市ID cityNameValue
        Call<OpenApiRespDTO<CityCarRespDTO>> carCityByName = fbtCommonService.getCarCityByName((String) cityNameValue);
        try {
            OpenApiRespDTO<CityCarRespDTO> body = carCityByName.execute().body();
            CityCarRespDTO carCityResp = body.getData();
            if (ObjectUtils.isEmpty(carCityResp)) {//根据城市名称没有查询到相对应的分贝城市code
                String msg = "您创建的从" + cityNameValue + "出发，到" + cityNameValue + "的云之家用车审批单城市名称错误，请核对名称后重新填写云之家申请单";
                yunzhijiaNoticeSender.sender(corpId, userValue.get(0), msg);
                return TaskResult.ABORT;
            } else {
                String cityCode = carCityResp.getId();
                createCarApplyReqTrip.setStartCityId(cityCode);
                createCarApplyReqTrip.setArrivalCityId(cityCode);
            }
        } catch (IOException e) {
            log.info("申请用车根据城市名称查询分贝城市code返回异常 {}");
            e.printStackTrace();
        }

        createCarApplyReqTrip.setStartTime(DateUtils.toSimpleStr(DateUtils.toDate(dateValue.get(0)), true));
        createCarApplyReqTrip.setEndTime(DateUtils.toSimpleStr(DateUtils.toDate(dateValue.get(1)), true));
        createCarApplyReqTrip.setType(3);
        ArrayList<CreateCarApproveReqDTO.CreateCarApplyReqTrip> tripList = Lists.newArrayList();
        tripList.add(createCarApplyReqTrip);
        //审批单详细信息设置
        CreateCarApproveReqDTO.CreateCarApplyReqData createCarApplyReqData = new CreateCarApproveReqDTO.CreateCarApplyReqData();
        //设置用车基本信息
        createCarApplyReqData.setApply(createCarApplyReqApply);
        //设置用车行程信息
        createCarApplyReqData.setTripList(tripList);
        //设置用车规则信息
        CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule createCarApplyReqTaxiRule = new CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule();
        createCarApplyReqTaxiRule.setType("taxi_scheduling_fee");
        createCarApplyReqTaxiRule.setValue("-1");
        CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule createCarApplyReqTaxiRule1 = new CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule();
        createCarApplyReqTaxiRule1.setType("allow_same_city");
        createCarApplyReqTaxiRule1.setValue("false");
        CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule createCarApplyReqTaxiRule2 = new CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule();
        createCarApplyReqTaxiRule2.setType("allow_called_for_other");
        createCarApplyReqTaxiRule2.setValue("true");
        CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule createCarApplyReqTaxiRule3 = new CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule();
        createCarApplyReqTaxiRule3.setType("price_limit");
        createCarApplyReqTaxiRule3.setValue("-1");
        CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule createCarApplyReqTaxiRule4 = new CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule();
        createCarApplyReqTaxiRule4.setType("day_price_limit");
        createCarApplyReqTaxiRule4.setValue("-1");
        CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule createCarApplyReqTaxiRule5 = new CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule();
        createCarApplyReqTaxiRule5.setType("times_limit_flag");
        createCarApplyReqTaxiRule5.setValue("2");


        CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule createCarApplyReqTaxiRule6 = new CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule();
        createCarApplyReqTaxiRule6.setType("times_limit");
        createCarApplyReqTaxiRule6.setValue(String.valueOf(countValue));
        CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule createCarApplyReqTaxiRule7 = new CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule();
        createCarApplyReqTaxiRule7.setType("total_price");
        createCarApplyReqTaxiRule7.setValue(String.valueOf(moneyValue));

        CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule createCarApplyReqTaxiRule8 = new CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule();
        createCarApplyReqTaxiRule8.setType("city_limit");
        createCarApplyReqTaxiRule8.setValue("1");
        CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule createCarApplyReqTaxiRule9 = new CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule();
        createCarApplyReqTaxiRule9.setType("price_limit_flag");
        createCarApplyReqTaxiRule9.setValue("2");

        List<CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule> carRuleList = Lists.newArrayList();
        carRuleList.add(createCarApplyReqTaxiRule);
        carRuleList.add(createCarApplyReqTaxiRule1);
        carRuleList.add(createCarApplyReqTaxiRule2);
        carRuleList.add(createCarApplyReqTaxiRule3);
        carRuleList.add(createCarApplyReqTaxiRule4);
        carRuleList.add(createCarApplyReqTaxiRule5);
        carRuleList.add(createCarApplyReqTaxiRule6);
        carRuleList.add(createCarApplyReqTaxiRule7);
        carRuleList.add(createCarApplyReqTaxiRule8);
        carRuleList.add(createCarApplyReqTaxiRule9);

        createCarApplyReqData.setApplyTaxiRuleInfo(carRuleList);
        createCarApproveReqDTO.setData(createCarApplyReqData);
        log.info("云之家创建用车申请单请求参数 {}", JsonUtils.toJson(createCarApproveReqDTO));
        String token = yunzhijiaFbEmployeeService.getCreateTripApproveToken(pluginCorp.getAppId(), userValue.get(0));

        String url = saasHost.concat("/apply/third/applyTaxi/create");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        String result  = RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(createCarApproveReqDTO));
        OpenApiResponseDTO<CreateApplyRespDTO> carApprove = JsonUtils.toObj(result, new TypeReference<OpenApiResponseDTO<CreateApplyRespDTO>>(){});
        if (carApprove == null || !carApprove.success()) {
            String msg = carApprove == null || ObjectUtils.isEmpty(carApprove.getMsg()) ? "" : ("," + carApprove.getMsg());
            //TODO 调用消息通知，或者根据消息相关通知进行处理
            throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, msg);
        }
        return TaskResult.SUCCESS;
    }

}
