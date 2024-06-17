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
public enum OrderTypeEnum {
    OPEN_ORDER_TYPE_NATIONAL_FLIGHT(40,"国际机票"),
    OPEN_ORDER_TYPE_HOTEL(11,"酒店"),
    OPEN_ORDER_TYPE_TRAIN(15,"火车"),
    OPEN_ORDER_TYPE_INTL_FLIGHT(7,"国内机票"),
    OPEN_ORDER_TYPE_AUTOMOBILE(135,"汽车");

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
        for(OrderTypeEnum orderType: OrderTypeEnum.values()){
            if(typeId==orderType.getTypeId()){
                return orderType.getTypeName();
            }
        }
        return null;
    }
}
