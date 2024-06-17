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
 * Created by hanshuqi on 2020/07/05.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fxiaoke_apply_trip")
public class FxiaokeApplyTrip {

    /**
     * 主键id
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
     * trip_id,根据ID匹配交通工具类型
     */
    @Column(name = "TRIP_ID")
    private String tripId;

    /**
     * 交通工具类型
     */
    @Column(name = "TRIP_TYPE")
    private String tripType;

    /**
     * 交通工具名称
     */
    @Column(name = "TRIP_NAME")
    private String tripName;

    /**
     * 状态是否可用,0:可用，1:不可用
     */
    @Column(name = "TRIP_STATUS")
    private String tripStatus;

    /**
     * 
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;


}
