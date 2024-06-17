package com.fenbeitong.openapi.plugin.voucher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by huangsiyuan on 2021/09/28.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomizeVoucherDTO {
    /**
     * 账单编号
     */
    @JsonProperty("bill_no")
    private String billNo;
    /**
     * 状态: 1:生成中;2:生成失败;3:生成成功
     */
    @JsonProperty("status")
    private Integer status;
    /**
     * excel url地址
     */
    @JsonProperty("url")
    private String url;
    /**
     * 创建时间
     */
    @JsonProperty("create_at")
    private Long createAt;
    /**
     * 创建人ID
     */
    @JsonProperty("create_by")
    private String createBy;
    /**
     * 创建人名称
     */
    @JsonProperty("create_name")
    private String createName;
    /**
     * 更新时间
     */
    @JsonProperty("update_at")
    private Long updateAt;
    /**
     * 更新人ID
     */
    @JsonProperty("update_by")
    private String updateBy;
    /**
     * 更新人名称
     */
    @JsonProperty("update_name")
    private String updateName;
}
