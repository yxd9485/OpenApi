package com.fenbeitong.openapi.plugin.wechat.eia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by dave.hansins on 19/12/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="process_instance")
public class ProcessInstance {

    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "CORP_ID")
    private String corpId;
    @Column(name = "PROCESS_CODE")
    private String processCode;
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "INSTANCE_ID")
    private String instanceId;
    @Column(name = "BUSINESS_ID")
    private String businessId;
    @Column(name = "BIZ_ACTION")
    private String bizAction;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

}
