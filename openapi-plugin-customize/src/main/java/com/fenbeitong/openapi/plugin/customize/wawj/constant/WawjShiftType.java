package com.fenbeitong.openapi.plugin.customize.wawj.constant;

/**
 * <p>Title: WawjShiftType</p>
 * <p>Description: 我爱我家班次 类型</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/2/1 2:30 PM
 */
public enum WawjShiftType {

    /**
     * 控股综合班次A+控股综合班次B标准
     */
    KGZH_A_PLUS("控股综合班次A+", 4, false, false),

    /**
     * 控股综合班次A标准
     */
    KGZH_A_STANDARD("控股综合班次A标准", 4, false, false),

    /**
     * 控股综合班次A微弹
     */
    KGZH_A_SHIFT("控股综合班次A微弹", 4, false, false),

    /**
     * 控股综合班次B标准
     */
    KGZH_B_STANDARD("控股综合班次B标准", 3, true, true),

    /**
     * 控股微弹班次
     */
    KGZH_B_SHIFT("控股综合班次B微弹", 3, true, true),

    /**
     * 控股微弹班次
     */
    KGZH_SHIFT("控股微弹班次", 2, true, true),

    /**
     * 控股标准班次
     */
    KGZH_STANDARD("控股标准班次", 2, true, true),


    /**
     * 海外综合班次A标准
     */
    HW_A_STANDARD("海外综合班次A标准", 4, false, false),

    /**
     * 海外综合班次B标准
     */
    HW_B_STANDARD("海外综合班次B标准", 3, true, true),

//    /**
//     * 海外微弹班次
//     */
//    HW_SHIFT("海外微弹班次", 1, true, true),

    /**
     * 海外标准班次
     */
    HW_STANDARD("海外标准班次", 2, true, true),


    /**
     * 新房综合班次A标准
     */
    XF_A_STANDARD("新房综合班次A标准", 4, false, false),

    /**
     * 新房综合班次B标准
     */
    XF_B_STANDARD("新房综合班次B标准", 3, true, true),

//    /**
//     * 新房微弹班次
//     */
//    XF_SHIFT("新房微弹班次", 1, true, true),

    /**
     * 新房标准班次
     */
    XF_STANDARD("新房标准班次", 2, true, true),

    /**
     * 金融综合班次A标准
     */
    JR_A_STANDARD("金融综合班次A标准", 4, false, false),

    /**
     * 金融综合班次B标准
     */
    JR_B_STANDARD("金融综合班次B标准", 3, true, true),

    /**
     * 金融标准班
     */
    JR_STANDARD("金融标准班次", 2, true, true),

    /**
     * 新控股综合班次A微弹
     */
    NEW_KGZH_A_SHIFT("新控股综合班次A微弹", 4, false, false),

    /**
     * 新控股综合班次B微弹
     */
    NEW_KGZH_B_SHIFT("新控股综合班次B微弹", 1, true, false),

    /**
     * 控股常白班（哺乳假-晚来）
     */
    NEW_KGCB_WL("控股常白班（哺乳假-晚来）", 2, true, true),

    /**
     * 控股常白班（哺乳假-早走）
     */
    NEW_KGCB_ZZ("控股常白班（哺乳假-早走）", 2, true, true);


    /**
     * 班次名称
     */
    private String shiftName;

    /**
     * 班次类型 1:弹性班次;2:标准班次;3:管理班次;4:高管班次
     */
    private int shiftType;

    /**
     * 是否用餐
     */
    private boolean useDinner;

    /**
     * 是否用车
     */
    private boolean useCar;

    WawjShiftType(String shiftName, int shiftType, boolean useDinner, boolean useCar) {
        this.shiftName = shiftName;
        this.shiftType = shiftType;
        this.useDinner = useDinner;
        this.useCar = useCar;
    }

    public String getShiftName() {
        return shiftName;
    }

    public int getShiftTypeByDateType(Integer dateType) {
        return shiftType;
    }

    public boolean isUseDinner() {
        return useDinner;
    }

    public boolean isUseCar() {
        return useCar;
    }

    public static WawjShiftType getByName(String shiftName) {
        for (WawjShiftType shiftType : values()) {
            if (shiftType.getShiftName().equals(shiftName)) {
                return shiftType;
            }
        }
        return null;
    }
}
