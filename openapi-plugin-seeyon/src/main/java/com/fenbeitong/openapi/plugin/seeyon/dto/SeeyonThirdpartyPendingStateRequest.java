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
public class SeeyonThirdpartyPendingStateRequest {

    /**
     * 系统注册编码
     */
    private String registerCode;

    /**
     * 第三方待办主键（保证唯一）
     */
    private String taskId;

    /**
     * 状态：0:未办理；1:已办理
     */
    private String state;

    /**
     * 处理后状态：0/1/2/3同意已办/不同意已办/取消/驳回
     */
    private String subState;


}
