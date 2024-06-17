package com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hanshuqi on 2020/07/06.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fxiaoke_trip_round")
public class FxiaokeTripRound {

    /**
     * 
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 公司ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 单程往返标识
     */
    @Column(name = "ROUND_TRIP")
    private Integer roundTrip;

    /**
     * 往返名称
     */
    @Column(name = "TRIP_NAME")
    private String tripName;

    /**
     * 是否可用
     */
    @Column(name = "STATE")
    private String state;

    /**
     * 纷享销客ID
     */
    @Column(name = "TRIP_ID")
    private String tripId;

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
