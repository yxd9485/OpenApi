package com.fenbeitong.openapi.plugin.dingtalk.isv.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lizhen on 2020/07/15.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_sync_biz_data_medium_history")
public class OpenSyncBizDataMediumHistory {

    /**
     * ID
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @Column(name = "gmt_modified")
    private Date gmtModified;

    /**
     * 订阅方ID
     */
    @Column(name = "subscribe_id")
    private String subscribeId;

    /**
     * 企业ID
     */
    @Column(name = "corp_id")
    private String corpId;

    /**
     * 业务ID
     */
    @Column(name = "biz_id")
    private String bizId;

    /**
     * 业务类型
     */
    @Column(name = "biz_type")
    private Integer bizType;

    /**
     * 业务数据
     */
    @Column(name = "biz_data")
    private String bizData;

    /**
     * 对账游标
     */
    @Column(name = "open_cursor")
    private Long openCursor;

    /**
     * 处理状态0为未处理。其他状态开发者自行定义
     */
    @Column(name = "status")
    private Integer status;

    @Column(name = "remark")
    private String remark;

    @Column(name = "next_execute")
    private Date nextExecute;
}
