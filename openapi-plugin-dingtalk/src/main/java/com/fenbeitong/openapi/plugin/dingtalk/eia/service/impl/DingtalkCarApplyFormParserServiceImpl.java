package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.IFormFieldAliasConstant;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkCarApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkProcessFormParserService;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkApprovalFormDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvCityDTO;
import com.fenbeitong.openapi.plugin.dingtalk.listener.DingTalkCarListener;
import com.fenbeitong.openapi.plugin.dingtalk.listener.Impl.DingTalkCarDefaultListener;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.citycode.dto.CityBaseInfo;
import com.fenbeitong.openapi.plugin.support.citycode.service.CityCodeService;
import com.fenbeitong.openapi.plugin.support.common.constant.OrderCategoryEnum;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenTemplateConfig;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.openapi.sdk.dto.common.TypeEntity;
import com.google.common.collect.Lists;
import com.luastar.swift.base.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static com.dingtalk.api.response.OapiProcessinstanceGetResponse.FormComponentValueVo;
import static com.dingtalk.api.response.OapiProcessinstanceGetResponse.ProcessInstanceTopVo;
import static com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkCarApplyProcessInfo.UseCarApplyRule;
import static com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo.*;

/**
 * <p>Title: DingtalkCarApplyFormParserServiceImpl</p>
 * <p>Description: 用车表单解析服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/11/27 3:28 PM
 */
@Slf4j
@Component
public class DingtalkCarApplyFormParserServiceImpl implements IDingtalkProcessFormParserService {


    @Autowired
    private ApiUserServiceImpl apiUserService;

    @Autowired
    OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    OpenTemplateConfigDao dingTalkTemplateConfigDao;

    @Autowired
    CityCodeService cityCodeService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Override
    public DingtalkTripApplyProcessInfo parse(String corpId, String companyId, int processType, String instanceId, ProcessInstanceTopVo processInstanceTopVo) {
        DingtalkCarApplyProcessInfo processInfo = new DingtalkCarApplyProcessInfo();
        // 设置审批信息
        ApplyBean apply = new ApplyBean();
        apply.setType(processType);
        //必须为4
        apply.setFlowType(4);
        apply.setThirdId(instanceId);
        apply.setThirdRemark(processInstanceTopVo.getTitle());
        apply.setBudget(0);
        //钉钉用户id
        String dingtalkUserId = processInstanceTopVo.getOriginatorUserid();
        // 行程表单信息
        parseForm(corpId, processInfo, apply, companyId, dingtalkUserId, processInstanceTopVo);
        return processInfo;
    }

    private List<UseCarApplyRule> buildUseCarRuleList(String companyId, String dingtalkUserId) {
        return Lists.newArrayList(
                UseCarApplyRule.builder().type("taxi_scheduling_fee").value(-1).build(),
                UseCarApplyRule.builder().type("allow_same_city").value(false).build(),
                UseCarApplyRule.builder().type("allow_called_for_other").value(true).build()
        );
    }

    private void parseForm(String corpId, DingtalkCarApplyProcessInfo processInfo, ApplyBean apply, String companyId, String dingtalkUserId, ProcessInstanceTopVo processInstanceTopVo) {
        DingtalkTripApplyProcessInfo.TripListBean tripListBean = new TripListBean();
        tripListBean.setType(3);
        List<UseCarApplyRule> useCarApplyRules = buildUseCarRuleList(companyId, dingtalkUserId);
        List<FormComponentValueVo> formComponentList = processInstanceTopVo.getFormComponentValues();
        log.info("钉钉用车申请表单-{}", JsonUtils.toJson(formComponentList));

        DingTalkCarListener dingTalkListener = getDingTalkCarLister(companyId);

        // 钉钉用车模板监听
        dingTalkListener.filterEiaDingTalk(apply, tripListBean, formComponentList, useCarApplyRules, companyId, dingtalkUserId);

        int estimatedAmount = tripListBean.getEstimatedAmount();
        boolean limitPrice = estimatedAmount != 0;
        if (limitPrice) {
            useCarApplyRules.add(UseCarApplyRule.builder().type("price_limit_flag").value(2).build());
            useCarApplyRules.add(UseCarApplyRule.builder().type("total_price").value(estimatedAmount).build());
        }
        processInfo.setTripList(Lists.newArrayList(tripListBean));
        processInfo.setApplyTaxiRuleInfo(useCarApplyRules);
        processInfo.setApply(apply);
        List<CustomField> customFieldList = genCustomFields(corpId, processInstanceTopVo);
        processInfo.setCustomFields(customFieldList);
    }


