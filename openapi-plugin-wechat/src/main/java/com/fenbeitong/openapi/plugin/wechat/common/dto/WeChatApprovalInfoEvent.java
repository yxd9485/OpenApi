package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

/**
 * 微信审批回调事件
 * Created by log.chang on 2019/12/12.
 */
@Data
@XStreamAlias("xml")
public class WeChatApprovalInfoEvent {

    @XStreamAlias("ToUserName")
    private String toUserName;
    @XStreamAlias("FromUserName")
    private String fromUserName;
    @XStreamAlias("CreateTime")
    private Long createTime;
    @XStreamAlias("MsgType")
    private String msgType;
    @XStreamAlias("Event")
    private String event;
    @XStreamAlias("AgentID")
    private String agentID;
    @XStreamAlias("ApprovalInfo")
    private ApprovalInfo approvalInfo;

    @Data
    public static class ApprovalInfo {

        // 审批编号
        @XStreamAlias("SpNo")
        private String spNo;
        // 审批申请类型名称（审批模板名称）
        @XStreamAlias("SpName")
        private String spName;
        // 申请单状态：1-审批中；2-已通过；3-已驳回；4-已撤销；6-通过后撤销；7-已删除；10-已支付
        @XStreamAlias("SpStatus")
        private Integer spStatus;
        // 审批模板id。可在“获取审批申请详情”、“审批状态变化回调通知”中获得，也可在审批模板的模板编辑页面链接中获得。
        @XStreamAlias("TemplateId")
        private String templateId;
        // 审批申请提交时间,Unix时间戳
        @XStreamAlias("ApplyTime")
        private Long applyTime;
        // 审批申请状态变化类型：1-提单；2-同意；3-驳回；4-转审；5-催办；6-撤销；8-通过后撤销；10-添加备注
        @XStreamAlias("StatuChangeEvent")
        private Integer statuChangeEvent;
        // 申请人信息
        @XStreamAlias("Applyer")
        private Applyer applyer;
        // 审批流程信息，可能有多个审批节点。
        @XStreamImplicit
        private List<SpRecord> spRecordList;
        // 抄送信息，可能有多个抄送节点
        @XStreamImplicit
        private List<Notifyer> notifyer;
        // 审批申请备注信息，可能有多个备注节点
        @XStreamImplicit
        private List<Comments> comments;

    }

    @Data
    public static class Applyer {
        // 申请人userid
        @XStreamAlias("UserId")
        private String userId;
        // 申请人所在部门pid
        @XStreamAlias("Party")
        private Integer party;
    }

    @Data
    @XStreamAlias("SpRecord")
    public static class SpRecord {
        // 审批节点状态：1-审批中；2-已同意；3-已驳回；4-已转审
        @XStreamAlias("SpStatus")
        private Integer spStatus;
        // 节点审批方式：1-或签；2-会签
        @XStreamAlias("ApproverAttr")
        private Integer approverAttr;
        // 审批节点详情。当节点为标签或上级时，一个节点可能有多个分支
        @XStreamImplicit
        private List<SpRecordDetails> detailsList;
    }

    @Data
    @XStreamAlias("Details")
    public static class SpRecordDetails {
        // 分支审批人
        @XStreamAlias("Approver")
        private SpRecordDetailsApprover approver;
        // 审批意见字段
        @XStreamAlias("Speech")
        private String speech;
        // 分支审批人审批状态：1-审批中；2-已同意；3-已驳回；4-已转审
        @XStreamAlias("SpStatus")
        private Integer spStatus;
        // 节点分支审批人审批操作时间，0为尚未操作
        @XStreamAlias("SpTime")
        private Long spTime;
        // 节点分支审批人审批意见附件，media_id具体使用请参考：https://work.weixin.qq.com/api/doc#90000/90135/90254
        @XStreamAlias("MediaId")
        private String mediaId;
    }

    @Data
    public static class SpRecordDetailsApprover {
        // 分支审批人userid
        @XStreamAlias("UserId")
        private String userId;
    }

    @Data
    @XStreamAlias("Notifyer")
    public static class Notifyer {
        // 节点抄送人userid
        @XStreamAlias("UserId")
        private String userId;
    }

    @Data
    @XStreamAlias("Comments")
    public static class Comments {
        // 备注人信息
        @XStreamAlias("CommentUserInfo")
        private CommentUserInfo commentUserInfo;
        // 备注提交时间
        @XStreamAlias("CommentTime")
        private Long commentTime;
        // 备注文本内容
        @XStreamAlias("CommentContent")
        private String commentContent;
        // 备注id
        @XStreamAlias("CommentId")
        private String commentId;
    }

    @Data
    public static class CommentUserInfo {
        // 备注人userid
        @XStreamAlias("UserId")
        private String userId;
    }

}
