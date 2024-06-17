package com.fenbeitong.openapi.plugin.func.order.constant;

/**
 * <p>Title: MeiShiBizType</p>
 * <p>Description: 美食业务类型</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/2 3:30 PM
 */
public enum  MeiShiBizType {

    /**
     * 团购
     */
    BizType_group(1, "团购"),

    /**
     * 买单
     */
    BizType_pay(11, "买单"),

    /**
     * 美团外卖
     */
    BizType_meituan_take(206, "美团外卖"),

    /**
     * 美团团购
     */
    BizType_meituan_group(101, "美团团购");

    private int key;

    private String value;

    MeiShiBizType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static MeiShiBizType getByKey(Integer key) {
        if (key == null) {
            return null;
        }
        for (MeiShiBizType type : values()) {
            if (type.getKey() == key) {
                return type;
            }
        }
        return null;
    }
}
