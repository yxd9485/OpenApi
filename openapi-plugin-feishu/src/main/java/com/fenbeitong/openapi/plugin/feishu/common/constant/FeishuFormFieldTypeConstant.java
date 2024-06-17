package com.fenbeitong.openapi.plugin.feishu.common.constant;

/**
 * <p>Title: FeishuFormFieldType<p>
 * <p>Description: 飞书表单字段类型<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/4/27 17:44
 */
public interface FeishuFormFieldTypeConstant {
    /**
     * 单行文本
     */
    String INPUT = "input";
    /**
     * 日期区间
     */
    String DATE_INTERVAL = "dateInterval";
    /**
     * 数字
     */
    String NUMBER = "number";
    /**
     * 金额
     */
    String AMOUNT = "amount";
    /**
     * 地址
     */
    String ADDRESS = "address";
    /**
     * 明细(多控件组合)
     */
    String FIELD_LIST = "fieldList";

}