    /**
     * 组装申请单自定义字段
     */
    private List<CustomField> genCustomFields(String corpId, ProcessInstanceTopVo processInstanceTopVo) {
        if (processInstanceTopVo.getTasks() == null || processInstanceTopVo.getTasks().isEmpty())
            return null;
        List<CustomField> customFieldsList = new ArrayList<>();
        StringBuilder userSb = new StringBuilder();
        for (OapiProcessinstanceGetResponse.TaskTopVo task : processInstanceTopVo.getTasks()) {
            if (task.getUserid() == null || task.getUserid().isEmpty()) {
                continue;
            }
            if (!"COMPLETED".equals(task.getTaskStatus())) {

                continue;
            }
            DingtalkUser dingtalkUser = null;
            try {
                OapiUserGetResponse userWithOriginal = apiUserService.getUserWithOriginal(corpId, task.getUserid());
                dingtalkUser = com.fenbeitong.openapi.plugin.util.JsonUtils.toObj(userWithOriginal.getBody(), DingtalkUser.class);
            } catch (Exception e) {
                log.error("查询钉钉用户详情失败：", e);
            }
            if (dingtalkUser == null) {
                continue;
            }
            if (userSb.length() > 0) {
                userSb.append(",");
            }
            userSb.append(dingtalkUser.getName());
        }
        CustomField thirdIdCustomField = new CustomField();
        thirdIdCustomField.setType("third_code");
        thirdIdCustomField.setValue(processInstanceTopVo.getBusinessId());
        CustomField userCustomField = new CustomField();
        userCustomField.setType("third_users");
        userCustomField.setValue(userSb.toString());
        customFieldsList.add(thirdIdCustomField);
        customFieldsList.add(userCustomField);
        return customFieldsList;
    }


    /**
     * 反射获取监听类
     */
    private DingTalkCarListener getDingTalkCarLister(String companyId) {
        OpenTemplateConfig dingTalkTemplateConfig = dingTalkTemplateConfigDao.selectByCompanyId(companyId, 1, 1);
        if (dingTalkTemplateConfig != null) {
            String className = dingTalkTemplateConfig.getListenerClass();
            if (!ObjectUtils.isEmpty(className)) {
                Class clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (clazz != null) {
                    Object bean = SpringUtils.getBean(clazz);
                    if (bean != null && bean instanceof DingTalkCarListener) {
                        return ((DingTalkCarListener) bean);
                    }
                }
            }
        }
        return SpringUtils.getBean(DingTalkCarDefaultListener.class);
    }

    /**
     * 钉钉ISV用车表单转换
     * @param bizData
     * @param instanceId
     * @return
     */
    public CommonApplyReqDTO parseDingtalkIsvCarForm(String bizData, String instanceId) {
        DingtalkApprovalFormDTO dingtalkIsvCarApprovalFormDTO = JsonUtils.toObj(bizData, DingtalkApprovalFormDTO.class);
        List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList = dingtalkIsvCarApprovalFormDTO.getFormValueVOS();
        String applyReason = "";
        String applyBeginDate = "";
        String applyEndDate = "";
        int carUseCount = 0;
        int carAmount = 0;
        List<String> cityNameList = Lists.newArrayList();
        for (DingtalkApprovalFormDTO.FormValueVOS formvos : formValueVOSList) {
            String formName = formvos.getName();
            String formValue = formvos.getValue();
            if (DingTalkConstant.car.KEY_APPLY_REASON.equals(formName)) {
                applyReason = formValue;//申请原因
            } else if (DingTalkConstant.car.KEY_START_END_TIME.equals(formName)) {
                Map<String, String> timeMap = setTime(formValue);//开始时间和结束时间
                applyBeginDate = timeMap.get("startTime");
                applyEndDate = timeMap.get("endTime");
            } else if (DingTalkConstant.car.KEY_TRIP_COUNT.equals(formName)) {
                carUseCount = NumericUtils.obj2int(formValue);//用车次数
            } else if (DingTalkConstant.car.KEY_TRIP_FEE.equals(formName)) {
                carAmount = NumericUtils.obj2int(formValue);//用车金额
                //用车城市
            } else if (StringUtils.isBlank(formName) && !CollectionUtils.isBlank(formvos.getDetails())) {
                List<DingtalkApprovalFormDTO.CarCity> details = formvos.getDetails();
                for (DingtalkApprovalFormDTO.CarCity cc : details) {
                    List<DingtalkApprovalFormDTO.FormValueVOS> cityDetail = cc.getDetails();
                    for (int i = 0; i < cityDetail.size(); i++) {
                        if (i > 9) {
                            break;
                        }
                        DingtalkApprovalFormDTO.FormValueVOS fv = cityDetail.get(i);
                        if (DingTalkConstant.car.KEY_CITY.equals(fv.getName())) {
                            //获取省市区的列表（名称、编码）
                            String extValue = fv.getExtValue();
                            DingtalkIsvCityDTO dingtalkIsvCityDTO = JsonUtils.toObj(extValue, DingtalkIsvCityDTO.class);
                            String name = dingtalkIsvCityDTO.getCity().getName();//城市名称
                            cityNameList.add(name);
                        }
                    }
                }
            }
        }

        String cityList = getCityList(cityNameList);
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        //构建公用审批数据
        CommonApply commonApply = new CommonApply();
        commonApply.setApplyReason(applyReason);
        commonApply.setThirdRemark(applyReason);
        commonApply.setApplyReasonDesc(applyReason);
        commonApply.setThirdId(instanceId);
        commonApply.setType(SaasApplyType.ApplyTaxi.getValue());
        commonApply.setFlowType(4);
        commonApplyReqDTO.setApply(commonApply);
        CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
        commonApplyTrip.setType(OrderCategoryEnum.Taxi.getKey());
        commonApplyTrip.setStartCityId(cityList);
        //设置日期格式
        commonApplyTrip.setStartTime(applyBeginDate);
        commonApplyTrip.setEndTime(applyEndDate);
        List<CommonApplyTrip> commonApplyTripList = new ArrayList<>();
        commonApplyTripList.add(commonApplyTrip);
        commonApplyReqDTO.setTripList(commonApplyTripList);
        ArrayList<TypeEntity> taxiRuleList = commonApplyService.initTaxiRule(carUseCount, carAmount);
        commonApplyReqDTO.setApplyTaxiRuleInfo(taxiRuleList);
        return commonApplyReqDTO;
    }

