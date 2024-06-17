package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.Data;

import java.util.Map;

/**
 * <p>Title: FxkGetCustomDataRespDTO</p>
 * <p>Description: 纷享销客获取自定义对象数据详情响应</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 7:44 PM
 */
@Data
public class FxkGetCustomDataRespDTO {

    /**
     * 返回码
     */
    private Integer errorCode;

    /**
     * 对返回码的文本描述内容
     */
    private String errorMessage;

    /**
     * 数据集
     */
    private Map data;
}
