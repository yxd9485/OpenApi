package com.fenbeitong.openapi.plugin.ecology.v8.standard.constant;

import lombok.Getter;

/**
 * @author zhangpeng
 * @date 2022/01/25
 */
@Getter
@SuppressWarnings("all")
public enum WebHookTaskStatusEnum {

    /**
     * 0:"待处理",10:"已处理",11:"已被他人处理",12:"已同意",13:"已拒绝",14:"已转交",50:"已失效",51:"已撤回",52:"已超时",70:"自动通过",71:"被跳过",72:"被跳转",99:"系统驳回";
     */

    TO_DO("0", "待处理"),
    HANDLED("10", "已处理"),
    HANDLED_BY_OTHER("11", "已被他人处理"),
    AGREE("12", "已同意"),
    REFUSE("13", "已拒绝"),
    GIVE_OTHER("14", "已转交");

    private String type;
    private String desc;

    WebHookTaskStatusEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
