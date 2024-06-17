package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkConstant;
import com.fenbeitong.openapi.plugin.dingtalk.eia.constant.ApplyTripType;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkProcessFormParserService;
import com.fenbeitong.openapi.plugin.dingtalk.listener.DingTalkBusinessListener;
import com.fenbeitong.openapi.plugin.dingtalk.listener.DingTalkCarListener;
import com.fenbeitong.openapi.plugin.dingtalk.listener.Impl.DingTalkBusinessDefaultListener;
import com.fenbeitong.openapi.plugin.support.apply.constant.TripRoundType;
import com.fenbeitong.openapi.plugin.support.citycode.dto.CityBaseInfo;
import com.fenbeitong.openapi.plugin.support.citycode.service.impl.CityCodeServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenTemplateConfig;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: DingtalkTripApplyFormParserServiceImpl</p>
 * <p>Description: 钉钉行程审批表单解析服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/24 11:06 AM
 */
@SuppressWarnings("all")
@Slf4j
@ServiceAspect
@Service
public class DingtalkTripApplyFormParserServiceImpl implements IDingtalkProcessFormParserService {

    @Value("${host.appgate}")
    private String appgateHost;

    @Autowired
    private ApiUserServiceImpl apiUserService;

    @Autowired
    private DingTalkNoticeServiceImpl dingTalkNoticeService;

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private DingtalkEiaEmployeeServiceImpl dingtalkEiaEmployeeService;

    @Autowired
    private CityCodeServiceImpl cityCodeService;

    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;

    @Override
    public DingtalkTripApplyProcessInfo parse(String corpId, String companyId, int applyType, String instanceId, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        log.info("行程申请表单-{}", JsonUtils.toJson(processInstanceTopVo));
        DingtalkTripApplyProcessInfo processInfo = new DingtalkTripApplyProcessInfo();
        DingtalkTripApplyProcessInfo.ApplyBean apply = genApply(applyType, instanceId, processInstanceTopVo.getTitle());
        processInfo.setApply(apply);

        List<DingtalkTripApplyProcessInfo.TripListBean> tripList = genTripList(companyId, instanceId, processInstanceTopVo);
        List<DingtalkTripApplyProcessInfo.Guest> guestList = buildGuestList(companyId, instanceId, processInstanceTopVo);
        // 如果没有可用行程返回null
        if (ObjectUtils.isEmpty(tripList)) {
            log.info("没有有效的分贝通行程，标记为无效审批单. apply: {}", apply);
            return null;
        }
        processInfo.setTripList(tripList);
        if (!ObjectUtils.isEmpty(guestList)) {
            processInfo.setGuestList(guestList);
        }
        List<DingtalkTripApplyProcessInfo.CustomField> customFields = genCustomFields(corpId, processInstanceTopVo);
        processInfo.setCustomFields(customFields);
        log.info("行程审批数据: {}", JsonUtils.toJson(processInfo));
        return processInfo;
    }

    /**
     * 组装申请单自定义字段
     */
    private List<DingtalkTripApplyProcessInfo.CustomField> genCustomFields(String corpId, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        if (processInstanceTopVo.getTasks() == null || processInstanceTopVo.getTasks().isEmpty()) {
            return null;
        }
        List<DingtalkTripApplyProcessInfo.CustomField> customFieldsList = new ArrayList<>();
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
                dingtalkUser = JsonUtils.toObj(userWithOriginal.getBody(), DingtalkUser.class);
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
        DingtalkTripApplyProcessInfo.CustomField thirdIdCustomField = new DingtalkTripApplyProcessInfo.CustomField();
        thirdIdCustomField.setType("third_code");
        thirdIdCustomField.setValue(processInstanceTopVo.getBusinessId());
        DingtalkTripApplyProcessInfo.CustomField userCustomField = new DingtalkTripApplyProcessInfo.CustomField();
        userCustomField.setType("third_users");
        userCustomField.setValue(userSb.toString());
        customFieldsList.add(thirdIdCustomField);
        customFieldsList.add(userCustomField);
        return customFieldsList;
    }

    /**
     * 组装申请单基础信息
     */
    private DingtalkTripApplyProcessInfo.ApplyBean genApply(int processType, String instanceId, String title) {
        // 设置审批信息
        DingtalkTripApplyProcessInfo.ApplyBean apply = new DingtalkTripApplyProcessInfo.ApplyBean();
        apply.setType(processType);
        //必须为4
        apply.setFlowType(4);
        apply.setThirdId(instanceId);
        apply.setThirdRemark(title);
        apply.setBudget(0);
        return apply;
    }

