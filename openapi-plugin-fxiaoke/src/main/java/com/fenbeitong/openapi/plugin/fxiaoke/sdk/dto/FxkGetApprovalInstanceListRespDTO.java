package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: FxkGetApprovalInstanceListRespDTO</p>
 * <p>Description: 分销逍客获取审批列表响应</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 7:33 PM
 */
@Data
public class FxkGetApprovalInstanceListRespDTO {

    /**
     * 返回码
     */
    private Integer errorCode;

    /**
     * 对返回码的文本描述内容
     */
    private String errorMessage;

    /**
     * 查询返回结果数据
     */
    private FxkApprovalInstanceQueryResult queryResult;

    @Data
    public static class FxkApprovalInstanceQueryResult{

        private Integer total;

        private List<FxkApprovalInstance> instanceList;
    }
}
