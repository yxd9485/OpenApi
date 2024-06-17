package com.fenbeitong.openapi.plugin.qiqi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName QiqiCorpInfo
 * @Description 企企企业配置表
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/5/13 下午2:29
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "qiqi_corp")
public class QiqiCorpInfo {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "company_Id")
    private String companyId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "open_id")
    private String openId;

    @Column(name = "access_key_id")
    private String accessKeyId;

    @Column(name = "secret_access_key")
    private String secretAccessKey;

    @Column(name = "state")
    private Integer state;

    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
}
