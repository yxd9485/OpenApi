package com.fenbeitong.openapi.plugin.seeyon.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hanshuqi on 2020/05/12.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_msg_setup")
public class SeeyonOpenMsgSetup {

    /**
     * 
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 公司ID
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 选项编码
     */
    @Column(name = "item_code")
    private String itemCode;

    /**
     * 默认值
     */
    @Column(name = "is_checked")
    private Integer isChecked;

    /**
     * 
     */
    @Column(name = "int_val1")
    private Integer intVal1;

    /**
     * 
     */
    @Column(name = "int_val2")
    private Integer intVal2;

    /**
     * 
     */
    @Column(name = "int_val3")
    private Integer intVal3;

    /**
     * 
     */
    @Column(name = "str_val1")
    private String strVal1;

    /**
     * 
     */
    @Column(name = "str_val2")
    private String strVal2;

    /**
     * 
     */
    @Column(name = "str_val3")
    private String strVal3;

    /**
     * 
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 
     */
    @Column(name = "update_time")
    private Date updateTime;


}
