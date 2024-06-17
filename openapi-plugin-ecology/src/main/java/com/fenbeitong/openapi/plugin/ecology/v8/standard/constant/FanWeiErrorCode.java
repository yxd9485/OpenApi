package com.fenbeitong.openapi.plugin.ecology.v8.standard.constant;

import java.util.HashSet;
import java.util.Set;

/**
 * 泛微错误类型
 * @Auther zhang.peng
 * @Date 2021/6/22
 */
public enum FanWeiErrorCode {

    CREATE_ERROR(-1,"泛微创建流程失败"),
    NO_AUTH_ERROR(-2,"泛微没有创建权限"),
    CREATE_PROCESS_ERROR(-3,"泛微创建流程失败"),
    FIELD_OR_TABLE_ERROR(-4,"泛微字段或表名不正确"),
    UPDATE_PROCESS_LEVEL_ERROR(-5,"泛微更新流程级别失败"),
    CREATE_TODO_ERROR(-6,"泛微无法创建流程待办任务"),
    NEXT_NODE_ERROR(-7,"泛微流程下一节点出错，请检查流程的配置，在OA中发起流程进行测试"),
    AUTO_ASSIGNMENT_OPERATION_ERROR(-8,"泛微流程节点自动赋值操作错误");

    private int code;

    private String desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    FanWeiErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isError(int code){
        FanWeiErrorCode[] itemAry = values();
        Set<Integer> codeSet = new HashSet<>();
        for (FanWeiErrorCode fanWeiErrorCode : itemAry) {
            codeSet.add(fanWeiErrorCode.getCode());
        }
        if (codeSet.contains(code)){
            return true;
        } else {
            return false;
        }
    }

    public static FanWeiErrorCode parse(Integer key) {
        if (key == null) {
            return null;
        }
        FanWeiErrorCode[] itemAry = values();
        for (FanWeiErrorCode item : itemAry) {
            if (item.getCode() == key) {
                return item;
            }
        }
        return null;
    }
}
