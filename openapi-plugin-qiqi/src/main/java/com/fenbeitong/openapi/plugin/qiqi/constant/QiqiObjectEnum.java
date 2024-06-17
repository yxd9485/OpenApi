package com.fenbeitong.openapi.plugin.qiqi.constant;

import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCustomArchiveReqDTO;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;

/**
 * @ClassName QiqiObjectTypeEnum
 * @Description 目标对象枚举
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/19
 **/
public enum QiqiObjectEnum {

    /**
     * 自定义档案class对象
     */
    BUDGET_ACCOUNT_CLASS("BudgetAccount", QiqiCustomArchiveReqDTO.class);

    private String code;
    private Class desc;

    public static QiqiObjectEnum parse(String key) {
        if (key == null) {
            return null;
        }
        QiqiObjectEnum[] itemAry = values();
        for (QiqiObjectEnum item : itemAry) {
            if (item.getCode().equals(key)) {

                return item;
            }
        }
        return null;
    }

    QiqiObjectEnum(String code, Class desc) {
        this.code = code;
        this.desc = desc;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Class getDesc() {
        return desc;
    }

    public void setDesc(Class desc) {
        this.desc = desc;
    }
}
