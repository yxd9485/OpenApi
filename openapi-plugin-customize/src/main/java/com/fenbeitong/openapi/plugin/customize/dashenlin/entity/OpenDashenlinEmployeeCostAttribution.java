package com.fenbeitong.openapi.plugin.customize.dashenlin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lizhen on 2021/04/08.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_dashenlin_employee_cost_attribution")
public class OpenDashenlinEmployeeCostAttribution {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 三方人员ID
     */
    @Column(name = "THIRD_EMPLOYEE_ID")
    private String thirdEmployeeId;

    /**
     * 费用归属ID
     */
    @Column(name = "COST_ATTRIBUTION_ID_OA")
    private String costAttributionIdOa;

    /**
     * 费用归属名称
     */
    @Column(name = "COST_ATTRIBUTION_NAME_OA")
    private String costAttributionNameOa;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;


}
