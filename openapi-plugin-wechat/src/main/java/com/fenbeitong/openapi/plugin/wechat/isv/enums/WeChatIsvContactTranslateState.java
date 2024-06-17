package com.fenbeitong.openapi.plugin.wechat.isv.enums;

/**
 * 通讯录转转移状态
 *
 * @author lizhen
 */
public enum WeChatIsvContactTranslateState {

    /**
     * 初始化
     */
    INIT(0),
    /**
     * 微信处理中
     */
    WECHAT_PROCESS(5),

    /**
     * 处理成功
     */
    SUCCESS(10),

    /**
     * 处理失败
     */
    FAILED(50);
    private int code;

    public int getCode() {
        return code;
    }

    WeChatIsvContactTranslateState(int code) {
        this.code = code;
    }
}
