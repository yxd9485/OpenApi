package com.fenbeitong.openapi.plugin.feishu.common.dto;

import lombok.Data;

/**
 * <p>Title: FeiShuCarFormParseDTO<p>
 * <p>Description: 飞书用车正向表单解析DTO，用于承接解析飞书用车表单后的字段值<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/8/29 15:11
 */
@Data
public class FeiShuCarFormParseDTO {
    /**
     *  申请事由
     */
    private String applyReason;
    /**
     * 开始时间
     */
    private String applyBeginDate;
    /**
     * 结束时间
     */
    private String applyEndDate;
    /**
     * 用车城市
     */
    private String applyCarCity;
    /**
     * 用车次数
     */
    private String carUseCount;
    /**
     * 用车费用
     */
    private String carAmount;

}
