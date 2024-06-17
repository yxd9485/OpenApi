package com.fenbeitong.openapi.plugin.func.common;

/**
 * @ClassName FuncApplyCategoryEnum
 * @Description 新审批场景类型（驳回、撤销）
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/12 下午3:29
 **/
public enum FuncApplyCategoryEnum {
    CENTER("1","center"),
    MULTI_TRIP("2","multi_trip"),
    TAXI("3","taxi"),
    MALL("4","mall"),
    TRIP("5","trip"),
    ORDER("6","order"),
    FB_COUPON("7","fb_coupon"),
    DINNER("8","dinner"),
    REFUND_CHANGE("9","refund_change"),
    TAKEAWAY("10","takeaway"),
    VIRTUAL_CARD_AMOUNT("11","virtual_card_amount"),
    VIRTUAL_CARD_WRITE_OFF("12","virtual_card_write_off"),
    BUSINESS_ORDER_WRITE_OFF("13","business_order_write_off"),
    PAYMENT("14","payment"),
    PETTY("15","petty"),
    MILEAGE("16","mileage"),
    DIDI_TAXI("17","didi_taxi"),
    CUSTOM_BEFOREHAND("18","custom_beforehand"),
    CUSTOM_REIMBURSEMENT("19","custom_reimbursement");

    FuncApplyCategoryEnum(String type, String category) {
        this.type = type;
        this.category = category;
    }

    private String type;
    private String category;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    //根据type值获取category信息
    public static String getCategoryByType(String type){
        FuncApplyCategoryEnum[] values = FuncApplyCategoryEnum.values();
        for(FuncApplyCategoryEnum categoryEnum:values){
            if(categoryEnum.type.equals(type)){
                return categoryEnum.category;
            }
        }
        return null;
    }
}
