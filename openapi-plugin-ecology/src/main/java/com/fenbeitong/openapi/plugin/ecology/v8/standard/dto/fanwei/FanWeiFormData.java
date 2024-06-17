package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 泛微表单
 * @Auther zhang.peng
 * @Date 2021/5/25
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FanWeiFormData {

    /**
     * 流程名称
     */
    private String workflowName;

    /**
     * 流程id
     */
    private String workflowId;

    /**
     * 流程类型
     */
    private String workflowTypeId;

    /**
     * 流程类型名称
     */
    private String workflowTypeName;
}