    /**
     * 组装申请单行程信息
     */
    private List<DingtalkTripApplyProcessInfo.TripListBean> genTripList(String companyId, String instanceId, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        // 行程表单信息
        List<DingtalkTripApplyProcessInfo.TripListBean> tripList = new ArrayList<>();
        List<OapiProcessinstanceGetResponse.FormComponentValueVo> form = processInstanceTopVo.getFormComponentValues();
        boolean formMatch = false;
        String dingtalkUserId = processInstanceTopVo.getOriginatorUserid();
        for (OapiProcessinstanceGetResponse.FormComponentValueVo formComponentValueVo : form) {
            String name = formComponentValueVo.getName();
            if (name == null) {
                continue;
            }
            String jsonForm = formComponentValueVo.getValue();
            // 循环遍历存储行程信息的KEY
            if (!DingTalkConstant.business.FORM_LABEL.equals(name.trim())) {
                continue;
            }
            List lists = JsonUtils.toObj(jsonForm, List.class);
            if (lists.size() >= 2) {
                Map obj = (Map) lists.get(1);
                String jsonValue = (String) obj.get("value");

                // 获取表单数据，遍历行程list
                List values = JsonUtils.toObj(jsonValue, List.class);
                for (Object formDesc : values) {

                    // 如果是汽车行程，则忽略
                    if (isCarTrip((Map) formDesc)) {
                        //log.info("检测到汽车行程，忽略该行程，userId: {}, instanceId: {}", dingtalkUserId, instanceId);
                        continue;
                    }
                    boolean round = this.isRound((Map) formDesc);
                    List rowValues = (List) ((Map) formDesc).get("rowValue");
                    boolean internationalTrip = this.isInternationalTrip(rowValues);
//                    // 创建行程,不是往返，并且不是国际机票行程，则创建单一行程审批
//                    if (!round || !internationalTrip) {
//                        DingtalkTripApplyProcessInfo.TripListBean tripListBean = getTripListBean(companyId, dingtalkUserId, (Map) formDesc, false, instanceId, 1);
//                        if (tripListBean == null) {
//                            return null;
//                        }
//                        tripList.add(tripListBean);
//                    }
//                    // 如果是飞机和火车的往返行程，则再创建一个返程的行程单
//                    ApplyTripType tripType = this.getTripType((Map) formDesc);
//                    boolean returnTrip = round && (tripType == ApplyTripType.TRAIN || tripType == ApplyTripType.AIR);
//                    if (returnTrip) {
//                        //查询用户配置，时间段还是时间点0-精确时间（天） 1-范围时间
//                        String accessToken = dingtalkEiaEmployeeService.getEmployeeFbToken(companyId, dingtalkUserId, "1");
//                        Integer applyDepartureDate = queryApplyDepartureDate(accessToken);
//                        DingtalkTripApplyProcessInfo.TripListBean returnTripListBean = getTripListBean(companyId, dingtalkUserId, (Map) formDesc, true, instanceId, applyDepartureDate);
//                        if (returnTripListBean == null) {
//                            return null;
//                        }
//                        tripList.add(returnTripListBean);
//                    }
                    DingtalkTripApplyProcessInfo.TripListBean tripListBean = getTripListBean(companyId, dingtalkUserId, (Map) formDesc, false, instanceId, 1);
                    if (tripListBean == null) {
                        continue;
                    }
                    if (round) {
                        tripListBean.setTripType(TripRoundType.RoungTrip.getValue());
                    } else {
                        tripListBean.setTripType(TripRoundType.SingleTrip.getValue());
                    }
                    tripList.add(tripListBean);
                }

            } else {
                log.info("解析审批表单数据失败, form: {}", JsonUtils.toJson(processInstanceTopVo));
                throw new FinhubException(300003, "行程审批单解析错误");
            }

            formMatch = true;
        }
        if (!formMatch) {
            log.info("没有找到相应的表单字段, form: {}", JsonUtils.toJson(processInstanceTopVo));
            throw new FinhubException(300003, "行程审批单解析错误:没有找到相应的表单字段");
        }
        return tripList;
    }

