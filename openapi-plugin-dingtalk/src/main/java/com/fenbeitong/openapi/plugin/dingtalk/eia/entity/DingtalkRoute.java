package com.fenbeitong.openapi.plugin.dingtalk.eia.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>Title: DingtalkRoute</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 10:33 AM
 */
@Data
@Table(name = "dingtalk_route")
public class DingtalkRoute {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CORP_ID")
    private String corpId;

    @Column(name = "PROXY_URL")
    private String proxyUrl;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    @Column(name = "DESCRPTION")
    private String descrption;

}