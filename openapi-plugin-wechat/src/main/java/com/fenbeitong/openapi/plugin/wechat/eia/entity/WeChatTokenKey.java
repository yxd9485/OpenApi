package com.fenbeitong.openapi.plugin.wechat.eia.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**公司企业微信token相关数据实体类
 * Created by dave.hansins on 19/12/13.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="qywx_token_key")
public class WeChatTokenKey {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CORP_ID")
    private String corpId;

    @Column(name = "CORP_TOKEN")
    private String corpToken;

    @Column(name = "CORP_ENCODING_AES_KEY")
    private String corpEncodingAesKey;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    @Column(name = "CORP_IGNORE_EVENT")
    private String corpIgnoreEvent;
}
