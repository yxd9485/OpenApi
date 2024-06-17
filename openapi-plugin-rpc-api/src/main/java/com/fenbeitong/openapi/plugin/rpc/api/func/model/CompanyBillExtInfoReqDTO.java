package com.fenbeitong.openapi.plugin.rpc.api.func.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>Title: CompanyBillExtInfoReqDTO</p>
 * <p>Description: 公司账单扩展信息请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/28 3:45 PM
 */
@Data
public class CompanyBillExtInfoReqDTO implements Serializable {

    /**
     * 公司id
     */
    @NotBlank(message = "公司ID[companyId]不可为空")
    private String companyId;

    /**
     * 人员id
     */
    private String employeeId;

    /**
     * 订单id
     */
    @NotBlank(message = "订单ID[orderId]不可为空")
    private String orderId;

    /**
     * 票id
     */
    private String ticketId;

    /**
     * 场景类型
     */
    @NotNull(message = "场景类型[type]不可为空")
    private Integer type;
}
