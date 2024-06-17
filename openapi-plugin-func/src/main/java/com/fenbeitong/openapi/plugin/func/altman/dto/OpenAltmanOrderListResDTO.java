package com.fenbeitong.openapi.plugin.func.altman.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.noc.api.service.altman.model.vo.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by xiaowei on 2020/05/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenAltmanOrderListResDTO {

    /**
     * 订单信息
     */
    private AltmanOrderInfo orderStereoInfoVO;

    /**
     * 价格信息
     */
    private AltmanPriceInfo priceStereoInfoVO;

    /**
     * 业务类别 等信息
     */
    private AltmanClassifyInfo classifyStereoInfoVO;


    /**
     * 使用人信息
     */
    private List<AltmanConsumerInfo> consumerInfoList;


    /**
     * 交易记录（供应商）
     */
    private AltmanSupplierInfo supplierStereoInfoVO;

    /**
     * 拓展字段的表头信息
     */
    // private List<DataFieldSimpleVO> fieldDescribeList;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AltmanOrderInfo {

        /**
         * 订单ID
         */
        private String orderId;


        /**
         * 公司ID
         */
        private String companyId;

        /**
         * 公司名称
         */
        private String companyName;

        /**
         * 员工(预定人)ID
         */
        private String userId;

        /**
         * 员工(预定人)姓名
         */
        private String userName;

        /**
         * 员工(预定人)手机号
         */
        private String userPhone;

        /**
         * 员工(预定人)部门id
         */
        private String userUnitId;

        /**
         * 员工(预定人)部门名称
         */
        private String userUnitName;

        /**
         * 预定时间
         */
        private Date createTime;

        private Map<String, Object> dataMap;


    }

//    @Data
//    @Builder
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class DataFieldSimpleVO {
//
//        private String fieldId;
//        private String fieldApiName;
//        private String fieldName;
//        private String controlId;
//    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AltmanPriceInfo {

        /**
         * 订单总价
         */
        private BigDecimal totalPrice;

        /**
         * 订单采购价
         */
        private BigDecimal costPrice;

        /**
         * 支付价格（支付价格=订单总价-优惠券价格）
         */
        private BigDecimal payPrice;

        /**
         * 优惠券总金额
         */
        private BigDecimal totalDiscount;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AltmanClassifyInfo {

        /**
         * 业务类别ID
         */
        private Integer orderTypeClassify;

        /**
         * 业务类别名称
         */
        private String orderTypeClassifyName;

        /**
         * 业务名称
         */
        private String orderTypeName;

        /**
         * 业务描述
         */
        private String orderTypeDesc;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AltmanConsumerInfo {

        /**
         * 使用人类型 0企业 1非企业
         */
        private Integer consumerType;

        /**
         * 使用人姓名    【德邦】若无显示乘车人手机号
         */
        private String consumerName;

        /**
         * 使用人手机号 【德邦】乘车人手机号
         */
        private String consumerPhone;

        /**
         * 使用人ID
         */
        private String consumerId;

        /**
         * 使用人部门ID 【德邦】有则记录，无则为空
         */
        private String consumerUnitId;

        /**
         * 使用人部门名称  【德邦】有则记录，无则为空
         */
        private String consumerUnitName;

        /**
         * 使用人部门全称  【德邦】有则记录，无则为空
         */
        private String consumerFullUnitName;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AltmanSupplierInfo {

        /**
         * 供应商ID
         */
        private Integer supplierId;

        /**
         * 供应商名称
         */
        private String supplierName;

        /**
         * 供应商订单号 根据此订单号查询供应商订单状态
         */
        private String supplierOrderId;

        /**
         * 采购总价   || 退款时，为退款给供应商金额
         */
        private BigDecimal costPrice;

    }

}