    private List<DingtalkTripApplyProcessInfo.Guest> buildGuestList(String companyId, String instanceId, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        // 行程表单信息
        List<DingtalkTripApplyProcessInfo.Guest> guestList = new ArrayList<>();
        try {
            List<OapiProcessinstanceGetResponse.FormComponentValueVo> form = processInstanceTopVo.getFormComponentValues();
            boolean formMatch = false;
            String dingtalkUserId = processInstanceTopVo.getOriginatorUserid();
            for (OapiProcessinstanceGetResponse.FormComponentValueVo formComponentValueVo : form) {
                String name = formComponentValueVo.getName();
                if (name == null) {
                    continue;
                }
                String jsonForm = formComponentValueVo.getValue();
                // 循环遍历存储行程信息的KEY
                if (!DingTalkConstant.business.FORM_LABEL.equals(name.trim())) {
                    continue;
                }
                // 差旅信息
                JSONArray jsonArray = JSONArray.parseArray(jsonForm);
                if (null == jsonArray || jsonArray.size() == 0) {
                    return guestList;
                }
                // 同行人信息
                JSONObject partner = new JSONObject();
                for (Object temp : jsonArray) {
                    if (null == temp) {
                        continue;
                    }
                    JSONObject info = (JSONObject) temp;
                    String type = (String) info.get(DingTalkConstant.partner.COMPONENT_NAME);
                    if (DingTalkConstant.partner.FIELD_TYPE.equals(type)) {
                        partner = info;
                    }
                }
                // 判断是否有属性
                if (null == partner.get(DingTalkConstant.partner.PROPS)) {
                    return guestList;
                }
                JSONObject props = (JSONObject) partner.get(DingTalkConstant.partner.PROPS);
                // 判断是否有名称
                if (null == props.get(DingTalkConstant.partner.LABEL)) {
                    return guestList;
                }
                String lable = (String) props.get(DingTalkConstant.partner.LABEL);
                if (!DingTalkConstant.business.KEY_PARTNER.equals(lable)) {
                    return guestList;
                }
                // 判断是否有扩展字段
                if (null == partner.get(DingTalkConstant.partner.EXT_VALUE)) {
                    return guestList;
                }
                // 获取扩展属性
                String extValueString = (String) partner.get(DingTalkConstant.partner.EXT_VALUE);
                JSONArray extValueJsonArray = JSONArray.parseArray(extValueString);
                for (Object ext : extValueJsonArray) {
                    if (null == ext) {
                        continue;
                    }
                    JSONObject info = (JSONObject) ext;
                    if (null == info) {
                        continue;
                    }
                    Object emp = info.get(DingTalkConstant.partner.EMPL_ID);
                    Object nameInfo = info.get(DingTalkConstant.partner.NAME);
                    DingtalkTripApplyProcessInfo.Guest guest = new DingtalkTripApplyProcessInfo.Guest();
                    guest.setId(null == emp ? "" : emp.toString());
                    guest.setName(null == nameInfo ? "" : nameInfo.toString());
                    // 外部员工填1
                    guest.setEmployeeType(1);
                    guest.setWhetherEmployee(true);
                    guestList.add(guest);
                }
            }
        } catch (Exception e) {
            log.error("获取同行人失败 : {}", e.getMessage());
        }
        return guestList;
    }

    private int queryApplyDepartureDate(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(appgateHost + "/saas/message/setup/apply_config/query", httpHeaders, Maps.newHashMap());
        Map map = JsonUtils.toObj(result, Map.class);
        return map == null ? 1 : map.get("data") == null ? 1 : NumericUtils.obj2int(((Map) map.get("data")).get("apply_departure_date"));
    }


    /**
     * 从表单中获取行程信息
     *
     * @param companyId          公司ID
     * @param formDescMap        表单信息
     * @param returnTrip         是否为返程
     * @param instanceId
     * @param applyDepartureDate 0-精确时间（天） 1-范围时间
     * @return
     */
    private DingtalkTripApplyProcessInfo.TripListBean getTripListBean(String companyId, String dingtalkUserId, Map formDescMap, boolean returnTrip, String instanceId, Integer applyDepartureDate) {
        List rowValues = (List) formDescMap.get("rowValue");
        DingtalkTripApplyProcessInfo.TripListBean tripListBean = null;
        ApplyTripType tripType = this.getTripType(formDescMap);

        if (tripType == null) {
            return null;
        }

        switch (tripType) {
            case AIR:
                boolean internationalTrip = this.isInternationalTrip(rowValues);
                if (internationalTrip) {
                    //加入国际机票审批单创建
                    log.info("开始解析国际航班审批实例");
                    //开始解析具体的审批单国际机票
                    tripListBean = this.createIntelAirTripBean(companyId, rowValues, returnTrip, dingtalkUserId, instanceId, applyDepartureDate);
                    break;
                } else {//国内机票
                    log.info("开始解析国内航班审批实例");
                    tripListBean = this.createAirTripBean(companyId, dingtalkUserId, rowValues, returnTrip, instanceId, applyDepartureDate);
                    break;
                }
            case TRAIN:
                tripListBean = this.createTrainTripBean(companyId, dingtalkUserId, rowValues, returnTrip, instanceId, applyDepartureDate);
                break;
            case HOTEL:
                DingTalkBusinessListener dingTalkBusinessListener = getDingTalkBusinessLister(companyId);
                tripListBean = dingTalkBusinessListener.createHotelTripBean(companyId, dingtalkUserId, rowValues, instanceId);
                break;
            default:
                break;
        }

        return tripListBean;
    }


