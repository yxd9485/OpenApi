package com.fenbeitong.openapi.plugin.yunzhijia.enums;


/**
 * 不同消息事件对应着相同的分贝通消息事件，修改消息可分为不同的类型，同步至分贝通时则统一合并为同一个消息类型
 */
public enum YunzhijiaTaskType {
    /**
     * 通讯录用户增加
     */
    YUNZHIJIA_USER_ADD_ORG("user_enter", "yunzhijia_org_user_add"),
    /**
     * 通讯录用户更改
     */
    YUNZHIJIA_USER_MODIFY_ORG("person_update", "yunzhijia_org_user_modify"),
    /**
     * 通讯录用户更改手机号
     */
    YUNZHIJIA_USER_MODIFY_PHONE("user_phone_update", "yunzhijia_org_user_modify"),
    /**
     * 通讯录人员角色变更
     */
    YUNZHIJIA_USER_MODIFY_ROLE("person_role", "yunzhijia_org_user_modify"),
    /**
     * 通讯录用户离职
     */
    YUNZHIJIA_USER_LEAVE_ORG("user_leave", "yunzhijia_org_user_leave"),
    /**
     * 通讯录企业部门创建
     */
    YUNZHIJIA_ORG_DEPT_CREATE("org_add", "yunzhijia_org_dept_add"),
    /**
     * 通讯录企业部门修改
     */
    YUNZHIJIA_ORG_DEPT_MODIFY("org_update", "yunzhijia_org_dept_modify"),
    /**
     * 通讯录部门负责人变更
     */
    YUNZHIJIA_ORG_DEPT_LEADER_MODIFY("org_admin", "yunzhijia_org_dept_leader"),

    /**
     * 通讯录企业部门删除
     */
    YUNZHIJIA_ORG_DEPT_REMOVE("org_delete", "yunzhijia_org_dept_remove");

//    YUNZHIJIA_BPMS_INSTANCE_CHANGE("bpms_instance_change", "审批实例开始，结束");

    private final String key;
    private final String value;

    YunzhijiaTaskType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static YunzhijiaTaskType parse(String key) {
        if (key == null) {
            return null;
        }
        YunzhijiaTaskType[] itemAry = values();
        for (YunzhijiaTaskType item : itemAry) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }


}
