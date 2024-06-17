package com.fenbeitong.openapi.plugin.seeyon.enums;

/**
 * 待办任务状态
 * @Auther xiaohai
 * @Date 2022/09/25
 */
public enum TaskStautsEnum {

    /**
     * 待办任务状态
     */
    PRNFING(0, null ,"待处理"),
    SOLVED(10, 0 ,"已处理"),
    SOLVED_BY_OTHERS(11, 0 ,"已被他人处理"),
    APPROVED(12, 0 ,"已同意"),
    REFUSED(13, 3 ,"已拒绝"),
    FORWARD(14, 0 ,"已转交"),
    INVALID(50, 2 ,"已失效"),
    REVOKED(51, 2 ,"已撤回"),
    OVERTIME(52, 2 ,"已超时"),
    AUTO_APPROVED(70, 0 ,"自动通过"),
    SKIPPED(71, 2 ,"被跳过"),
    SKIPPED_TO(72, 2 ,"被跳转"),
    SYSTEM_REFUSE_TO(99, 3 ,"系统驳回"),
    DEFAULT_TASKSTATUS(null, 2 ,"默认超时处理");


    private Integer taskStatus;

    private Integer seeyonTaskStatus;

    private String desc;

    TaskStautsEnum(Integer taskStatus,Integer seeyonTaskStatus, String desc) {
        this.taskStatus = taskStatus;
        this.seeyonTaskStatus = seeyonTaskStatus;
        this.desc = desc;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public Integer getSeeyonTaskStatus() {
        return seeyonTaskStatus;
    }

    public String getDesc() {
        return desc;
    }

    public static TaskStautsEnum parse(Integer taskStatus) {
        if (taskStatus == null) {
            return DEFAULT_TASKSTATUS;
        }
        TaskStautsEnum[] itemAry = values();
        for (TaskStautsEnum item : itemAry) {
            if (item.getTaskStatus().equals(taskStatus)) {
                return item;
            }
        }
        return DEFAULT_TASKSTATUS;
    }


}
