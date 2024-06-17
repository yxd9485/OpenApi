package com.fenbeitong.openapi.plugin.func.employee.service;

import com.fenbeitong.openapi.plugin.rpc.api.func.model.EmployeeDefaultAuthDto;
import com.fenbeitong.openapi.plugin.rpc.api.func.service.IEmployeeDefaultAuthService;
import com.fenbeitong.openapi.plugin.support.privilege.dao.OpenEmployeePrivSupportDao;
import com.fenbeitong.openapi.plugin.support.privilege.dao.OpenEmployeeRuleTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.privilege.entity.OpenEmployeePriv;
import com.fenbeitong.openapi.plugin.support.privilege.entity.OpenEmployeeRuleTemplateConfig;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.rule.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: EmployeeDefaultAuthServiceImpl</p>
 * <p>Description: 员工默认权限 服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/7/21 1:50 PM
 */
@Slf4j
@Component
@DubboService(timeout = 15000)
public class EmployeeDefaultAuthServiceImpl implements IEmployeeDefaultAuthService {

    @Autowired
    private OpenEmployeePrivSupportDao employeePrivSupportDao;

    @Autowired
    private OpenEmployeeRuleTemplateConfigDao employeeRuleTemplateConfigDao;

    @Override
    public List<EmployeeDefaultAuthDto> listEmployeeDefaultAuth(String companyId) {
        Map<String, Object> conditonMap = Maps.newHashMap();
        conditonMap.put("companyId", companyId);
        List<OpenEmployeePriv> openEmployeePrivList = employeePrivSupportDao.listOpenEmployeePriv(conditonMap);
        if (ObjectUtils.isEmpty(openEmployeePrivList)) {
            return Lists.newArrayList();
        }
        Map<Long, List<OpenEmployeePriv>> employeePrivMap = openEmployeePrivList.stream().collect(Collectors.groupingBy(OpenEmployeePriv::getRoleType));
        List<EmployeeDefaultAuthDto> authList = Lists.newArrayList();
        employeePrivMap.forEach((roleType, privList) -> {
            if (!ObjectUtils.isEmpty(privList) && roleType != 0) {
                //场景权限
                Map<String, OpenEmployeePriv> sceneAuthMap = privList.stream().collect(Collectors.toMap(OpenEmployeePriv::getScene, Function.identity(), (o, n) -> n));
                EmployeeDefaultAuthDto employeeDefaultAuthDto = new EmployeeDefaultAuthDto();
                employeeDefaultAuthDto.setRoleType(roleType.intValue());
                employeeDefaultAuthDto.setAir_policy(getAirPolicy(sceneAuthMap.get("air")));
                employeeDefaultAuthDto.setIntl_air_policy(getIntlAirPolicy(sceneAuthMap.get("intl_air")));
                employeeDefaultAuthDto.setHotel_policy(getHotelPolicy(sceneAuthMap.get("hotel")));
                employeeDefaultAuthDto.setCar_policy(getCarPolicy(sceneAuthMap.get("car")));
                employeeDefaultAuthDto.setTrain_policy(getTrainPolicy(sceneAuthMap.get("train")));
                employeeDefaultAuthDto.setMall_policy(getMallPolicy(sceneAuthMap.get("mall")));
                employeeDefaultAuthDto.setTakeaway_policy(getTakeawayPolicy(sceneAuthMap.get("takeaway")));
                employeeDefaultAuthDto.setDinners_policy(getDinnersPolicy(sceneAuthMap.get("dinners")));
                employeeDefaultAuthDto.setShansong_policy(getShansongPolicy(sceneAuthMap.get("shansong")));
                employeeDefaultAuthDto.setShunfeng_policy(getShunfengPolicy(sceneAuthMap.get("shunfeng")));
                employeeDefaultAuthDto.setPayment_apply_policy(getPaymentApplyPolicy(sceneAuthMap.get("payment_apply")));
                employeeDefaultAuthDto.setVirtual_card_policy(getVirtualCardPolicy(sceneAuthMap.get("virtual_card")));
                employeeDefaultAuthDto.setMileage_policy(getMileagePolicy(sceneAuthMap.get("mileage")));
                employeeDefaultAuthDto.setBus_policy(getBusPolicy(sceneAuthMap.get("bus")));
                authList.add(employeeDefaultAuthDto);
            }
        });
        return authList;
    }


