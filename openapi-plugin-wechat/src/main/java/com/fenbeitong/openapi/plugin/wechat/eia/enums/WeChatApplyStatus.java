package com.fenbeitong.openapi.plugin.wechat.eia.enums;

import lombok.Getter;

/**
 * 微信审批状态 ：1-审批中；2-已通过；3-已驳回；4-已撤销；6-通过后撤销；7-已删除；10-已支付
 * Created by log.chang on 2019/12/12.
 */
@Getter
public enum WeChatApplyStatus {

    Pass(2, "已通过");

    private int state;
    private String stateDesc;

    WeChatApplyStatus(int state, String stateDesc) {
        this.state = state;
        this.stateDesc = stateDesc;
    }

}
