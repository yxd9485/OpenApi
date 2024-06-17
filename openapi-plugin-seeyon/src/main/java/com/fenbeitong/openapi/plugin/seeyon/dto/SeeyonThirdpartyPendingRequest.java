package com.fenbeitong.openapi.plugin.seeyon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单条待办
 * @author xiaohai
 * @date 2022/09/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeeyonThirdpartyPendingRequest {

    /**
     * 系统注册编码
     */
    private String registerCode;

    /**
     * 第三方待办主键（保证唯一）
     */
    private String taskId;

    /**
     * 待办标题
     */
    private String title;

    /**
     * 第三方待办发起人姓名
     */
    private String senderName;

    /**
     * 状态：0:未办理；1:已办理
     */
    private String state;

    /**
     * 待办创建时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String creationDate;

    /**
     * PC端穿透链接
     */
    private String h5url;

    /**
     * 移动端穿透链接
     */
    private String url;

    /**
     * 第三方待办发起人主键 （需绑定后才可传三方人员id，如果没有绑定用免绑定的两个参数）
     */
    private String thirdSenderId;

    /**
     * 第三方待办接收人主键（需绑定后才可传三方人员id，如果没有绑定用免绑定的两个参数）
     */
    private String thirdReceiverId;

    /**
     * 登录名称/人员编码/手机号/电子邮件 （免绑定，可传四个其中一个，和OA后台配置对应）
     */
    private String  noneBindingSender;

    /**
     * 登录名称/人员编码/手机号/电子邮件（免绑定，可传四个其中一个，和OA后台配置对应）
     */
    private String  noneBindingReceiver;





}
