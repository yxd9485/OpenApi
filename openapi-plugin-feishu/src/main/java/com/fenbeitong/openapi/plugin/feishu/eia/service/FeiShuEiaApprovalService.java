package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuUserInfoDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuApprovalService;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuEmployeeService;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.common.listener.FeiShuApprovalListener;
import com.fenbeitong.openapi.plugin.feishu.common.listener.FeiShuApprovalDefaultListener;
import com.fenbeitong.openapi.plugin.feishu.eia.util.FeiShuEiaHttpUtils;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyGuest;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyTrip;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenTemplateConfig;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.sdk.dto.common.TypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
@Deprecated
public class FeiShuEiaApprovalService extends AbstractFeiShuApprovalService {
    @Autowired
    private FeiShuEiaHttpUtils feiShuEiaHttpUtils;
    @Autowired
    private FeiShuEiaEmployeeService feiShuEiaEmployeeService;
    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;


    @Override
    protected AbstractFeiShuHttpUtils getFeiShuHttpUtils() {
        return feiShuEiaHttpUtils;
    }

    @Override
    protected AbstractFeiShuEmployeeService getFeiShuEmployeeService() {
        return feiShuEiaEmployeeService;
    }

    /**
     * 飞书审批使用自定义字段组装差旅套件
     *
     * @param corpId
     * @param approvalId
     * @param form
     * @param type
     * @return
     */
    public CommonApplyReqDTO parseFeiShuApprovalForm(String corpId, String approvalId, String form, int type) {
        String jsonForm = form.replaceAll("\\\\", "");
        List<Map> list = JsonUtils.toObj(jsonForm, List.class);
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        List<CommonApplyTrip> tripList = new ArrayList();
        if (1 == type) {//差旅
            String applyReason = "";
            int applySingle = 1;
            String applyDeparture = "";
            String applyDestination = "";
            List<String> applyCompanions = new ArrayList<>();
            String applyBeginDate = "";
            String applyEndDate = "";
            List<String> tripTypeList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Map formMap = list.get(i);
                Object formName = formMap.get("name");
                Object value = formMap.get("value");
                if (FeiShuConstant.APPROVAL_FORM_REASON.equals(formName)) {
                    applyReason = (String) value;
                } else if (FeiShuConstant.APPROVAL_FORM_TRIP_TYPE.equals(formName)) {//交通工具
                    tripTypeList = (List) value;
                } else if (FeiShuConstant.APPROVAL_FORM_DEPATURE.equals(formName)) {//出发地
                    applyDeparture = ((String) value).trim();
                } else if (FeiShuConstant.APPROVAL_FORM_DESTINATION.equals(formName)) {//目的地
                    applyDestination = ((String) value).trim();
                } else if (FeiShuConstant.APPROVAL_FORM_COMPANION.equals(formName)) {//同行人
                    applyCompanions = (List) value;
                } else if (FeiShuConstant.APPROVAL_FORM_SINGLE.equals(formName)) {//单程往返
                    if ("往返".equals(value)) {//默认单程
                        applySingle = 2;
                    }
                } else if (FeiShuConstant.APPROVAL_FORM_DATE.equals(formName)) {//行程日期
                    Map dateMap = (Map) value;
                    applyBeginDate = (String) dateMap.get("start");
                    applyEndDate = (String) dateMap.get("end");
                }
            }

            CommonApply commonApply = new CommonApply();
            commonApply.setApplyReason(applyReason);
            commonApply.setApplyReasonDesc(applyReason);
            commonApply.setThirdRemark(applyReason);
            commonApply.setThirdId(approvalId);
            commonApply.setType(1);
            commonApply.setFlowType(4);
            commonApply.setBudget(0);
            commonApplyReqDTO.setApply(commonApply);
            //获取同行人信息
            List<CommonApplyGuest> guests = new ArrayList<>();
            for (String companion : applyCompanions) {
                //调用飞书查询人员数据
                FeiShuUserInfoDTO userInfo = feiShuEiaEmployeeService.getUserInfo(FeiShuConstant.ID_TYPE_EMPLOYEE_ID, companion, corpId);
                if (!ObjectUtils.isEmpty(userInfo)) {//调用飞书查询查询用户详情
                    String name = userInfo.getName();
                    String mobile = userInfo.getMobile();
                    CommonApplyGuest guest = new CommonApplyGuest();
                    guest.setId(companion);
                    guest.setName(name);
                    guest.setPhoneNum(mobile);
                    guest.setIsEmployee(true);
                    guests.add(guest);
                }
            }
            applyBeginDate = applyBeginDate.substring(0, applyBeginDate.indexOf("T"));
            applyEndDate = applyEndDate.substring(0, applyEndDate.indexOf("T"));

            for (String trip : tripTypeList) {
                //具体交通工具类型
                String tripType = null;
                if (trip.equals("国内机票")) {
                    tripType = "7";
                } else if (trip.equals("国际机票")) {
                    tripType = "40";
                } else if (trip.equals("火车")) {
                    tripType = "15";
                } else if (trip.equals("酒店")) {
                    tripType = "11";
                }
                CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
                commonApplyTrip.setType(Integer.valueOf(tripType));
                commonApplyTrip.setTripType(applySingle);
                commonApplyTrip.setEstimatedAmount(0);
                if ("11".equals(tripType)) {//酒店，城市全部取目的城市
                    commonApplyTrip.setStartCityName(applyDestination);
                } else {
                    commonApplyTrip.setStartCityName(applyDeparture);
                }
                commonApplyTrip.setArrivalCityName(applyDestination);
                commonApplyTrip.setStartTime(applyBeginDate);
                commonApplyTrip.setEndTime(applyEndDate);
                commonApplyTrip.setEstimatedAmount(0);
                tripList.add(commonApplyTrip);
            }
            commonApplyReqDTO.setTripList(tripList);
            commonApplyReqDTO.setGuestList(guests);
        } else {//用车
            String applyReason = "";
            String applyBeginDate = "";
            String applyEndDate = "";
            String applyCarCity = "";
            int carUseCount = 0;
            Object carAmount = 0;
            for (int i = 0; i < list.size(); i++) {
                Map formMap = list.get(i);
                Object formName = formMap.get("name");
                Object value = formMap.get("value");
                if (FeiShuConstant.APPROVAL_FORM_REASON.equals(formName)) {//申请事由
                    applyReason = (String) value;
                } else if (FeiShuConstant.APPROVAL_FORM_CAR_CITY.equals(formName)) {//用车城市
                    applyCarCity = (String) value;
                } else if (FeiShuConstant.APPROVAL_FORM_DATE.equals(formName)) {//行程日期
                    Map dateMap = (Map) value;
                    applyBeginDate = (String) dateMap.get("start");
                    applyEndDate = (String) dateMap.get("end");
                } else if (FeiShuConstant.APPROVAL_FORM_CAR_USE_COUNT.equals(formName)) {
                    carUseCount = (int) value;
                } else if (FeiShuConstant.APPROVAL_FORM_CAR_AMOUNT.equals(formName)) {
                    carAmount = value;
                }
            }
            //构建公用审批数据
            CommonApply commonApply = new CommonApply();
            commonApply.setApplyReason(applyReason);
            commonApply.setThirdRemark(applyReason);
            commonApply.setApplyReasonDesc(applyReason);
            commonApply.setThirdId(approvalId);
            commonApply.setType(12);
            commonApply.setFlowType(4);
            commonApplyReqDTO.setApply(commonApply);
            CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
            commonApplyTrip.setType(3);
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


            ArrayList<TypeEntity> taxiRuleList = new ArrayList<TypeEntity>();
            TypeEntity typeEntity = new TypeEntity();
            typeEntity.setType("taxi_scheduling_fee");
            typeEntity.setValue("-1");
            TypeEntity typeEntity1 = new TypeEntity();
            typeEntity1.setType("allow_same_city");
            typeEntity1.setValue("false");
            TypeEntity typeEntity2 = new TypeEntity();
            typeEntity2.setType("allow_called_for_other");
            typeEntity2.setValue("true");
            TypeEntity typeEntity3 = new TypeEntity();
            typeEntity3.setType("price_limit");
            typeEntity3.setValue("-1");
            TypeEntity typeEntity4 = new TypeEntity();
            typeEntity4.setType("day_price_limit");
            typeEntity4.setValue("-1");
            TypeEntity typeEntity5 = new TypeEntity();
            typeEntity5.setType("times_limit_flag");
            typeEntity5.setValue("2");
            TypeEntity typeEntity6 = new TypeEntity();
            //设置使用次数
            typeEntity6.setType("times_limit");
            typeEntity6.setValue(carUseCount);
            TypeEntity typeEntity7 = new TypeEntity();
            //设置金额
            typeEntity7.setType("total_price");
            typeEntity7.setValue(carAmount);
            TypeEntity typeEntity8 = new TypeEntity();
            typeEntity8.setType("city_limit");
            typeEntity8.setValue("1");
            TypeEntity typeEntity9 = new TypeEntity();
            typeEntity9.setType("price_limit_flag");
            typeEntity9.setValue("2");

            taxiRuleList.add(typeEntity);
            taxiRuleList.add(typeEntity1);
            taxiRuleList.add(typeEntity2);
            taxiRuleList.add(typeEntity3);
            taxiRuleList.add(typeEntity4);
            taxiRuleList.add(typeEntity5);
            taxiRuleList.add(typeEntity6);
            taxiRuleList.add(typeEntity7);
            taxiRuleList.add(typeEntity8);
            taxiRuleList.add(typeEntity9);

            commonApplyReqDTO.setApplyTaxiRuleInfo(taxiRuleList);
        }
        return commonApplyReqDTO;
    }

    /**
     * 飞书审批使用自带出差套件
     *
     * @param corpId
     * @param approvalId
     * @param form
     * @param type
     * @return
     */
    public CommonApplyReqDTO parseFeiShuTripApprovalForm(String companyId, String corpId, String approvalId, String form, int type,String thirdEmployeeId) {
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();

        FeiShuApprovalListener feiShuApprovalListener = getFeiShuApprovalLister(companyId);
        if (1 == type) {//差旅
            commonApplyReqDTO = feiShuApprovalListener.parseFeiShuBusinessForm(companyId, corpId, approvalId, form);
        } else {//用车
            commonApplyReqDTO = feiShuApprovalListener.parseFeiShuCarForm(companyId, corpId, approvalId, form , thirdEmployeeId);
        }
        commonApplyReqDTO.getApply().setCompanyId(companyId);
        return commonApplyReqDTO;
    }

    /**
     * 反射获取监听类
     */
    private FeiShuApprovalListener getFeiShuApprovalLister(String companyId) {
        OpenTemplateConfig openTemplateConfig = openTemplateConfigDao.selectByCompanyId(companyId, 0, 3);
        if (openTemplateConfig != null) {
            String className = openTemplateConfig.getListenerClass();
            if (!ObjectUtils.isEmpty(className)) {
                Class clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (clazz != null) {
                    Object bean = SpringUtils.getBean(clazz);
                    if (bean != null && bean instanceof FeiShuApprovalListener) {
                        return ((FeiShuApprovalListener) bean);
                    }
                }
            }
        }
        return SpringUtils.getBean(FeiShuApprovalDefaultListener.class);
    }

    public static void main(String[] args) {
        String s = "北市京市";
        if (s.endsWith("市")) {
            s = s.substring(0, s.length() - 1);
        }
        System.out.println(s);
    }
}
