package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

/**
 * <p>Title: DingtalkLocationResult</p>
 * <p>Description: 钉钉打卡位置结果</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/17 6:04 PM
 */
public enum DingtalkLocationResult {

    /**
     * 未知位置结果类型
     */
    UnKnown(-1, "UnKnown", "未知"),

    /**
     * Normal：范围内；
     */
    Normal(1, "Normal", "范围内"),

    /**
     * Outside：范围外；
     */
    Early(2, "Outside", "范围外"),

    /**
     * NotSigned：未打卡
     */
    Late(3, "NotSigned", "未打卡");

    private int type;

    private String desc1;

    private String desc2;

    DingtalkLocationResult(int type, String desc1, String desc2) {
        this.type = type;
        this.desc1 = desc1;
        this.desc2 = desc2;
    }

    public int getType() {
        return type;
    }

    public String getDesc1() {
        return desc1;
    }

    public String getDescription() {
        return desc1 + ":" + desc2;
    }

    public static DingtalkLocationResult getDingtalkLocationResult(String desc1) {
        for (DingtalkLocationResult dingtalkLocationResult : values()) {
            if (dingtalkLocationResult.getDesc1().equals(desc1)) {
                return dingtalkLocationResult;
            }
        }
        return DingtalkLocationResult.UnKnown;
    }
}
