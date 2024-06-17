package com.fenbeitong.openapi.plugin.wechat.isv.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lizhen on 2020/09/22.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wechat_isv_contact_translate")
public class WechatIsvContactTranslate {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 任务ID
     */
    @Column(name = "TASK_ID")
    private String taskId;

    /**
     * 企业三方ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * oss文件key
     */
    @Column(name = "OSS_KEY")
    private String ossKey;

    /**
     * 上传微信的文件sf
     */
    @Column(name = "MEDIA_ID")
    private String mediaId;

    /**
     * 企业微信任务ID
     */
    @Column(name = "JOB_ID")
    private String jobId;

    /**
     * 处理状态，0初始化，10已提交微信处理，20处理成功，9999处理失败
     */
    @Column(name = "STATUS")
    private Integer status;

}
