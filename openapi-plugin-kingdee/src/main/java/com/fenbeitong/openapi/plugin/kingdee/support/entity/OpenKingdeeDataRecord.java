package com.fenbeitong.openapi.plugin.kingdee.support.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by duhui on 2022/01/14.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_kingdee_data_record")
public class OpenKingdeeDataRecord {

    /**
     * 
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 公司ID
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 编号
     */
    @Column(name = "number")
    private String number;

    /**
     * 数据 value
     */
    @Column(name = "data_value")
    private String dataValue;

    /**
     * 0 初始转态 1 保存 2提交 3审核
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "create_date")
    private Date createDate;

    /**
     * 更新时间
     */
    @Column(name = "update_date")
    private Date updateDate;


}
