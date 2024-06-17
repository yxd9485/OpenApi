package com.fenbeitong.openapi.plugin.func.callback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallbackCommonDTO {
    /**
     * 详情类型 1为订单2为审批
     */
    private Integer type;
    /**
     * 如果为订单时就是订单号如果是审批单时就是审批单号
     */
    private String dataId;
    /**
     * 详情的map
     */
    private Map<String,Object> data;
}
