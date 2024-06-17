package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.constant;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @ClassName OrderTypeEnum
 * @Description 订单类型
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/8/18 下午1:42
 **/
@AllArgsConstructor
@NoArgsConstructor
public enum ApplyTypeEnum {
    OPEN_ORDER_TYPE_TRAVEL(1,"差旅"),
    OPEN_ORDER_TYPE_CAR(2,"临时用车"),
    OPEN_ORDER_TYPE_PURCHASE(4,"采购"),
    OPEN_ORDER_TYPE_EXCEED_DINNER(5,"超规用餐"),
    OPEN_ORDER_TYPE_INTL_FLIGHT(6,"国内机票"),
    OPEN_ORDER_TYPE_NATIONAL_FLIGHT(7,"国际机票"),
    OPEN_ORDER_TYPE_HOTEL(8,"酒店"),
    OPEN_ORDER_TYPE_TRAIN(9,"火车"),
    OPEN_ORDER_TYPE_FENBEI_TICKET(10,"分贝券"),
    OPEN_ORDER_TYPE_DINNER(11,"用餐"),
    OPEN_ORDER_TYPE_UNSUB(12,"用车"),
    OPEN_ORDER_TYPE_TAKEAWAY(14,"外卖"),
    OPEN_ORDER_TYPE_VIRTUAL_CARD(15,"个人虚拟卡"),
    OPEN_ORDER_TYPE_WRITE_OFF(16,"核销"),
    OPEN_ORDER_TYPE_BUSINESS_WRITE_OFF(17,"商务核销"),
    OPEN_ORDER_TYPE_PAYMENT(18,"付款"),
    OPEN_ORDER_TYPE_PETTY_CASH(19,"备用金"),
    OPEN_ORDER_TYPE_MILEAG(21,"里程"),
    OPEN_ORDER_TYPE_DIDI_CAR(22,"滴滴企业用车"),
    OPEN_ORDER_TYPE_DEFINE_FORM(24,"自定义表单"),
    OPEN_ORDER_TYPE_AUTOMOBILE(25,"汽车");

    private int typeId;
    private String typeName;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public static String getTypeNameById(int typeId){
        for(ApplyTypeEnum orderType: ApplyTypeEnum.values()){
            if(typeId==orderType.getTypeId()){
                return orderType.getTypeName();
            }
        }
        return null;
    }
}
