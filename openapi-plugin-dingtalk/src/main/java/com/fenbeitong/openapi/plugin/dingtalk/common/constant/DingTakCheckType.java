package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

/**
 * <p>Title: DingTakCheckType</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/17 5:38 PM
 */
public enum DingTakCheckType {

    /**
     * 未知打卡类型
     */
    UnKnown("UnKnown"),

    /**
     * 上班打卡
     */
    OnDuty("OnDuty"),

    /**
     * 下班打卡
     */
    OffDuty("OffDuty");

    private String checkType;

    DingTakCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getCheckType() {
        return checkType;
    }

    public static DingTakCheckType getDingTakCheckType(String checkType) {
        for (DingTakCheckType dingTakCheckType : values()) {
            if (dingTakCheckType.getCheckType().equals(checkType)) {
                return dingTakCheckType;
            }
        }
        return DingTakCheckType.UnKnown;
    }
}