    private Map<String, String> setTime(String startAndEndTime) {
        Map<String, String> map = new HashMap<String, String>();
        String startTime = startAndEndTime.substring(startAndEndTime.indexOf("[") + 2, startAndEndTime.indexOf(",") - 1);
        String endTime = startAndEndTime.substring(startAndEndTime.indexOf(",") + 2, startAndEndTime.lastIndexOf(",") - 1);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return map;

    }

    //用车城市编码
    public String getCityList(List<String> cityNameList) {
        Map<String, CityBaseInfo> carCode = cityCodeService.getCarCode(cityNameList);
        List<String> cityIdList = new ArrayList<>();
        for (String cityName : cityNameList) {
            cityIdList.add(carCode.get(cityName).getId());
        }
        HashSet set = new HashSet(cityIdList);
        cityIdList.clear();
        cityIdList.addAll(set);
        if (!ObjectUtils.isEmpty(cityIdList)) {
            return String.join(",", cityIdList);
        }
        return "";
    }


    /**
     * 钉钉ISV用车套件表单解析
     * @param bizData
     * @param instanceId
     * @return
     */
    public CommonApplyReqDTO parseDingtalkIsvCarKitForm(String bizData, String instanceId) {
        DingtalkApprovalFormDTO dingtalkIsvCarApprovalFormDTO = JsonUtils.toObj(bizData, DingtalkApprovalFormDTO.class);
        List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList = dingtalkIsvCarApprovalFormDTO.getFormValueVOS();
        //构建公用审批数据
        CommonApply commonApply = new CommonApply();
        CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
        commonApplyTrip.setType(OrderCategoryEnum.Taxi.getKey());
        List<CommonApplyTrip> commonApplyTripList = new ArrayList<>();
        Map<String , Object> carMap =  new HashMap<>();
        List<CostAttributionDTO> costAttributionList =  new ArrayList<>();
        formValueVOSList.forEach( formValueVOS ->  {
            String bizAlias = formValueVOS.getBizAlias();
            switch (bizAlias){
                case IFormFieldAliasConstant.CAR_LEAVE_TYEP://申请事由
                    commonApply.setApplyReason( formValueVOS.getValue() );
                    break;
                case IFormFieldAliasConstant.CAR_SUBTEXTAREA_FIELD://事由补充
                    String reasonDesc = formValueVOS.getValue();
                    commonApply.setApplyReasonDesc(reasonDesc);
                    commonApply.setThirdRemark(reasonDesc);
                    break;
                case IFormFieldAliasConstant.CAR_CITY://用车城市
                    String cityExtendValue = formValueVOS.getExtValue();
                    commonApplyTrip.setStartCityId( getCityList( cityExtendValue ) );
                    break;
                case IFormFieldAliasConstant.CAR_TIME_SECTION://开始时间和结束时间
                    //设置日期格式
                    String cityValue = StringEscapeUtils.unescapeJava( formValueVOS.getValue() );
                    List<String> timtList = com.fenbeitong.openapi.plugin.util.JsonUtils.toObj(cityValue, List.class , String.class);
                    if(!CollectionUtils.isBlank( timtList ) && timtList.size()>=2){
                        commonApplyTrip.setStartTime( timtList.get(0) );
                        commonApplyTrip.setEndTime( timtList.get(1) );
                    }
                    break;
                case IFormFieldAliasConstant.CAR_CAR_FREQUENCY://用车次数
                    carMap.put("carCount" ,  formValueVOS.getValue() );
                    break;
                case IFormFieldAliasConstant.CAR_MONEY_FIELD://用车金额
                    carMap.put("carAmount" ,  formValueVOS.getValue() );
                    break;
                case IFormFieldAliasConstant.CAR_COST_DEPARTMENT_FIELD://费用归属部门
                    getCostDeaprtmentList( formValueVOS.getExtValue() , costAttributionList);
                    break;
                case IFormFieldAliasConstant.CAR_COST_PROJECT_FIELD://费用归属项目
                    getCostProjectList( formValueVOS.getExtValue() , costAttributionList);
                    break;
                default:
                    break;
            }
        });

        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        //构建公用审批数据
        commonApply.setThirdId(instanceId);
        commonApply.setType(SaasApplyType.ApplyTaxi.getValue());
        commonApply.setFlowType(4);
        commonApply.setCostAttributionList(costAttributionList);
        commonApplyReqDTO.setApply(commonApply);
        commonApplyTripList.add(commonApplyTrip);
        commonApplyReqDTO.setTripList(commonApplyTripList);
        ArrayList<TypeEntity> taxiRuleList = commonApplyService.initTaxiRule(NumericUtils.obj2int( carMap.get("carCount") ), NumericUtils.obj2int( carMap.get("carAmount") ));
        commonApplyReqDTO.setApplyTaxiRuleInfo(taxiRuleList);
        return commonApplyReqDTO;
    }