    private AirPolicyBean getAirPolicy(OpenEmployeePriv air) {
        if (air == null || ObjectUtils.isEmpty(air.getPrivJsonData())) {
            return getDefaultAirPolicy();
        }
        String privJsonData = air.getPrivJsonData();
        log.info("清洗国内机票历史模板数据, companyId={},roleType={},privJson={}", air.getCompanyId(), air.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        AirPolicyBean airPolicyBean = new AirPolicyBean();
        airPolicyBean.setSwitch_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("switch_flag"))));
        airPolicyBean.setRule_limit_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("air_rule_limit_flag"))));
        airPolicyBean.setRule_id(StringUtils.obj2str(jsonMap.get("air_rule_id")));
        airPolicyBean.setAir_verify_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("air_verify_flag"))));
        airPolicyBean.setAir_order_verify_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("air_order_verify_flag"))));
        //1 仅允许本人预订 2 允许本人预订，允许为其他员工预订 3 允许本人预订，允许为其他员工预订，非企业员工预订标识
        airPolicyBean.setAir_priv_type((Integer) jsonMap.get("air_priv_type"));
        airPolicyBean.setExceed_buy_type(NumericUtils.obj2int(jsonMap.get("exceed_buy_type"), 1));
        airPolicyBean.setRefund_ticket_type((Integer) jsonMap.get("refund_ticket_type"));
        airPolicyBean.setChanges_ticket_type((Integer) jsonMap.get("changes_ticket_type"));
        airPolicyBean.setOneself_limit((Integer) jsonMap.get("oneself_limit"));
        log.info("清洗国内机票历史模板数据, companyId={},roleType={},privJson={},airPolicyBean={}", air.getCompanyId(), air.getRoleType(), privJsonData, JsonUtils.toJson(airPolicyBean));
        return airPolicyBean;
    }

    private AirPolicyBean getDefaultAirPolicy() {
        AirPolicyBean airPolicyBean = new AirPolicyBean();
        airPolicyBean.setSwitch_flag(false);
        airPolicyBean.setRule_limit_flag(false);
        airPolicyBean.setAir_verify_flag(false);
        airPolicyBean.setAir_order_verify_flag(false);
        //1 仅允许本人预订 2 允许本人预订，允许为其他员工预订 3 允许本人预订，允许为其他员工预订，非企业员工预订标识
        airPolicyBean.setAir_priv_type(1);
        airPolicyBean.setExceed_buy_type(1);
        airPolicyBean.setRefund_ticket_type(0);
        airPolicyBean.setChanges_ticket_type(0);
        airPolicyBean.setOneself_limit(0);
        return airPolicyBean;
    }

    private IntlAirPolicyBean getIntlAirPolicy(OpenEmployeePriv intlAir) {
        if (intlAir == null || ObjectUtils.isEmpty(intlAir.getPrivJsonData())) {
            return getDefaultIntlAirPolicy();
        }
        String privJsonData = intlAir.getPrivJsonData();
        log.info("清洗国际机票历史模板数据, companyId={},roleType={},privJson={}", intlAir.getCompanyId(), intlAir.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        IntlAirPolicyBean intlAirPolicyBean = new IntlAirPolicyBean();
        intlAirPolicyBean.setSwitch_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("switch_flag"))));
        intlAirPolicyBean.setRule_limit_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("air_rule_limit_flag"))));
        intlAirPolicyBean.setRule_id(StringUtils.obj2str(jsonMap.get("air_rule_id")));
        intlAirPolicyBean.setAir_verify_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("air_verify_flag"))));
        intlAirPolicyBean.setIntl_air_order_verify_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("air_order_verify_flag"))));
        //1 仅允许本人预订 2 允许本人预订，允许为其他员工预订 3 允许本人预订，允许为其他员工预订，非企业员工预订标识
        intlAirPolicyBean.setAir_priv_type((Integer) jsonMap.get("air_priv_type"));
        intlAirPolicyBean.setExceed_buy_type(NumericUtils.obj2int(jsonMap.get("exceed_buy_type"), 1));
        intlAirPolicyBean.setOneself_limit((Integer) jsonMap.get("oneself_limit"));
        log.info("清洗国际机票历史模板数据, companyId={},roleType={},privJson={},intlAirPolicyBean={}", intlAir.getCompanyId(), intlAir.getRoleType(), privJsonData, JsonUtils.toJson(intlAirPolicyBean));
        return intlAirPolicyBean;
    }

    private IntlAirPolicyBean getDefaultIntlAirPolicy() {
        IntlAirPolicyBean intlAirPolicyBean = new IntlAirPolicyBean();
        intlAirPolicyBean.setSwitch_flag(false);
        intlAirPolicyBean.setRule_limit_flag(false);
        intlAirPolicyBean.setAir_verify_flag(false);
        intlAirPolicyBean.setIntl_air_order_verify_flag(false);
        //1 仅允许本人预订 2 允许本人预订，允许为其他员工预订 3 允许本人预订，允许为其他员工预订，非企业员工预订标识
        intlAirPolicyBean.setAir_priv_type(1);
        intlAirPolicyBean.setExceed_buy_type(1);
        intlAirPolicyBean.setOneself_limit(0);
        return intlAirPolicyBean;
    }

    private HotelPolicyBean getHotelPolicy(OpenEmployeePriv hotel) {
        if (hotel == null || ObjectUtils.isEmpty(hotel.getPrivJsonData())) {
            return getDefaultHotelPolicy();
        }
        String privJsonData = hotel.getPrivJsonData();
        log.info("清洗酒店历史模板数据, companyId={},roleType={},privJson={}", hotel.getCompanyId(), hotel.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        HotelPolicyBean hotelPolicyBean = new HotelPolicyBean();
        hotelPolicyBean.setSwitch_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("switch_flag"))));
        hotelPolicyBean.setRule_limit_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("hotel_rule_limit_flag"))));
        hotelPolicyBean.setRule_id(StringUtils.obj2str(jsonMap.get("hotel_rule_id")));
        hotelPolicyBean.setHotel_verify_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("hotel_verify_flag"))));
        hotelPolicyBean.setHotel_order_verify_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("hotel_order_verify_flag"))));
        //1 仅允许本人预订 2 允许本人预订，允许为其他员工预订 3 允许本人预订，允许为其他员工预订，非企业员工预订标识
        hotelPolicyBean.setHotel_priv_type((Integer) jsonMap.get("hotel_priv_type"));
        hotelPolicyBean.setExceed_buy_type(NumericUtils.obj2int(jsonMap.get("exceed_buy_type"), 1));
        hotelPolicyBean.setRefund_ticket_type((Integer) jsonMap.get("refund_ticket_type"));
        hotelPolicyBean.setOneself_limit((Integer) jsonMap.get("oneself_limit"));
        hotelPolicyBean.setPersonal_pay("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("personal_pay"))));
        log.info("清洗酒店历史模板数据, companyId={},roleType={},privJson={},hotelPolicyBean={}", hotel.getCompanyId(), hotel.getRoleType(), privJsonData, JsonUtils.toJson(hotelPolicyBean));
        return hotelPolicyBean;
    }

    private HotelPolicyBean getDefaultHotelPolicy() {
        HotelPolicyBean hotelPolicyBean = new HotelPolicyBean();
        hotelPolicyBean.setSwitch_flag(false);
        hotelPolicyBean.setRule_limit_flag(false);
        hotelPolicyBean.setHotel_verify_flag(false);
        hotelPolicyBean.setHotel_order_verify_flag(false);
        //1 仅允许本人预订 2 允许本人预订，允许为其他员工预订 3 允许本人预订，允许为其他员工预订，非企业员工预订标识
        hotelPolicyBean.setHotel_priv_type(1);
        hotelPolicyBean.setExceed_buy_type(1);
        hotelPolicyBean.setRefund_ticket_type(0);
        hotelPolicyBean.setOneself_limit(0);
        hotelPolicyBean.setPersonal_pay(false);
        return hotelPolicyBean;
    }

    private TarinPolicyBean getTrainPolicy(OpenEmployeePriv train) {
        if (train == null || ObjectUtils.isEmpty(train.getPrivJsonData())) {
            return getDefaultTrainPolicy();
        }
        String privJsonData = train.getPrivJsonData();
        log.info("清洗火车历史模板数据, companyId={},roleType={},privJson={}", train.getCompanyId(), train.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        TarinPolicyBean tarinPolicyBean = new TarinPolicyBean();
        tarinPolicyBean.setSwitch_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("switch_flag"))));
        tarinPolicyBean.setRule_limit_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("train_rule_limit_flag"))));
        tarinPolicyBean.setRule_id(StringUtils.obj2str(jsonMap.get("train_rule_id")));
        tarinPolicyBean.setTrain_verify_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("train_verify_flag"))));
        tarinPolicyBean.setTrain_order_verify_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("train_order_verify_flag"))));
        //1 仅允许本人预订 2 允许本人预订，允许为其他员工预订 3 允许本人预订，允许为其他员工预订，非企业员工预订标识
        tarinPolicyBean.setTrain_priv_type((Integer) jsonMap.get("train_priv_type"));
        tarinPolicyBean.setExceed_buy_type(NumericUtils.obj2int(jsonMap.get("exceed_buy_type"), 1));
        tarinPolicyBean.setRefund_ticket_type((Integer) jsonMap.get("refund_ticket_type"));
        tarinPolicyBean.setChanges_ticket_type((Integer) jsonMap.get("changes_ticket_type"));
        tarinPolicyBean.setOneself_limit((Integer) jsonMap.get("oneself_limit"));
        log.info("清洗火车历史模板数据, companyId={},roleType={},privJson={},tarinPolicyBean={}", train.getCompanyId(), train.getRoleType(), privJsonData, JsonUtils.toJson(tarinPolicyBean));
        return tarinPolicyBean;
    }

    private TarinPolicyBean getDefaultTrainPolicy() {
        TarinPolicyBean tarinPolicyBean = new TarinPolicyBean();
        tarinPolicyBean.setSwitch_flag(false);
        tarinPolicyBean.setRule_limit_flag(false);
        tarinPolicyBean.setTrain_verify_flag(false);
        tarinPolicyBean.setTrain_order_verify_flag(false);
        //1 仅允许本人预订 2 允许本人预订，允许为其他员工预订 3 允许本人预订，允许为其他员工预订，非企业员工预订标识
        tarinPolicyBean.setTrain_priv_type(1);
        tarinPolicyBean.setExceed_buy_type(1);
        tarinPolicyBean.setRefund_ticket_type(0);
        tarinPolicyBean.setChanges_ticket_type(0);
        tarinPolicyBean.setOneself_limit(0);
        return tarinPolicyBean;
    }

    private CarPolicyBean getCarPolicy(OpenEmployeePriv car) {
        if (car == null || ObjectUtils.isEmpty(car.getPrivJsonData())) {
            return getDefaultCarPolicy();
        }
        String privJsonData = car.getPrivJsonData();
        log.info("清洗用车历史模板数据, companyId={},roleType={},privJson={}", car.getCompanyId(), car.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        CarPolicyBean carPolicyBean = new CarPolicyBean();
        carPolicyBean.setCar_priv_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("car_priv_flag"))));
        carPolicyBean.setRule_limit_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("rule_limit_flag"))));
        carPolicyBean.setExceed_buy_type(NumericUtils.obj2int(jsonMap.get("exceed_buy_type"), 1));
        carPolicyBean.setPersonal_pay("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("personal_pay"))));
        carPolicyBean.setAllow_shuttle("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("allowShuttle"))));
        List<Map<String, Object>> ruleInfos = (List<Map<String, Object>>) jsonMap.get("rule_ids");
        if (!ObjectUtils.isEmpty(ruleInfos)) {
            if (!ObjectUtils.isEmpty(ruleInfos)) {
                carPolicyBean.setRule_infos(ruleInfos.stream().map(ruleMap -> {
                    RuleIdDto ruleIdDto = new RuleIdDto();
                    RuleCategoryTypeDto ruleCategoryTypeDto = new RuleCategoryTypeDto();
                    ruleCategoryTypeDto.setKey(NumericUtils.obj2int(ruleMap.get("type")));
                    ruleCategoryTypeDto.setValue("");
                    ruleCategoryTypeDto.setIcon("");
                    ruleIdDto.setType(ruleCategoryTypeDto);
                    List<Integer> ruleIdList = (List<Integer>) ruleMap.get("rule_id");
                    ruleIdDto.setRule_info(ruleIdList.stream().map(ruleId -> {
                        RuleIdDto.RuleInfo ruleInfo = new RuleIdDto.RuleInfo();
                        ruleInfo.setRule_id(StringUtils.obj2str(ruleId));
                        return ruleInfo;
                    }).collect(Collectors.toList()));
                    return ruleIdDto;
                }).collect(Collectors.toList()));
            }
        }
        log.info("清洗用车历史模板数据, companyId={},roleType={},privJson={},carPolicyBean={}", car.getCompanyId(), car.getRoleType(), privJsonData, JsonUtils.toJson(carPolicyBean));
        return carPolicyBean;
    }

    private CarPolicyBean getDefaultCarPolicy() {
        CarPolicyBean carPolicyBean = new CarPolicyBean();
        carPolicyBean.setCar_priv_flag(false);
        carPolicyBean.setRule_limit_flag(false);
        carPolicyBean.setExceed_buy_type(1);
        carPolicyBean.setPersonal_pay(false);
        carPolicyBean.setAllow_shuttle(false);
        return carPolicyBean;
    }

    private MallPolicyBean getMallPolicy(OpenEmployeePriv mall) {
        if (mall == null || ObjectUtils.isEmpty(mall.getPrivJsonData())) {
            return getDefaultMallPolicy();
        }
        String privJsonData = mall.getPrivJsonData();
        log.info("清洗采购历史模板数据, companyId={},roleType={},privJson={}", mall.getCompanyId(), mall.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        MallPolicyBean mallPolicyBean = new MallPolicyBean();
        mallPolicyBean.setMall_priv_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("mall_priv_flag"))));
        mallPolicyBean.setRule_limit_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("rule_limit_flag"))));
        mallPolicyBean.setRule_id(StringUtils.obj2str(jsonMap.get("rule_id")));
        mallPolicyBean.setExceed_buy_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("exceed_buy_flag"))));
        mallPolicyBean.setMall_verify_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("mall_verify_flag"))));
        log.info("清洗采购历史模板数据, companyId={},roleType={},privJson={},mallPolicyBean={}", mall.getCompanyId(), mall.getRoleType(), privJsonData, JsonUtils.toJson(mallPolicyBean));
        return mallPolicyBean;
    }

    private MallPolicyBean getDefaultMallPolicy() {
        MallPolicyBean mallPolicyBean = new MallPolicyBean();
        mallPolicyBean.setMall_priv_flag(false);
        mallPolicyBean.setRule_limit_flag(false);
        mallPolicyBean.setExceed_buy_flag(false);
        mallPolicyBean.setMall_verify_flag(false);
        return mallPolicyBean;
    }


    private TakeawayPolicyBean getTakeawayPolicy(OpenEmployeePriv takeaway) {
        if (takeaway == null || ObjectUtils.isEmpty(takeaway.getPrivJsonData())) {
            return getDefaultTakeawayPolicy();
        }
        String privJsonData = takeaway.getPrivJsonData();
        log.info("清洗外卖历史模板数据, companyId={},roleType={},privJson={}", takeaway.getCompanyId(), takeaway.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        TakeawayPolicyBean takeawayPolicyBean = new TakeawayPolicyBean();
        takeawayPolicyBean.setTakeaway_priv_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("takeaway_priv_flag"))));
        takeawayPolicyBean.setRule_limit_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("takeaway_rule_limit_flag"))));
        String takeawayRuleId = StringUtils.obj2str(jsonMap.get("takeaway_rule_id"));
        if (!ObjectUtils.isEmpty(takeawayRuleId)) {
            takeawayPolicyBean.setRule_id(NumericUtils.obj2int(takeawayRuleId));
        }
        takeawayPolicyBean.setExceed_buy_type(NumericUtils.obj2int(jsonMap.get("exceed_buy_type"), 1));
        takeawayPolicyBean.setPersonal_pay("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("personal_pay"))));
        log.info("清洗外卖历史模板数据, companyId={},roleType={},privJson={},takeawayPolicyBean={}", takeaway.getCompanyId(), takeaway.getRoleType(), privJsonData, JsonUtils.toJson(takeawayPolicyBean));
        return takeawayPolicyBean;
    }

    private TakeawayPolicyBean getDefaultTakeawayPolicy() {
        TakeawayPolicyBean takeawayPolicyBean = new TakeawayPolicyBean();
        takeawayPolicyBean.setTakeaway_priv_flag(false);
        takeawayPolicyBean.setRule_limit_flag(false);
        takeawayPolicyBean.setExceed_buy_type(1);
        takeawayPolicyBean.setPersonal_pay(false);
        return takeawayPolicyBean;
    }

    private DinnersPolicyDto getDinnersPolicy(OpenEmployeePriv dinners) {
        if (dinners == null || ObjectUtils.isEmpty(dinners.getPrivJsonData())) {
            return getDefaultDinnersPolicy();
        }
        String privJsonData = dinners.getPrivJsonData();
        log.info("清洗到店用餐历史模板数据, companyId={},roleType={},privJson={}", dinners.getCompanyId(), dinners.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        DinnersPolicyDto dinnersPolicyBean = new DinnersPolicyDto();
        dinnersPolicyBean.setRule_priv_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("rule_priv_flag"))));
        dinnersPolicyBean.setRule_limit_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("rule_limit_flag"))));
        dinnersPolicyBean.setRule_id(StringUtils.obj2str(jsonMap.get("rule_id")));
        Map<String, Object> meishiPolicy = (Map<String, Object>) jsonMap.get("meishi_policy");
        if (meishiPolicy != null) {
            MeishiPolicyDto meishiPolicyDto = new MeishiPolicyDto();
            meishiPolicyDto.setPersonal_pay("TRUE".equalsIgnoreCase(StringUtils.obj2str(meishiPolicy.get("personal_pay"))));
            meishiPolicyDto.setExceed_buy_type(NumericUtils.obj2int(meishiPolicy.get("exceed_buy_type"), 1));
            dinnersPolicyBean.setMeishi_policy(meishiPolicyDto);
        }
        log.info("清洗到店用餐历史模板数据, companyId={},roleType={},privJson={},dinnersPolicyBean={}", dinners.getCompanyId(), dinners.getRoleType(), privJsonData, JsonUtils.toJson(dinnersPolicyBean));
        return dinnersPolicyBean;
    }

    private DinnersPolicyDto getDefaultDinnersPolicy() {
        DinnersPolicyDto dinnersPolicyBean = new DinnersPolicyDto();
        dinnersPolicyBean.setRule_priv_flag(false);
        dinnersPolicyBean.setRule_limit_flag(false);
        MeishiPolicyDto meishiPolicyDto = new MeishiPolicyDto();
        meishiPolicyDto.setPersonal_pay(false);
        meishiPolicyDto.setExceed_buy_type(1);
        dinnersPolicyBean.setMeishi_policy(meishiPolicyDto);
        return dinnersPolicyBean;
    }


    private ShansongPolicyDto getShansongPolicy(OpenEmployeePriv shansong) {
        if (shansong == null || ObjectUtils.isEmpty(shansong.getPrivJsonData())) {
            return getDefaultShansongPolicy();
        }
        String privJsonData = shansong.getPrivJsonData();
        log.info("清洗闪送历史模板数据, companyId={},roleType={},privJson={}", shansong.getCompanyId(), shansong.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        ShansongPolicyDto shansongPolicyDto = new ShansongPolicyDto();
        shansongPolicyDto.setShansong_priv_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("shansong_priv_flag"))));
        log.info("清洗闪送历史模板数据, companyId={},roleType={},privJson={},shansongPolicyDto={}", shansong.getCompanyId(), shansong.getRoleType(), privJsonData, JsonUtils.toJson(shansongPolicyDto));
        return shansongPolicyDto;
    }

    private ShansongPolicyDto getDefaultShansongPolicy() {
        ShansongPolicyDto shansongPolicyDto = new ShansongPolicyDto();
        shansongPolicyDto.setShansong_priv_flag(false);
        return shansongPolicyDto;
    }

    private ShunfengPolicyDto getShunfengPolicy(OpenEmployeePriv shunfeng) {
        if (shunfeng == null || ObjectUtils.isEmpty(shunfeng.getPrivJsonData())) {
            return getDefaultShunfengPolicy();
        }
        String privJsonData = shunfeng.getPrivJsonData();
        log.info("清洗快递历史模板数据, companyId={},roleType={},privJson={}", shunfeng.getCompanyId(), shunfeng.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        ShunfengPolicyDto shunfengPolicyDto = new ShunfengPolicyDto();
        shunfengPolicyDto.setShunfeng_priv_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("shunfeng_priv_flag"))));
        log.info("清洗快递历史模板数据, companyId={},roleType={},privJson={},shunfengPolicyDto={}", shunfeng.getCompanyId(), shunfeng.getRoleType(), privJsonData, JsonUtils.toJson(shunfengPolicyDto));
        return shunfengPolicyDto;
    }

    private ShunfengPolicyDto getDefaultShunfengPolicy() {
        ShunfengPolicyDto shunfengPolicyDto = new ShunfengPolicyDto();
        shunfengPolicyDto.setShunfeng_priv_flag(false);
        return shunfengPolicyDto;
    }

    private PaymentApplyPolicyDto getPaymentApplyPolicy(OpenEmployeePriv paymentApply) {
        if (paymentApply == null || ObjectUtils.isEmpty(paymentApply.getPrivJsonData())) {
            return getDefaultPaymentApplyPolicy();
        }
        String privJsonData = paymentApply.getPrivJsonData();
        log.info("清洗对公付款历史模板数据, companyId={},roleType={},privJson={}", paymentApply.getCompanyId(), paymentApply.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        PaymentApplyPolicyDto paymentApplyPolicyDto = new PaymentApplyPolicyDto();
        paymentApplyPolicyDto.setPayment_apply_priv_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("payment_apply_priv_flag"))));
        log.info("清洗对公付款历史模板数据, companyId={},roleType={},privJson={},paymentApplyPolicyDto={}", paymentApply.getCompanyId(), paymentApply.getRoleType(), privJsonData, JsonUtils.toJson(paymentApplyPolicyDto));
        return paymentApplyPolicyDto;
    }

    private PaymentApplyPolicyDto getDefaultPaymentApplyPolicy() {
        PaymentApplyPolicyDto paymentApplyPolicyDto = new PaymentApplyPolicyDto();
        paymentApplyPolicyDto.setPayment_apply_priv_flag(false);
        return paymentApplyPolicyDto;
    }

    private VirtualCardPolicyDto getVirtualCardPolicy(OpenEmployeePriv virtualCard) {
        if (virtualCard == null || ObjectUtils.isEmpty(virtualCard.getPrivJsonData())) {
            return getDefaultVirtualCardPolicy();
        }
        String privJsonData = virtualCard.getPrivJsonData();
        log.info("清洗虚拟卡历史模板数据, companyId={},roleType={},privJson={}", virtualCard.getCompanyId(), virtualCard.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        VirtualCardPolicyDto virtualCardPolicyDto = new VirtualCardPolicyDto();
        virtualCardPolicyDto.setVirtual_card_priv_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("virtual_card_priv_flag"))));
        log.info("清洗虚拟卡历史模板数据, companyId={},roleType={},privJson={},virtualCardPolicyDto={}", virtualCard.getCompanyId(), virtualCard.getRoleType(), privJsonData, JsonUtils.toJson(virtualCardPolicyDto));
        return virtualCardPolicyDto;
    }

    private VirtualCardPolicyDto getDefaultVirtualCardPolicy() {
        VirtualCardPolicyDto virtualCardPolicyDto = new VirtualCardPolicyDto();
        virtualCardPolicyDto.setVirtual_card_priv_flag(false);
        return virtualCardPolicyDto;
    }

    private MileagePolicyDto getMileagePolicy(OpenEmployeePriv mileage) {
        if (mileage == null || ObjectUtils.isEmpty(mileage.getPrivJsonData())) {
            return getDefaultMileagePolicy();
        }
        String privJsonData = mileage.getPrivJsonData();
        log.info("清洗里程补贴卡历史模板数据, companyId={},roleType={},privJson={}", mileage.getCompanyId(), mileage.getRoleType(), privJsonData);
        Map<String, Object> jsonMap = JsonUtils.toObj(privJsonData, Map.class);
        MileagePolicyDto mileagePolicyDto = new MileagePolicyDto();
        mileagePolicyDto.setRule_priv_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("rule_priv_flag"))));
        mileagePolicyDto.setRule_limit_flag("TRUE".equalsIgnoreCase(StringUtils.obj2str(jsonMap.get("rule_limit_flag"))));
        mileagePolicyDto.setRule_id(StringUtils.obj2str(jsonMap.get("rule_id")));
        mileagePolicyDto.setExceed_buy_flag(NumericUtils.obj2int(jsonMap.get("exceed_buy_flag"), 1));
        log.info("清洗里程补贴卡历史模板数据, companyId={},roleType={},privJson={},mileagePolicyDto={}", mileage.getCompanyId(), mileage.getRoleType(), privJsonData, JsonUtils.toJson(mileagePolicyDto));
        return mileagePolicyDto;
    }

    private MileagePolicyDto getDefaultMileagePolicy() {
        MileagePolicyDto mileagePolicyDto = new MileagePolicyDto();
        mileagePolicyDto.setRule_priv_flag(false);
        mileagePolicyDto.setRule_limit_flag(false);
        mileagePolicyDto.setExceed_buy_flag(1);
        return mileagePolicyDto;
    }

    private BusPolicyDto getBusPolicy(OpenEmployeePriv bus) {
        BusPolicyDto busPolicyDto = new BusPolicyDto();
        busPolicyDto.setUnemployee_bus(false);
        busPolicyDto.setBus_other_flag(false);
        busPolicyDto.setBus_priv_flag(false);
        busPolicyDto.setBus_verify_flag(false);
        busPolicyDto.setRule_limit_flag(false);
        busPolicyDto.setExceed_buy_flag(false);
        busPolicyDto.setExceed_buy_type(1);
        busPolicyDto.setBus_order_verify_flag(false);
        busPolicyDto.setOneself_limit(0);
        busPolicyDto.setBus_priv_type(1);
        busPolicyDto.setSwitch_flag(false);
        return busPolicyDto;
    }

    @Override
    public void updateEmployeeAuthTemplate(String companyId, Map<Integer, String> templateMap) {
        if (!ObjectUtils.isEmpty(templateMap)) {
            templateMap.forEach((roleType, templateId) -> {
                OpenEmployeeRuleTemplateConfig employeeRuleTemplateConfig = new OpenEmployeeRuleTemplateConfig();
                employeeRuleTemplateConfig.setCompanyId(companyId);
                employeeRuleTemplateConfig.setTemplateId(templateId);
                employeeRuleTemplateConfig.setRoleType(roleType);
                employeeRuleTemplateConfigDao.saveSelective(employeeRuleTemplateConfig);
            });
        }
    }
}
