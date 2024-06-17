package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fenbeitong.saasplus.api.model.dto.finance.FinanceCostInfoVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author wuchao
 * @date 2020/12/23
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ApplyOrderDetailDTO {

    /**
     * 申请单信息
     */
    private ApplyOrderContractDto apply;

    /**
     * 同行人信息
     */
    private List<UserContactDTO> guest_list;

    /**
     * 行程信息
     */
    private List<ApplyTripInfoDTO> trip_list;

    /**
     * 订单信息
     */
    private List<Map<String, Object>> order_list;


    private Map<String, Object> order_info;

    /**
     * 已经回票列表
     */
    private List<Map> alreadyReturnList;

    /**
     * 未回票列表
     */
    private List<Map> notReturnList;

    /**
     * 已回票数量
     */
    private Integer alreadyReturnCount;

    /**
     * 所有需回票数量
     */
    private Integer allNeedReturnCount;

    private List<FinanceCostInfoVO> costList;

    /**
     * 公司支付金额
     */
    private BigDecimal publicAmount;
    /**
     * 个人支付金额
     */
    private BigDecimal personalAmount;
    /**
     * 报销金额
     */
    private BigDecimal amountReimburseCompany;
    /**
     * 自费金额
     */
    private BigDecimal amountReimburseSelf;
    /**
     * 实际应收金额
     */
    private BigDecimal actualReceivableAmout;

    /**
     * 三方信息FuncThirdInfoServiceImpl
     */
    private ApplyDetailThirdInfoDTO thirdInfo;
}
