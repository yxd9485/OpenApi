package com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hanshuqi on 2020/09/18.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fxiaoke_pre_install_obj")
public class FxiaokePreInstallObj {

    /**
     * 
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 企业ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 纷销客对象id
     */
    @Column(name = "API_NAME")
    private String apiName;

    /**
     * 纷销客预置对象ID
     */
    @Column(name = "PRE_INSTALL_ID")
    private String preInstallId;

    /**
     * 纷销客预置对象名称
     */
    @Column(name = "PRE_INSTALL_NAME")
    private String preInstallName;

    /**
     * 纷销客预置对象状态
     */
    @Column(name = "PRE_INSTALL_STATE")
    private Integer preInstallState;

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
