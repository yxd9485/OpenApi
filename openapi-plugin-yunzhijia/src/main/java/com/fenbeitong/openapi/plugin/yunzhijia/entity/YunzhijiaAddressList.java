package com.fenbeitong.openapi.plugin.yunzhijia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="yunzhijia_address_list")
public class YunzhijiaAddressList {


    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CORP_ID")
    private String corpId;

    @Column(name = "CORP_TOKEN")
    private String  corpToken ;

    @Column(name = "CORP_SECRET")
    private String corpSecret;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "UPDATE_TIME")
    private Date updateTime;
}
