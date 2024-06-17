package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: FxkGetApprovalInstanceListReqDTO</p>
 * <p>Description: 分销逍客获取审批列表参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 7:33 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxkGetApprovalInstanceListReqDTO {

    /**
     * 企业应用访问公司合法性凭证
     */
    private String corpAccessToken;

    /**
     * 开放平台公司账号
     */
    private String corpId;

    /**
     * 当前操作人的openUserId
     */
    private String currentOpenUserId;

    /**
     * 审批流程 apiName
     */
    private String flowApiName;

    /**
     * 流程状态 流程实例状态 in_progress 进行中,pass 通过,error 异常,cancel 取消,reject 拒绝
     */
    private String state;

    /**
     * 开始时间(时间戳形式)
     */
    private Long startTime;

    /**
     * 结束时间(时间戳形式)
     */
    private Long endTime;

    /**
     * 数据对象的 apiName
     */
    private String objectApiName;

    /**
     * 页码默认为 1
     */
    private Integer pageNumber;

    /**
     * 分页大小默认 20
     */
    private Integer pageSize;
}
