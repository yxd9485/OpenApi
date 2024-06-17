package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common;

import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenDinnerApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonOrderInfo;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.weaver.v8.workflow.WorkflowMainTableInfo;
import com.weaver.v8.workflow.WorkflowRequestTableField;
import com.weaver.v8.workflow.WorkflowRequestTableRecord;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 泛微表单主子表构建
 * @Auther zhang.peng
 * @Date 2021/5/27
 */
@Slf4j
public class WorkFlowRequestTableFieldBuilder {

    private static final String MAIN_TABLE = "main";
    private static final String DETAIL_TABLE = "detail";
    private static final String FBT_APPROVE_ID = "fbtApproveId";
    private static final String RULE_INFO = "ruleInfo";
    private static final String REASON_INFO = "reasonInfo";
    private static final String CITY_INFO = "cityName";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String TRAFFIC_TYPE = "trafficType";
    private static final String TRIP_TYPE = "tripType";
    private static final String START_CITY_NAME = "startCityName";
    private static final String ARRIVAL_CITY_NAME = "arrivalCityName";
    private static final String START_HOUR = "startHour";
    private static final String END_HOUR = "endHour";
    private static final String TRAVEL_DAY = "travelDay";
    /*****用车费用*****/
    private static final String CAR_COST = "carCost";
    /*****用车次数*****/
    private static final String CAR_TIMES = "carTimes";
    private static final String ESTIMATED_AMOUNT = "estimatedAmount";

    /*****用餐信息*****/
    private static final String ORDER_PERSON = "orderPerson";
    private static final String APPLY_TIME = "applyTime";
    private static final String DINNER_PRICE = "dinnerPrice";
    private static final String PERSON_NUMBER = "personNumber";
    private static final String DINNER_CITY = "dinnerCity";

    /**
     * 超规信息
     */
    private static final String COST_ATTRIBUTION = "costAttribution";
    private static final String APPROVE_AMOUNT = "approveAmount";
    private static final String BUSINESS_TYPE = "businessType";
    private static final String USER_PERSON = "userPerson";

    public static WorkflowRequestTableField[] buildTripDetailInfo(FenbeitongApproveDto fenbeitongApproveDto , FenbeitongApproveDto.Trip trip , List<OpenEtlMappingConfig> mappingConfigList){

        WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[6];//主的4个字段

        mappingConfigList = mappingConfigList.stream().filter(config -> DETAIL_TABLE.equals(config.getGroupName())).collect(Collectors.toList());
        if (CollectionUtils.isBlank(mappingConfigList)){
            return WorkflowRequestTableField;
        }
        WorkflowRequestTableField = new WorkflowRequestTableField[mappingConfigList.size()];//每行13个字段

        buildDetailFields(mappingConfigList,WorkflowRequestTableField,fenbeitongApproveDto,trip,null);

        return WorkflowRequestTableField;
    }

    public static WorkflowRequestTableField[] buildCarDetailInfo(FenbeitongApproveDto fenbeitongApproveDto , FenbeitongApproveDto.Trip trip , String cityName , List<OpenEtlMappingConfig> mappingConfigList){

        WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[6];//主的4个字段

        mappingConfigList = mappingConfigList.stream().filter(config -> DETAIL_TABLE.equals(config.getGroupName())).collect(Collectors.toList());
        if (CollectionUtils.isBlank(mappingConfigList)){
            return WorkflowRequestTableField;
        }

        WorkflowRequestTableField = new WorkflowRequestTableField[mappingConfigList.size()];//每行13个字段

        buildDetailFields(mappingConfigList,WorkflowRequestTableField,fenbeitongApproveDto,trip,cityName);

        return WorkflowRequestTableField;
    }

    public static WorkflowMainTableInfo buildTripMainTableInfo(FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId , List<OpenEtlMappingConfig> mappingConfigList){

        WorkflowMainTableInfo workflowMainTableInfo = new WorkflowMainTableInfo();//主表
        WorkflowRequestTableRecord[] workflowRequestTableRecord = new WorkflowRequestTableRecord[1];//主表字段只有一条记录

        mappingConfigList = mappingConfigList.stream().filter(config -> MAIN_TABLE.equals(config.getGroupName())).collect(Collectors.toList());
        if (CollectionUtils.isBlank(mappingConfigList)){
            return workflowMainTableInfo;
        }

        WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[mappingConfigList.size()];//主的字段

        buildFields(mappingConfigList,WorkflowRequestTableField,fenbeitongApproveDto,fbtApproveId);

        workflowRequestTableRecord[0] = new WorkflowRequestTableRecord();
        workflowRequestTableRecord[0].setWorkflowRequestTableFields(WorkflowRequestTableField);
        workflowMainTableInfo.setRequestRecords(workflowRequestTableRecord);

        return workflowMainTableInfo;
    }

