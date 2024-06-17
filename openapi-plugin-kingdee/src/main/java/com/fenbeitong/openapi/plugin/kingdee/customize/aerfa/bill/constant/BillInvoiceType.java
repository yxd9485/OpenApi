package com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.constant;

/**
 * @ClassName BillInvoiceType
 * @Description 金蝶账单发票类型
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/9/14 下午5:42
 **/
public enum BillInvoiceType {

    AIR_INVOICE("P", 7),
    TRAIN_INVOICE("R", 15),
    HOTEL_INVOICE("1", 11),
    EXPRESS_INVOICE("1", 130),
    EXPRESSDELIVERY_INVOICE("1",131),
    Taxi_INVOICE("0",3),
    Dinner_INVOICE("0",30),
    MEISHI_INVOICE("0",60),
    TAKEOUT_INVOICE("0",50),
    ERROR_INVOICE("E",-1);


    private final String key;
    private final Integer value;


    BillInvoiceType(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 根据场景关联发票类型
     */
    public static  String getKingdeeInvoiceType(Integer invoiceType) {
        for (BillInvoiceType billInvoiceType : BillInvoiceType.values()) {
            if (billInvoiceType.value.equals(invoiceType)) {
                return billInvoiceType.key;
            }
        }
        return BillInvoiceType.ERROR_INVOICE.key;
    }

    public String getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

}