    /**
     * 获取差旅类型
     *
     * @param formDescMap 行程表单数据
     * @return ApplyTripType {@link ApplyTripType}
     */
    private ApplyTripType getTripType(Map formDescMap) {

        ApplyTripType tripType = null;

        List rowValues = (List) formDescMap.get("rowValue");
        int fieldLength = 6;
        for (int i = 0; i < fieldLength; i++) {
            Map field = (Map) rowValues.get(i);
            String label = (String) field.get("label");
            String value = (String) field.get("value");
            if (DingTalkConstant.business.KEY_APPLY_TYPE.equals(label)) {
                value = value.replaceAll(" ", "");
                switch (value) {
                    case DingTalkConstant.business.VAL_APPLY_TYPE_AIR:
                        tripType = ApplyTripType.AIR;
                        break;
                    case DingTalkConstant.business.VAL_APPLY_TYPE_TRAIN:
                        tripType = ApplyTripType.TRAIN;
                        break;
                    case DingTalkConstant.business.VAL_APPLY_TYPE_HOTEL:
                        tripType = ApplyTripType.HOTEL;
                        break;
                    default:
                        log.info("无法识别的审批单类型， 跳过, tripType: {}", value);
                        break;
                }
                break;
            }
        }
        return tripType;
    }

    /**
     * 判断表单行程是否是汽车行程
     *
     * @return
     */
    private boolean isCarTrip(Map formDescMap) {
        boolean carTrip = false;
        List rowValues = (List) formDescMap.get("rowValue");
        int fieldLength = 6;
        for (int i = 0; i < fieldLength; i++) {
            Map field = (Map) rowValues.get(i);
            String label = (String) field.get("label");
            String value = (String) field.get("value");
            if (DingTalkConstant.business.KEY_APPLY_TYPE.equals(label)
                    && DingTalkConstant.business.VAL_APPLY_TYPE_CAR.equals(value)) {
                carTrip = true;
            }
        }
        return carTrip;
    }

    /**
     * 创建机票行程
     *
     * @param rowValues  行程表单数据
     * @param companyId  企业ID
     * @param returnTrip 是否为返程
     * @return
     */
    private DingtalkTripApplyProcessInfo.TripListBean createAirTripBean(String companyId, String dingtalkUserId, List rowValues, boolean returnTrip, String instanceId, Integer applyDepartureDate) {
        DingtalkTripApplyProcessInfo.TripListBean tripListBean = new DingtalkTripApplyProcessInfo.TripListBean();
        tripListBean.setType(ApplyTripType.AIR.getCode());
        DingTalkBusinessListener dingTalkBusinessListener = getDingTalkBusinessLister(companyId);
        dingTalkBusinessListener.fillTripBeanFields(companyId, rowValues, returnTrip, tripListBean, false, dingtalkUserId, instanceId, applyDepartureDate, ApplyTripType.AIR.getCode());
        return tripListBean;
    }

    /**
     * 创建火车行程
     *
     * @param rowValues  行程表单数据
     * @param companyId  企业ID
     * @param returnTrip 是否为返程
     * @return
     */
    private DingtalkTripApplyProcessInfo.TripListBean createTrainTripBean(String companyId, String dingtalkUserId, List rowValues, boolean returnTrip, String instanceId, Integer applyDepartureDate) {
        DingtalkTripApplyProcessInfo.TripListBean tripListBean = new DingtalkTripApplyProcessInfo.TripListBean();
        tripListBean.setType(ApplyTripType.TRAIN.getCode());
        DingTalkBusinessListener dingTalkBusinessListener = getDingTalkBusinessLister(companyId);
        dingTalkBusinessListener.fillTripBeanFields(companyId, rowValues, returnTrip, tripListBean, false, dingtalkUserId, instanceId, applyDepartureDate, ApplyTripType.TRAIN.getCode());

        return tripListBean;
    }


