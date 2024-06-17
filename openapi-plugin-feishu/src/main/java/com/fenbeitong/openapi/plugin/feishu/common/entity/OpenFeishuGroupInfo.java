package com.fenbeitong.openapi.plugin.feishu.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by zhangpeng on 2021/09/25.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_feishu_group_info")
public class OpenFeishuGroupInfo {

    /**
     * 主键
     */
    @Column(name = "ID")
    private String id;

    /**
     * 考勤组ID
     */
    @Column(name = "GROUP_ID")
    private String groupId;

    /**
     * 对应发券分组ID
     */
    @Column(name = "VOUCHER_GROUP_ID")
    private String voucherGroupId;

    /**
     * 考勤组状态；1 可用；0 废弃
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 公司ID
     */
    @Column(name = "COMPANY_ID")
    private String companyId;

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
