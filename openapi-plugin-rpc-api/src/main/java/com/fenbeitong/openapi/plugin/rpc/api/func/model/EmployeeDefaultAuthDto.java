package com.fenbeitong.openapi.plugin.rpc.api.func.model;

import com.fenbeitong.usercenter.api.model.dto.rule.*;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>Title: EmployeeDeaultAuthDto</p>
 * <p>Description: 员工默认权限</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/7/21 10:24 AM
 */
@Data
public class EmployeeDefaultAuthDto implements Serializable {

    /**
     * 权限类型
     */
    private Integer roleType;

    /**
     * 用车权限
     */
    private CarPolicyBean car_policy;

    /**
     * 采购权限
     */
    private MallPolicyBean mall_policy;

    /**
     * 用餐权限
     */
    private DinnerPolicyBean dinner_policy;

    private AirPolicyBean air_policy;

    private HotelPolicyBean hotel_policy;

    private TarinPolicyBean train_policy;

    private IntlAirPolicyBean intl_air_policy;

    /**
     * 汽车票
     */
    private BusPolicyDto bus_policy;

    /**
     * 用餐
     */
    private TakeawayPolicyBean takeaway_policy;

    /**
     * 美食 2.5.1
     */
    private DinnersPolicyDto dinners_policy;

    /**
     * 闪送 3.4.0
     */
    private ShansongPolicyDto shansong_policy;

    //顺丰 3.7.0
    private ShunfengPolicyDto shunfeng_policy;

    //付款申请 4.1.0
    private PaymentApplyPolicyDto payment_apply_policy;

    //虚拟卡权限 4.2.0
    private VirtualCardPolicyDto virtual_card_policy;

    /**
     * 里程权限 4.6.1
     */
    private MileagePolicyDto mileage_policy;

}