    public static WorkflowMainTableInfo buildCarMainTableInfo(FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId , List<OpenEtlMappingConfig> mappingConfigList){

        WorkflowMainTableInfo workflowMainTableInfo = new WorkflowMainTableInfo();//主表
        WorkflowRequestTableRecord[] workflowRequestTableRecord = new WorkflowRequestTableRecord[1];//主表字段只有一条记录

        if (CollectionUtils.isBlank(mappingConfigList)){
            return workflowMainTableInfo;
        }
        mappingConfigList = mappingConfigList.stream().filter(config -> MAIN_TABLE.equals(config.getGroupName())).collect(Collectors.toList());
        if (CollectionUtils.isBlank(mappingConfigList)){
            return workflowMainTableInfo;
        }
        WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[mappingConfigList.size()];//主的字段

        buildFields(mappingConfigList,WorkflowRequestTableField,fenbeitongApproveDto,fbtApproveId);

        workflowRequestTableRecord[0] = new WorkflowRequestTableRecord();
        workflowRequestTableRecord[0].setWorkflowRequestTableFields(WorkflowRequestTableField);
        workflowMainTableInfo.setRequestRecords(workflowRequestTableRecord);

        return workflowMainTableInfo;
    }

    public static void buildFields(List<OpenEtlMappingConfig> mappingConfigList , WorkflowRequestTableField[] WorkflowRequestTableField , FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId ){
        Field[] fields = fenbeitongApproveDto.getClass().getDeclaredFields();
        Set<String> fieldSet = new HashSet<>();
        if ( fields.length != 0){
            for (Field field : fields) {
                fieldSet.add(field.getName());
            }
        }
        List<FenbeitongApproveDto.Trip> tripList = fenbeitongApproveDto.getTripList();
        StringBuilder ruleInfoBuilder = new StringBuilder();
        if (CollectionUtils.isNotBlank(tripList)){
            FenbeitongApproveDto.Trip trip = tripList.get(0);
            List<FenbeitongApproveDto.RuleInfo> ruleInfos = trip.getRuleInfos();
            if (CollectionUtils.isNotBlank(ruleInfos)){
                ruleInfos.stream().forEach(ruleInfo -> {
                    ruleInfoBuilder.append(ruleInfo.getKey() + ":" + ruleInfo.getValue() + "\n");
                });
            }
        }
        String reasonInfo = fenbeitongApproveDto.getApplyReason() + (StringUtils.isBlank(fenbeitongApproveDto.getApplyReasonDesc()) ? "" : (";" + fenbeitongApproveDto.getApplyReasonDesc()));
        for (int i = 0; i < mappingConfigList.size(); i++) {
            WorkflowRequestTableField[i] = new WorkflowRequestTableField();
            OpenEtlMappingConfig config = mappingConfigList.get(i);
            String sourceName = config.getSrcCol();
            WorkflowRequestTableField[i].setFieldName(config.getTgtCol());//姓名
            try {
                if (fieldSet.contains(sourceName)){
                    Field field = fenbeitongApproveDto.getClass().getDeclaredField(sourceName);
                    field.setAccessible(true);
                    WorkflowRequestTableField[i].setFieldValue((String) field.get(fenbeitongApproveDto));//
                }
                if (RULE_INFO.equals(sourceName)){
                    WorkflowRequestTableField[i].setFieldValue(ruleInfoBuilder.toString());//
                }
                if (FBT_APPROVE_ID.equals(sourceName)){
                    WorkflowRequestTableField[i].setFieldValue(fbtApproveId);//
                }
                if (REASON_INFO.equals(sourceName)){
                    WorkflowRequestTableField[i].setFieldValue(reasonInfo);//
                }
                buildDinnerFields(fenbeitongApproveDto,sourceName,WorkflowRequestTableField[i]);
                buildOrderFields(fenbeitongApproveDto,sourceName,WorkflowRequestTableField[i]);
            } catch (Exception e) {
                log.warn("转换字段失败 : {} " , e.getMessage());
            }
            WorkflowRequestTableField[i].setView(true);//字段是否可见
            WorkflowRequestTableField[i].setEdit(true);//字段是否可编辑
        }
    }