    private String getCityList(String cityExtendValue){
        List<String> cityIdList = new ArrayList<>();
        String cityValue = StringEscapeUtils.unescapeJava(cityExtendValue);
        List<Map> list = com.fenbeitong.openapi.plugin.util.JsonUtils.toObj(cityValue, List.class , Map.class);
        list.forEach( city -> {
            String area = StringUtils.obj2str( city.get("area") );
            cityIdList.add(area);
        });
        if (!ObjectUtils.isEmpty(cityIdList)) {
            return String.join(",", cityIdList);
        }
        return "";
    }

    private void getCostDeaprtmentList(String costProjectExtendValue ,List<CostAttributionDTO> costAttributionList){
        String costDepartmentValue = StringEscapeUtils.unescapeJava(costProjectExtendValue);
        List<Map> list = com.fenbeitong.openapi.plugin.util.JsonUtils.toObj(costDepartmentValue, List.class , Map.class);
        if(!CollectionUtils.isBlank(list)){
            list.forEach( city -> {
                CostAttributionDTO costAttributionDTO = new CostAttributionDTO();
                costAttributionDTO.setCostAttributionId(StringUtils.obj2str( city.get("id") ));
                costAttributionDTO.setCostAttributionName(StringUtils.obj2str( city.get("name") ));
                costAttributionDTO.setCostAttributionCategory(1);
                costAttributionList.add(costAttributionDTO);
            });
        }
    }

    private void getCostProjectList(String costProjectExtendValue ,List<CostAttributionDTO> costAttributionList){
        String costProjectValue = StringEscapeUtils.unescapeJava(costProjectExtendValue);
        List<Map> list = com.fenbeitong.openapi.plugin.util.JsonUtils.toObj(costProjectValue, List.class , Map.class);
        if(!CollectionUtils.isBlank(list)){
            list.forEach( project -> {
                CostAttributionDTO costAttributionDTO = new CostAttributionDTO();
                costAttributionDTO.setCostAttributionId(StringUtils.obj2str( project.get("id") ));
                costAttributionDTO.setCostAttributionName(StringUtils.obj2str( project.get("name") ));
                costAttributionDTO.setCostAttributionCategory(2);
                costAttributionList.add(costAttributionDTO);
            });
        }
    }

}
