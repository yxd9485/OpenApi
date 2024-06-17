package com.fenbeitong.openapi.plugin.welink.isv.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by lizhen on 2020/07/01.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "welink_isv_order")
public class WeLinkIsvOrder {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 客户在华为云注册账号的唯一标识
     */
    @Column(name = "BUSINESS_ID")
    private String businessId;

    /**
     * 客户在华为云注册的账户名
     */
    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    /**
     * 客户以IAM用户认证方式登录时对应子用户的唯一标识
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 客户以IAM用户认证方式登录的用户名
     */
    @Column(name = "USER_NAME")
    private String userName;

    /**
     * 客户手机号
     */
    @Column(name = "MOBILE_PHONE")
    private String mobilePhone;

    /**
     * 客户邮箱
     */
    @Column(name = "EMAIL")
    private String email;

    /**
     * 云市场业务ID
     */
    @Column(name = "INSTANCE_ID")
    private String instanceId;

    /**
     * 云市场订单ID
     */
    @Column(name = "ORDER_ID")
    private String orderId;

    /**
     * 产品规格标识
     */
    @Column(name = "SKU_CODE")
    private String skuCode;

    /**
     * 产品标识
     */
    @Column(name = "PRODUCT_ID")
    private String productId;

    /**
     * 是否为调试请求,1：调试请求,0： 非调试请求
     */
    @Column(name = "TEST_FLAG")
    private String testFlag;

    /**
     * 是否是开通试用实例,1：试用实例,0：非试用实例
     */
    @Column(name = "TRIAL_FLAG")
    private String trialFlag;

    /**
     * 
     */
    @Column(name = "EXPIRE_TIME")
    private String expireTime;

    /**
     * 计费模式。3：表示按次购买。
     */
    @Column(name = "CHARGING_MODE")
    private Integer chargingMode;

    /**
     * 扩展参数
     */
    @Column(name = "SAAS_EXTEND_PARAMS")
    private String saasExtendParams;

    /**
     * 数量类型的商品定价属性
     */
    @Column(name = "AMOUNT")
    private Integer amount;


}
