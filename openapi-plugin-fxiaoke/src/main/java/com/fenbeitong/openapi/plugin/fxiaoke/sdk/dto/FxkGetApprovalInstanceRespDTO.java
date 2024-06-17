package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: FxkGetApprovalInstanceReqDTO</p>
 * <p>Description: 纷享销客根据审批实例ID获取审批详情响应信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 7:04 PM
 */
@Data
public class FxkGetApprovalInstanceRespDTO {

    /**
     * 返回码
     */
    private Integer errorCode;

    /**
     * 对返回码的文本描述内容
     */
    private String errorMessage;

    /**
     * 流程实例详情
     */
    private FxkApprovalInstanceDetail instanceDetail;

    @Data
    public static class FxkApprovalInstanceDetail {

        private FxkApprovalInstance instance;

        private List<FxkApprovalTask> tasks;
    }

    @Data
    public static class FxkApprovalTask {

        /**
         * 任务id
         */
        private String id;

        /**
         * 任务类型 单人审批single 多人审批one_pass 会签all_pass
         * 单人审批single:一个人过就过
         * 多人审批one_pass:多个人当中一个人通过就过
         * 会签 ll_pass:所有人都通过才过
         */
        private String type;

        /**
         * 任务状态 进行中 in_progress ;通过 pass;自动通过 auto_pass;拒绝 reject;取消 cancel;回退 go_back;自动回退 auto_go_back;定时 schedule;异常 error
         */
        private String state;

        /**
         * 审批流 apiName
         */
        private String flowApiName;

        /**
         * 创建时间
         */
        private Long createTime;

        /**
         * 最后更新时间
         */
        private Long modifyTime;

        /**
         * 审批意见
         */
        private List<FxkApprovalTaskOpinion> opinions;

        /**
         * 未完成人员列表
         */
        private List<String> unCompletePersons;

        /**
         * 完成时间
         */
        private Long endTime;

        /**
         * 已完成人员列表
         */
        private List<String> completeOpenPersons;
    }

    @Data
    public static class FxkApprovalTaskOpinion {

        private String actionType;

        private String opinion;

        private Long replyTime;

        private String openUserId;
    }
}
