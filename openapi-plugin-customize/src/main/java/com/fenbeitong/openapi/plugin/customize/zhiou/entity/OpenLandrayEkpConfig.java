package com.fenbeitong.openapi.plugin.customize.zhiou.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author lizhen
 * @date 2021/01/27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_landray_ekp_config")
public class OpenLandrayEkpConfig {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 公司ID
     */
    @Column(name = "COMPANY_ID")
    private String companyId;

    /**
     * 公司名称
     */
    @Column(name = "COMPANY_NAME")
    private String companyName;

    /**
     * webservice地址
     */
    @Column(name = "WS_URL")
    private String wsUrl;

    /**
     * 工作流监听
     */
    @Column(name = "WORK_FLOW_LISTENER")
    private String workFlowListener;

    /**
     * 行程数据表单method
     */
    @Column(name = "TRIP_FORM_METHOD")
    private String tripFormMethod;

    /**
     * 用户名
     */
    @Column(name = "USER_NAME")
    private String userName;

    /**
     * 密码
     */
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "HTTP_URL")
    private String httpUrl;

}
