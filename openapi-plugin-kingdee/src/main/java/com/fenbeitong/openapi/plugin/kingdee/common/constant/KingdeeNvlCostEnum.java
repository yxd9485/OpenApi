package com.fenbeitong.openapi.plugin.kingdee.common.constant;

import lombok.Getter;

/**
 * module: 婼薇乐<br/>
 * <p>
 * description: 婼薇乐费用项目码表<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/10/14 18:25
 */
@Getter
public enum KingdeeNvlCostEnum {
    /**
     * 快递
     */
    EXPRESS("CI037","6","快递费"),

    /**
     * 办公费
     */
    OFFICE("FYXM14_SYS","13","办公费");


    private final String code;

    private final String rate;

    private final String name;

    KingdeeNvlCostEnum(String code,String rate,String name){
        this.code = code;
        this.rate = rate;
        this.name = name;
    }
}
