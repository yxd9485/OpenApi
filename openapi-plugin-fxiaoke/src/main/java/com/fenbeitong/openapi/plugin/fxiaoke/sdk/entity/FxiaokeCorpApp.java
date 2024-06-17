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
 * Created by hanshuqi on 2020/07/01.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fxiaoke_corp_app")
public class FxiaokeCorpApp {

    /**
     * 主键
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
     * 应用ID
     */
    @Column(name = "APP_ID")
    private String appId;

    /**
     * 应用secret
     */
    @Column(name = "APP_SECRET")
    private String appSecret;

    /**
     * APP名称
     */
    @Column(name = "APP_NAME")
    private String appName;

    /**
     * 永久授权码
     */
    @Column(name = "PERMANENT")
    private String permanent;

    /**
     * token
     */
    @Column(name = "TOKEN")
    private String token;

    /**
     * aeskey
     */
    @Column(name = "ENCODING_AES_KEY")
    private String encodingAesKey;
    /**
     * token
     */
    @Column(name = "APP_STATE")
    private String appState;
    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;


}