    public static void buildDinnerFields(FenbeitongApproveDto fenbeitongApproveDto , String name , WorkflowRequestTableField tableField ){
        if (!StringUtils.isBlank(tableField.getFieldValue())){
            return;
        }
        String value = "";
        if (APPLY_TIME.equals(name)){
            Date now = new Date();
            value = DateUtils.toStr(now,"yyyy-MM-dd");
        }
        if (ORDER_PERSON.equals(name)){
            value = fenbeitongApproveDto.getApplyName();
        }
        if (DINNER_PRICE.equals(name)){
            value = null == getTripInfo(fenbeitongApproveDto) ? "" : getTripInfo(fenbeitongApproveDto).getEstimatedAmount();
        }
        if (PERSON_NUMBER.equals(name)){
            value = null == getTripInfo(fenbeitongApproveDto) ? "" : getTripInfo(fenbeitongApproveDto).getPersonCount();
        }
        if (DINNER_CITY.equals(name)){
            value = null == getTripInfo(fenbeitongApproveDto) ? "" : getTripInfo(fenbeitongApproveDto).getStartCityName();
        }
        if (START_TIME.equals(name)){
            String startTime = getTripInfo(fenbeitongApproveDto).getStartTime();
            if ( !StringUtils.isBlank(startTime) ){
//                startTime = startTime + ":00";
                startTime = startTime ;
            }
            value = StringUtils.isBlank(startTime) ? "" : startTime;
        }
        if (END_TIME.equals(name)){
            String endTime = getTripInfo(fenbeitongApproveDto).getEndTime();
            if ( !StringUtils.isBlank(endTime) && endTime.contains(" ") ){
                String[] times = endTime.split(" ");
                endTime = "24:00".equals(times[1]) ? (times[0] + " 23:59:59") : (endTime + ":00");
            }
            value = StringUtils.isBlank(endTime) ? "" : DateUtils.toStr(DateUtils.toDate(endTime),"yyyy-MM-dd HH:mm");
        }
        tableField.setFieldValue(value);//
    }

    public static void buildOrderFields(FenbeitongApproveDto fenbeitongApproveDto , String name , WorkflowRequestTableField tableField ){
        // 超规原因特殊
        if (REASON_INFO.equals(name)){
            String reasonInfo = StringUtils.isBlank(fenbeitongApproveDto.getApplyReasonDesc()) ? "" : fenbeitongApproveDto.getApplyReasonDesc();
            tableField.setFieldValue(reasonInfo);//
        }
        if (APPLY_TIME.equals(name)){
            Date now = new Date();
            tableField.setFieldValue(DateUtils.toStr(now,DateUtils.FORMAT_DATE_PATTERN));
        }
        if (!StringUtils.isBlank(tableField.getFieldValue())){
            return;
        }
        String value = "";
        if (BUSINESS_TYPE.equals(name)){
            // 0=机票，1=酒店，2=火车票
//            value = null == fenbeitongApproveDto.getAir() ? ( null == fenbeitongApproveDto.getTrain() ? "酒店" : "火车" ) : "机票";
            value = null == fenbeitongApproveDto.getAir() ? ( null == fenbeitongApproveDto.getTrain() ? "1" : "2" ) : "0";
        }
        if (APPROVE_AMOUNT.equals(name)){
            value = fenbeitongApproveDto.getOrderPrice();
        }
        if (COST_ATTRIBUTION.equals(name)){
            value = fenbeitongApproveDto.getCost_attribution_id();
        }
        if (USER_PERSON.equals(name)){
            List<FenbeitongApproveDto.Guest> guestList = fenbeitongApproveDto.getGuestList();
            if (CollectionUtils.isNotBlank(guestList)){
                List<String> userNames = guestList.stream().map(guest -> guest.getThirdEmployeeId()).collect(Collectors.toList());
                value = userNames.toString().replace("[","").replace("]","");
            }
        }
        tableField.setFieldValue(value);
    }

    private static FenbeitongApproveDto.Trip getTripInfo(FenbeitongApproveDto fenbeitongApproveDto){
        if ( null == fenbeitongApproveDto || CollectionUtils.isBlank(fenbeitongApproveDto.getTripList())){
            return null;
        }
        return fenbeitongApproveDto.getTripList().get(0);
    }