    /**
     * 创建国际机票行程
     *
     * @param companyId
     * @param rowValues
     * @param returnTrip
     * @return
     */
    private DingtalkTripApplyProcessInfo.TripListBean createIntelAirTripBean(String companyId, List rowValues, boolean returnTrip, String dingtalkUserId, String instanceId, Integer applyDepartureDate) {
        DingtalkTripApplyProcessInfo.TripListBean tripListBean = new DingtalkTripApplyProcessInfo.TripListBean();
        tripListBean.setType(ApplyTripType.INTEL_AIR.getCode());
        log.info("解析国际机票表单 {}", JsonUtils.toJson(rowValues));
        DingTalkBusinessListener dingTalkBusinessListener = getDingTalkBusinessLister(companyId);
        dingTalkBusinessListener.fillIntlAirTripBeanFields(companyId, rowValues, returnTrip, tripListBean, returnTrip, dingtalkUserId, instanceId, applyDepartureDate);
        log.info("国际机票行程审批信息 {}", JsonUtils.toJson(tripListBean));
        return tripListBean;
    }


    /**
     * 是否是国际航班
     *
     * @param rowValues rowValues
     * @return
     */
    private boolean isInternationalTrip(List rowValues) {

        boolean internationalCity = false;
        log.info("检测是否为国际机票审批  ------- 开始 {}", internationalCity);
        int fieldLength = 6;
        for (int i = 0; i < fieldLength; i++) {
            Map field = (Map) rowValues.get(i);
            String label = (String) field.get("label");

            if (DingTalkConstant.business.KEY_START_CITY.equals(label)
                    || DingTalkConstant.business.KEY_ARRIVAL_CITY.equals(label)) {

                Map<String, Object> extendValue = (Map<String, Object>) field.get("extendValue");
                if (extendValue == null) {
                    extendValue = Maps.newHashMap();
                }
                int region = NumericUtils.obj2int(extendValue.get("region"), 0);
                if (region == DingTalkConstant.business.VAL_REGION_INTERNATIONAL) {
                    internationalCity = true;
                }
            }
        }
        log.info("检测是否为国际机票审批  ------- 结束 {}", internationalCity);
        return internationalCity;
    }

    /**
     * 从钉钉机场编码中获取分贝通城市编码
     *
     * @param companyId companyId
     * @param airCode   airCode
     * @return
     */
    private String getCityCodeByAir(String companyId, String airCode) {
        Map<String, CityBaseInfo> airCodeMap = cityCodeService.getAirCode(Lists.newArrayList(airCode));
        CityBaseInfo cityBaseInfo = airCodeMap.get(airCode);
        return cityBaseInfo == null ? null : cityBaseInfo.getId();
    }


    /**
     * 检查是否为往返行程
     *
     * @param formDesc
     * @return
     */
    private boolean isRound(Map formDesc) {
        // 行程表单值，顺序为交通工具/单程往返/出发城市/目的城市/开始时间/结束时间/ ...其他
        List rowValues = (List) formDesc.get("rowValue");
        int fieldLength = 6;
        boolean matched = false;
        for (int i = 0; i < fieldLength; i++) {
            Map field = (Map) rowValues.get(i);
            String label = (String) field.get("label");
            String value = (String) field.get("value");
            if (DingTalkConstant.business.KEY_TRIP_TYPE.equals(label)) {
                matched = true;
                if (DingTalkConstant.business.VAL_TRIP_TYPE_ROUND.equals(value)) {
                    return true;
                }
            }
        }
        if (!matched) {
            log.info("行程审批单:没有找到往返的表单字段, form: {}", JsonUtils.toJson(formDesc));
            throw new FinhubException(300003, "行程审批单:没有找到往返的表单字段");
        }
        return false;
    }


    /**
     * 反射获取监听类
     */
    private DingTalkBusinessListener getDingTalkBusinessLister(String companyId) {
        OpenTemplateConfig openTemplateConfig = openTemplateConfigDao.selectByCompanyId(companyId, 2, 1);
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
                    if (bean != null && bean instanceof DingTalkCarListener) {
                        return ((DingTalkBusinessListener) bean);
                    }
                }
            }
        }
        return SpringUtils.getBean(DingTalkBusinessDefaultListener.class);
    }

    @Data
    static class Component {
        private String componentName;
        private String componentType;
        private List<ExtValue> extValue;
        private Prop props;
        private String value;
    }

    @Data
    static class Prop {
        private String bizAlias;
        private String choice;
        private String holidayOptions;
        private String id;
        private String label;
        private String placeholder;
    }

    @Data
    static class ExtValue {
        private String emplId;
        private String name;
        private String selectDeptName;
        private String avatar;
        private String selectDeptId;
    }

}
