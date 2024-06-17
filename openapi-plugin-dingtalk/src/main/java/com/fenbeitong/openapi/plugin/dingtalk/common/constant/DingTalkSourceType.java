package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

/**
 * <p>Title: DingTalkSourceType</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/17 6:20 PM
 */
public enum DingTalkSourceType {

    /**
     * 未知时间结果类型
     */
    UnKnown("UnKnown", "未知"),

    /**
     * ATM：考勤机;
     */
    ATM("ATM", "考勤机"),

    /**
     * BEACON：IBeacon
     */
    BEACON("BEACON", "IBeacon"),

    /**
     * DING_ATM：钉钉考勤机;
     */
    DING_ATM("DING_ATM", "钉钉考勤机"),

    /**
     * USER：用户打卡;
     */
    USER("USER", "用户打卡"),

    /**
     * BOSS：老板改签;
     */
    BOSS("BOSS", "老板改签"),

    /**
     * APPROVE：审批系统;
     */
    APPROVE("APPROVE", "审批系统"),

    /**
     * SYSTEM：考勤系统;
     */
    SYSTEM("SYSTEM", "考勤系统"),

    /**
     * AUTO_CHECK：自动打卡
     */
    AUTO_CHECK("AUTO_CHECK", "自动打卡");


    private String desc1;

    private String desc2;

    DingTalkSourceType(String desc1, String desc2) {
        this.desc1 = desc1;
        this.desc2 = desc2;
    }

    public String getDesc1() {
        return desc1;
    }

    public String getDescription() {
        return desc1 + ":" + desc2;
    }

    public static DingTalkSourceType getDingTalkSourceType(String desc1) {
        for (DingTalkSourceType dingTalkSourceType : values()) {
            if (dingTalkSourceType.getDesc1().equals(desc1)) {
                return dingTalkSourceType;
            }
        }
        return DingTalkSourceType.UnKnown;
    }
}
