package com.fenbeitong.openapi.plugin.dingtalk.yida.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 宜搭对公付款定制化逻辑类型枚举
 *
 * @author ctl
 * @date 2022/3/4
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("all")
public enum YiDaCustomPaymentTypeEnum {

    /**
     * 与智汇电器相同的表单和逻辑可用此业务类 表单明细中的所有列放在applyReason字段中拼接
     */
    HUIZHI(1, "智汇电器表单"),

    ;

    private Integer type;
    private String desc;

    /**
     * 根据类型获取枚举
     *
     * @param formType
     * @return
     */
    public static YiDaCustomPaymentTypeEnum getEnumByType(Integer formType) {
        if (formType == null) {
            return null;
        }
        for (YiDaCustomPaymentTypeEnum value : YiDaCustomPaymentTypeEnum.values()) {
            if (value.getType().equals(formType)) {
                return value;
            }
        }
        return null;
    }
}
