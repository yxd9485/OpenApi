package com.fenbeitong.openapi.plugin.dingtalk.yida.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 宜搭表单类型枚举
 *
 * @author ctl
 * @date 2022/3/4
 * @see com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("all")
public enum YiDaFormTypeEnum {

    DEFAULT(0, "暂不支持的类型"),
    PAYMENT(31, "对公付款"),

    ;

    private Integer type;
    private String desc;

    /**
     * 根据类型获取枚举
     *
     * @param formType
     * @return
     */
    public static YiDaFormTypeEnum getEnumByType(Integer formType) {
        if (formType == null) {
            return DEFAULT;
        }
        for (YiDaFormTypeEnum value : YiDaFormTypeEnum.values()) {
            if (value.getType().equals(formType)) {
                return value;
            }
        }
        return DEFAULT;
    }
}