    public static void buildDetailFields(List<OpenEtlMappingConfig> mappingConfigList , WorkflowRequestTableField[] WorkflowRequestTableField , FenbeitongApproveDto fenbeitongApproveDto ,FenbeitongApproveDto.Trip trip , String cityName ){
        Field[] fields = fenbeitongApproveDto.getClass().getDeclaredFields();
        Set<String> fieldSet = new HashSet<>();
        if ( fields.length != 0){
            for (Field field : fields) {
                fieldSet.add(field.getName());
            }
        }
        for (int i = 0; i < mappingConfigList.size(); i++) {
            WorkflowRequestTableField[i] = new WorkflowRequestTableField();
            OpenEtlMappingConfig config = mappingConfigList.get(i);
            String sourceName = config.getSrcCol();
            WorkflowRequestTableField[i].setFieldName(config.getTgtCol());//姓名
            try {
                WorkflowRequestTableField[i].setView(true);//字段是否可见
                WorkflowRequestTableField[i].setEdit(true);//字段是否可编辑
                if (TRAVEL_DAY.equals(sourceName)){
                    if (StringUtils.isBlank(trip.getStartTime()) || StringUtils.isBlank(trip.getEndTime())){
                        continue;
                    }
                    Date startDate = DateUtils.toDate(trip.getStartTime()) == null ? new Date() : DateUtils.toDate(trip.getStartTime());
                    Date endDate = DateUtils.toDate(trip.getEndTime()) == null ? new Date() : DateUtils.toDate(trip.getEndTime());
                    int day = DateUtils.differentDaysByMillisecond(startDate, endDate) + 1;
                    WorkflowRequestTableField[i].setFieldValue(day + ".00");//
                    continue;
                }
                if (fieldSet.contains(sourceName)){
                    Field field = fenbeitongApproveDto.getClass().getDeclaredField(sourceName);
                    field.setAccessible(true);
                    WorkflowRequestTableField[i].setFieldValue((String) field.get(fenbeitongApproveDto));//
                }
                if (CITY_INFO.equals(sourceName)){
                    WorkflowRequestTableField[i].setFieldValue(cityName);//
                }
                if (START_TIME.equals(sourceName)){
                    String startTime = DateUtils.toStr(DateUtils.toDate(trip.getStartTime()),"yyyy-MM-dd");
                    WorkflowRequestTableField[i].setFieldValue(startTime);//
                }
                if (END_TIME.equals(sourceName)){
                    String endTime = DateUtils.toStr(DateUtils.toDate(trip.getEndTime()),"yyyy-MM-dd");
                    WorkflowRequestTableField[i].setFieldValue(endTime);//
                }
                if (TRAFFIC_TYPE.equals(sourceName)){
                    WorkflowRequestTableField[i].setFieldValue(ApplyTripType.getValueByKey(trip.getType()));//
                }
                if (TRIP_TYPE.equals(sourceName)){
                    int tripType = (StringUtils.isBlank(trip.getTripType()) || "0".equals(trip.getTripType())) ? 1 : Integer.parseInt(trip.getTripType());
                    String tripTypeString = tripType == 1 ? "单程" : "往返";
                    WorkflowRequestTableField[i].setFieldValue(tripTypeString);//
                }
                if (START_CITY_NAME.equals(sourceName)){
                    WorkflowRequestTableField[i].setFieldValue(trip.getStartCityName());//
                }
                if (ARRIVAL_CITY_NAME.equals(sourceName)){
                    WorkflowRequestTableField[i].setFieldValue(trip.getArrivalCityName());//
                    // 如果差旅类型是酒店的话 , 目的城市取出发城市
                    if ( ApplyTripType.HOTEL.getCode() == trip.getType() ){
                        WorkflowRequestTableField[i].setFieldValue(trip.getStartCityName());//
                    }
                }
                if (START_HOUR.equals(sourceName)){
                    // 开始时间取配置信息
                    WorkflowRequestTableField[i].setFieldValue(config.getScript());//
                }
                if (END_HOUR.equals(sourceName)){
                    // 结束时间取配置信息
                    WorkflowRequestTableField[i].setFieldValue(config.getScript());//
                }
                if (CAR_COST.equals(sourceName)){
                    String priceLimit = null == trip ? "" : trip.getPriceLimit();
                    priceLimit = "-1".equals(priceLimit) ? "用车费用不限制" : priceLimit;
                    WorkflowRequestTableField[i].setFieldValue(priceLimit);//
                }
                if (CAR_TIMES.equals(sourceName)){
                    String personCount = null == trip ? "" : trip.getPersonCount();
                    personCount = "-1".equals(personCount) ? "用车次数不限制" : personCount;
                    WorkflowRequestTableField[i].setFieldValue(personCount);//
                }
                if (ESTIMATED_AMOUNT.equals(sourceName)){
                    String estimatedAmount = null == trip ? "" : trip.getEstimatedAmount();
                    WorkflowRequestTableField[i].setFieldValue(estimatedAmount);//
                }
            } catch (Exception e) {
                log.warn("转换字段失败 : {} " , e.getMessage());
            }
        }
    }

