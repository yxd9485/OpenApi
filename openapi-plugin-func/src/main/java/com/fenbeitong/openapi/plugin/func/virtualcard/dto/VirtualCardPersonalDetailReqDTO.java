package com.fenbeitong.openapi.plugin.func.virtualcard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName VirtualCardPersonalDetailReqDTO
 * @Description 虚拟卡个人消费
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/21 下午10:02
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualCardPersonalDetailReqDTO {

    /**
     * 交易流水创建时间---开始时间 yyyy-MM-dd HH:mm:ss
     */
    @JsonProperty("create_begin")
    private String createBegin;

    /**
     * 交易流水创建时间---结束时间 yyyy-MM-dd HH:mm:ss
     */
    @JsonProperty("create_end")
    private String createEnd;

    /**
     * 交易类型: 1消费2退款3冲正4撤销冲正 （银行业务类型）
     * @see com.fenbeitong.finhub.common.constant.BankHuPoTxnType
     */
    @JsonProperty("bank_hupo_trans_type")
    @NotNull(message="交易类型[bank_hupo_trans_type]不可为空")
    private Integer bankHupoTransType;

    /**
     * 交易编号
     */
    @JsonProperty("show_type")
    private Integer showType=1;

    /**
     * 用户ID
     */
    @JsonProperty("third_employee_id")
    @NotBlank(message = "[third_employee_id]不可为空")
    private String thirdEmployeeId;

    /**
     * 开始页
     */
    @JsonProperty("page_index")
    private Integer pageIndex;
    /**
     * 每页显示的条数
     */
    @JsonProperty("page_size")
    private Integer pageSize;
}
