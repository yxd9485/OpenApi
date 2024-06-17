package com.fenbeitong.openapi.plugin.feishu.eia.constant;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @ClassName APPLYTypeEnum
 * @Description 申请单类型
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/8/18 下午1:42
 **/
@AllArgsConstructor
@NoArgsConstructor
public enum ApplyCategoryEnum {
    OPEN_APPLY_TYPE_TRAVEL(1,"行程"),
    OPEN_APPLY_TYPE_APPLY(2,"订单"),
    OPEN_APPLY_TYPE_PURCHASE(3,"采购"),
    OPEN_APPLY_TYPE_FENBEI_TICKET(4,"分贝券"),
    OPEN_APPLY_TYPE_DINNER(5,"用餐"),
    OPEN_APPLY_TYPE_RETREAT_REFORM(6,"退改"),
    OPEN_APPLY_TYPE_TAKEAWAY(7,"外卖"),
    OPEN_APPLY_TYPE_VIRTUAL_CARD(8,"虚拟卡"),
    OPEN_APPLY_TYPE_WRITE_OFF(9,"核销"),
    OPEN_APPLY_TYPE_BUSINESS(10,"商务"),
    OPEN_APPLY_TYPE_PAYMENT(11,"付款"),
    OPEN_APPLY_TYPE_PETTY_CASH(12,"备用金"),
    OPEN_APPLY_TYPE_MILEAG(13,"里程");

    private Integer typeId;
    private String typeName;

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public static String getTypeNameById(Integer typeId){
        for(ApplyCategoryEnum APPLYType: ApplyCategoryEnum.values()){
            if(typeId==APPLYType.getTypeId()){
                return APPLYType.getTypeName();
            }
        }
        return null;
    }
}