    public static WorkflowMainTableInfo buildDinnerMainTableInfo(FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId , List<OpenEtlMappingConfig> mappingConfigList){

        WorkflowMainTableInfo workflowMainTableInfo = new WorkflowMainTableInfo();//主表
        WorkflowRequestTableRecord[] workflowRequestTableRecord = new WorkflowRequestTableRecord[1];//主表字段只有一条记录

        if (CollectionUtils.isBlank(mappingConfigList)){
            return workflowMainTableInfo;
        }
        mappingConfigList = mappingConfigList.stream().filter(config -> MAIN_TABLE.equals(config.getGroupName())).collect(Collectors.toList());
        if (CollectionUtils.isBlank(mappingConfigList)){
            return workflowMainTableInfo;
        }
        WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[mappingConfigList.size()];//主的字段

        buildFields(mappingConfigList,WorkflowRequestTableField,fenbeitongApproveDto,fbtApproveId);

        workflowRequestTableRecord[0] = new WorkflowRequestTableRecord();
        workflowRequestTableRecord[0].setWorkflowRequestTableFields(WorkflowRequestTableField);
        workflowMainTableInfo.setRequestRecords(workflowRequestTableRecord);

        return workflowMainTableInfo;
    }

    public static WorkflowMainTableInfo buildOrderMainTableInfo(FenbeitongApproveDto fenbeitongApproveDto, String fbtApproveId, List<OpenEtlMappingConfig> mappingConfigList){

        WorkflowMainTableInfo workflowMainTableInfo = new WorkflowMainTableInfo();//主表
        WorkflowRequestTableRecord[] workflowRequestTableRecord = new WorkflowRequestTableRecord[1];//主表字段只有一条记录

        if (CollectionUtils.isBlank(mappingConfigList)){
            return workflowMainTableInfo;
        }
        mappingConfigList = mappingConfigList.stream().filter(config -> MAIN_TABLE.equals(config.getGroupName())).collect(Collectors.toList());
        if (CollectionUtils.isBlank(mappingConfigList)){
            return workflowMainTableInfo;
        }
        WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[mappingConfigList.size()];//主的字段

        buildFields(mappingConfigList,WorkflowRequestTableField,fenbeitongApproveDto,fbtApproveId);

        workflowRequestTableRecord[0] = new WorkflowRequestTableRecord();
        workflowRequestTableRecord[0].setWorkflowRequestTableFields(WorkflowRequestTableField);
        workflowMainTableInfo.setRequestRecords(workflowRequestTableRecord);

        return workflowMainTableInfo;
    }

    public static WorkflowRequestTableField[] buildOrderDetailInfo(FenbeitongApproveDto fenbeitongApproveDto , CommonOrderInfo commonOrderInfo , List<OpenEtlMappingConfig> mappingConfigList){

        WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[6];//子的4个字段

        mappingConfigList = mappingConfigList.stream().filter(config -> DETAIL_TABLE.equals(config.getGroupName())).collect(Collectors.toList());
        if (CollectionUtils.isBlank(mappingConfigList)){
            return WorkflowRequestTableField;
        }
        WorkflowRequestTableField = new WorkflowRequestTableField[mappingConfigList.size()];//每行13个字段

        buildOrderFields(mappingConfigList,WorkflowRequestTableField,fenbeitongApproveDto,commonOrderInfo);

//        return WorkflowRequestTableField;
        return filterEmptyElement(WorkflowRequestTableField);
    }

