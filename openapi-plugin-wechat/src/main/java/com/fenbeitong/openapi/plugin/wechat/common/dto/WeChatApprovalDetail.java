package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 企业微信审批单详情
 * Created by dave.hansins on 19/12/14.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatApprovalDetail {
    /**
     * 错误信息
     */
    @JsonProperty("errmsg")
    private String errorMsg;
    /**
     * 错误code
     */
    @JsonProperty("errcode")
    private Integer errCode;
    /**
     * 具体审批单信息内容
     */
    @JsonProperty("info")
    private WeChatApprovalInfo weChatApprovalInfo;

    @Data
    public class WeChatApprovalInfo{
        /**
         * 审批编号
         */
        @JsonProperty("sp_no")
        private String spNo;
        /**
         * 审批申请类型名称（审批模板名称）
         */
        @JsonProperty("sp_name")
        private String spName;
        /**
         * 审批模板id
         */
        @JsonProperty("template_id")
        private String templateId;
        /**
         * 申请单状态：1-审批中；2-已通过；3-已驳回；4-已撤销；6-通过后撤销；7-已删除；10-已支付
         */
        @JsonProperty("sp_status")
        private Integer spStatus;
        /**
         * 审批申请提交时间,Unix时间戳
         */
        @JsonProperty("apply_time")
        private Long applyTime;
        @JsonProperty("applyer")
        private Applyer applyer;
        @JsonProperty("sp_record")
        private List<SpRecord> spRecords;
        @JsonProperty("notifyer")
        private List<Notifyer> notifyer;
        @JsonProperty("comments")
        private List<Comments> comments;
        @JsonProperty("apply_data")
        private ApplyData applyData;
    }
    //申请人信息
    @Data
    public static class Applyer {
        /**
         *  申请人userid
         */
        @JsonProperty("userid")
        private String userId;
        /**
         * 申请人所在部门pid
         */
        @JsonProperty("partyid")
        private Long partyId;
    }
    @Data
    public static class SpRecord {
        /**
         * 审批节点状态：1-审批中；2-已同意；3-已驳回；4-已转审
         */
        @JsonProperty("sp_status")
        private Integer spStatus;
        /**
         * 节点审批方式：1-或签；2-会签
          */
        @JsonProperty("approverattr")
        private Integer approverAttr;
        /**
         * 审批节点详情。当节点为标签或上级时，一个节点可能有多个分支
         */
        @JsonProperty("details")
        private List<SpRecordDetails> detailsList;
    }
    //审批节点详情
    @Data
    public static class SpRecordDetails {
        /**
         * 分支审批人
         */
        @JsonProperty("approver")
        private SpRecordDetailsApprover approver;
        /**
         * 审批意见字段
         */
        @JsonProperty("speech")
        private String speech;
        /**
         * 分支审批人审批状态：1-审批中；2-已同意；3-已驳回；4-已转审
         */
        @JsonProperty("sp_status")
        private Integer spStatus;
        /**
         *节点分支审批人审批操作时间，0为尚未操作
         */
        @JsonProperty("sptime")
        private Long spTime;
        /**
         * 节点分支审批人审批意见附件，media_id具体使用请参考：https://work.weixin.qq.com/api/doc#90000/90135/90254
         */
    }

    @Data
    public static class SpRecordDetailsApprover {
        // 分支审批人userid
        @JsonProperty("userid")
        private String userId;
    }
    /**
     * 抄送信息
     */
    @Data
    public static class Notifyer {
        /**
         * 节点抄送人userid
         */
        @JsonProperty("userid")
        private String userId;
    }
    /**
     * 审批具体数据
     */
    @Data
    public static class ApplyData{
        @JsonProperty("contents")
        private List<Content> contens;
    }
    /**
     * 审批具体数据,每个content内容都一样
     */
    @Data
    public static class Content{
        @JsonProperty("control")
        private String control;
        @JsonProperty("id")
        private String id;
        @JsonProperty("title")
        private List<Title> titles;
        /**
         * 每个对象的value值不相同
         */
        @JsonProperty("value")
        private ContentValue contentValue ;
    }
    @Data
    public static class Title{
        @JsonProperty("text")
        private String text;
        @JsonProperty("lang")
        private String lang;
    }
    @Data
    /**
     * content里面的value对象值，每个value对象不相同，根据control值来进行区分
     */
    public static class ContentValue{
        @JsonProperty("selector")
        private Selector selector;
        @JsonProperty("text")
        private String text;
        @JsonProperty("date")
        private Date date;
        @JsonProperty("new_money")
        private String newMoney;
        @JsonProperty("members")
        private List<Member> member;
        @JsonProperty("new_number")
        private String number;
        @JsonProperty("children")
        private List<ChildList> children;
    }

    @Data
    public static class ChildList{
        @JsonProperty("list")
        private List<Content> list;

    }

    @Data
    /**
     * 出发和到达日期对象
     */
    public static class Date{
        @JsonProperty("type")
        private String type;
        @JsonProperty("s_timestamp")
        private long sTimestamp;
    }
    @Data
    /**
     * 选择器，出差种类，机票酒店火车
     */
    public static class Selector{
        @JsonProperty("type")
        private String type;
        @JsonProperty("options")
        private List<Option> options;
    }
    @Data
    public static class Option{
        @JsonProperty("key")
        private String key;
        @JsonProperty("value")
        private List<Value> values;
    }
    @Data
    public static class Value{
        @JsonProperty("text")
        private String text;
        @JsonProperty("lang")
        private String lang;
    }
    @Data
    public static class Member{
        @JsonProperty("userid")
        private String userid;
        @JsonProperty("name")
        private String name;
    }
    @Data
    public static class Comments {
        /**
         * 备注人信息
         */
        @JsonProperty("commentUserInfo")
        private CommentUserInfo commentUserInfo;
        /**
         * 备注提交时间
         */
        @JsonProperty("commenttime")
        private Long commentTime;
        /**
         * 备注文本内容
         */
        @JsonProperty("commentcontent")
        private String commentContent;
        /**
         * 备注id
         */
        @JsonProperty("commentid")
        private String commentId;
    }
    @Data
    public static class CommentUserInfo {
        /**
         * 备注人userid
         */
        @JsonProperty("userid")
        private String userId;
    }
}
