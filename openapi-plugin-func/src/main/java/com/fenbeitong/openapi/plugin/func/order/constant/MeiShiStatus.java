package com.fenbeitong.openapi.plugin.func.order.constant;

/**
 * <p>Title: MeiShiStatus</p>
 * <p>Description: 美食状态枚举</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/2 4:11 PM
 */
@SuppressWarnings("all")
public enum MeiShiStatus {

    MeishiUnpaid(1, "待付款"),

    MeishiCancelled(82, "已取消"),

    MeishiClosed(81, "已关闭"),

    MeishiPaid(2, "已支付"),

    MeishiFinish(80, "已完成"),

    MeishiFail(21, "发货失败"),

    MeishiRefundNoQuit(0, "无退款"),

    MeishiRefundQuiting(90, "退款中"),

    MeishiRefundQuited(95, "退款完成"),

    MeishiUnKnown(-1, "未知"),

    DELETE(-256, "已删除");

    private int key;
    private String value;

    MeiShiStatus(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static MeiShiStatus getByKey(Integer key) {
        if (key == null) {
            return MeishiUnKnown;
        }
        for (MeiShiStatus type : values()) {
            if (type.getKey() == key) {
                return type;
            }
        }
        return MeishiUnKnown;
    }
}
