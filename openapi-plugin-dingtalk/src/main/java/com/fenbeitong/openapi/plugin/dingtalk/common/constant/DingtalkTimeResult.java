package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

/**
 * <p>Title: DingtalkTimeResult</p>
 * <p>Description: 钉钉打卡时间结果</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/17 6:04 PM
 */
public enum DingtalkTimeResult {

    /**
     * 未知时间结果类型
     */
    UnKnown(-1, "UnKnown", "未知"),

    /**
     * Normal：正常;
     */
    Normal(1, "Normal", "正常"),

    /**
     * Early：早退;
     */
    Early(2, "Early", "早退"),

    /**
     * Late：迟到;
     */
    Late(3, "Late", "迟到"),

    /**
     * SeriousLate：严重迟到；
     */
    SeriousLate(4, "SeriousLate", "严重迟到"),

    /**
     * Absenteeism：旷工迟到；
     */
    Absenteeism(5, "Absenteeism", "旷工迟到"),

    /**
     * NotSigned：未打卡
     */
    NotSigned(6, "NotSigned", "未打卡");

    private int type;

    private String desc1;

    private String desc2;

    DingtalkTimeResult(int type, String desc1, String desc2) {
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

    public static DingtalkTimeResult getDingtalkTimeResult(String desc1) {
        for (DingtalkTimeResult dingtalkTimeResult : values()) {
            if (dingtalkTimeResult.getDesc1().equals(desc1)) {
                return dingtalkTimeResult;
            }
        }
        return DingtalkTimeResult.UnKnown;
    }
}