    public static WorkflowRequestTableField[] filterEmptyElement(WorkflowRequestTableField[] workflowRequestTableField){
        List<WorkflowRequestTableField> newWorkflowRequestTableFieldList = new ArrayList<>();
        if ( null == workflowRequestTableField || workflowRequestTableField.length == 0 ){
            return workflowRequestTableField;
        }
        Set<String> hasValueSet = new HashSet<>();
        for (int i = 0; i < workflowRequestTableField.length; i++) {
            //去掉相同类型的字段
            if (hasValueSet.contains(workflowRequestTableField[i].getFieldName())){
                continue;
            }
            if (!StringUtils.isBlank(workflowRequestTableField[i].getFieldValue())){
                newWorkflowRequestTableFieldList.add(workflowRequestTableField[i]);
                hasValueSet.add(workflowRequestTableField[i].getFieldName());
            }
        }
        if (CollectionUtils.isBlank(newWorkflowRequestTableFieldList)) {
            return workflowRequestTableField;
        }
        return newWorkflowRequestTableFieldList.toArray(new WorkflowRequestTableField[newWorkflowRequestTableFieldList.size()]);
    }

    public static void buildOrderFields(List<OpenEtlMappingConfig> mappingConfigList , WorkflowRequestTableField[] WorkflowRequestTableField , FenbeitongApproveDto fenbeitongApproveDto , CommonOrderInfo commonOrderInfo ){
        Field[] fields = fenbeitongApproveDto.getClass().getDeclaredFields();
        Set<String> fieldSet = new HashSet<>();
        if ( fields.length != 0){
            for (Field field : fields) {
                fieldSet.add(field.getName());
            }
        }
        Set<String> airFieldSet = new HashSet<>();
        Set<String> trainFieldSet = new HashSet<>();
        Set<String> hotelFieldSet = new HashSet<>();
        CommonOrderInfo.Air air = commonOrderInfo.getAir();
        CommonOrderInfo.Train train = commonOrderInfo.getTrain();
        CommonOrderInfo.Hotel hotel = commonOrderInfo.getHotel();
        if ( null != air ){
            Field[] airFields = air.getClass().getDeclaredFields();
            buildFieldsSet(airFields,airFieldSet);
        }
        if ( null != train ){
            Field[] trainFields = train.getClass().getDeclaredFields();
            buildFieldsSet(trainFields,trainFieldSet);
        }
        if ( null != hotel ){
            Field[] hotelFields = hotel.getClass().getDeclaredFields();
            buildFieldsSet(hotelFields,hotelFieldSet);
        }
        for (int i = 0; i < mappingConfigList.size(); i++) {
            WorkflowRequestTableField[i] = new WorkflowRequestTableField();
            OpenEtlMappingConfig config = mappingConfigList.get(i);
            String sourceName = config.getSrcCol();
            WorkflowRequestTableField[i].setFieldName(config.getTgtCol());//姓名
            try {
                WorkflowRequestTableField[i].setView(true);//字段是否可见
                WorkflowRequestTableField[i].setEdit(true);//字段是否可编辑
                if (fieldSet.contains(sourceName)){
                    Field field = fenbeitongApproveDto.getClass().getDeclaredField(sourceName);
                    field.setAccessible(true);
                    WorkflowRequestTableField[i].setFieldValue((String) field.get(fenbeitongApproveDto));//
                }
                if (airFieldSet.contains(sourceName) && !StringUtils.isBlank(air.getAirLine())){
                    Field field = commonOrderInfo.getAir().getClass().getDeclaredField(sourceName);
                    field.setAccessible(true);
                    WorkflowRequestTableField[i].setFieldValue((String) field.get(commonOrderInfo.getAir()));//
                }
                if (trainFieldSet.contains(sourceName) && !StringUtils.isBlank(train.getStartTime())){
                    Field field = commonOrderInfo.getTrain().getClass().getDeclaredField(sourceName);
                    field.setAccessible(true);
                    WorkflowRequestTableField[i].setFieldValue((String) field.get(commonOrderInfo.getTrain()));//
                }
                if (hotelFieldSet.contains(sourceName) && !StringUtils.isBlank(hotel.getHotelName())){
                    Field field = commonOrderInfo.getHotel().getClass().getDeclaredField(sourceName);
                    field.setAccessible(true);
                    WorkflowRequestTableField[i].setFieldValue((String) field.get(commonOrderInfo.getHotel()));//
                }
            } catch (Exception e) {
                log.warn("转换字段失败 : {} " , e.getMessage());
            }
        }
    }

    public static void buildFieldsSet(Field[] fields , Set<String> fieldSet){
        if ( fields.length != 0 ){
            for (Field field : fields) {
                fieldSet.add(field.getName());
            }
        }
    }

}
