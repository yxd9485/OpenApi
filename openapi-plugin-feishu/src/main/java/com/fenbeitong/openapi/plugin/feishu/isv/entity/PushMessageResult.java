package com.fenbeitong.openapi.plugin.feishu.isv.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhang on 2021/01/14.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "push_message_result")
public class PushMessageResult implements Serializable {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 场景类型
     */
    @Column(name = "open_type")
    private String openType;

    /**
     * 用户的三方平台id
     */
    @Column(name = "third_employee_id")
    private String thirdEmployeeId;

    /**
     * 用户在分贝通的ID
     */
    @Column(name = "employee_id")
    private String employeeId;

    /**
     * 用户在分贝通的公司ID
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 用户在三方平台的ID
     */
    @Column(name = "corp_id")
    private String corpId;

    /**
     * 发送的json内容
     */
    @Column(name = "send_content")
    private String sendContent;

    /**
     * 发送消息的类型
     */
    @Column(name = "msg_type")
    private String msgType;

    /**
     * 是否发送成功  1是成功 0是失败
     */
    @Column(name = "send_success")
    private Integer sendSuccess;

    /**
     * 发送失败次数
     */
    @Column(name = "fail_num")
    private Integer failNum;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 消息ID
     */
    @Column(name ="message_id")
    private Integer messageId;

}
