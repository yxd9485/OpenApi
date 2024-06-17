package com.fenbeitong.openapi.plugin.definition.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by xiaowei on 2020/05/19.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attr_spec")
public class AttrSpec {

    /**
     * 
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 
     */
    @Column(name = "attr_cd")
    private String attrCd;

    /**
     * 
     */
    @Column(name = "value")
    private String value;

    /**
     * 
     */
    @Column(name = "value_name")
    private String valueName;

    /**
     * 
     */
    @Column(name = "spec_type")
    private String specType;

    /**
     * 
     */
    @Column(name = "list_flag")
    private String listFlag;

    /**
     * 
     */
    @Column(name = "default_flag")
    private String defaultFlag;

    /**
     * 
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 
     */
    @Column(name = "attr_value_name")
    private String attrValueName;


}
