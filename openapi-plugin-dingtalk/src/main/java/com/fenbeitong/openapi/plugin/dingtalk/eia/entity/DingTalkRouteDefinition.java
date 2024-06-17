package com.fenbeitong.openapi.plugin.dingtalk.eia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 钉钉代理地址配置
 * Created by log.chang on 2019/12/24.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dingtalk_route")
public class DingTalkRouteDefinition {

    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "CORP_ID")
    private String corpId;// 钉钉企业ID
    @Column(name = "PROXY_URL")
    private String proxyUrl;// 代理地址，如：http://192.168.1.1/abc
    @Column(name = "CREATE_TIME")
    private Date createTime;// 创建时间
    @Column(name = "UPDATE_TIME")
    private Date updateTime;// 修改时间
    @Column(name = "DESCRPTION")
    private String description;// 备注

}
