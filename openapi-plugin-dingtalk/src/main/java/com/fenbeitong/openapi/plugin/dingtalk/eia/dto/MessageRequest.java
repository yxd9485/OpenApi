package com.fenbeitong.openapi.plugin.dingtalk.eia.dto;

import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkMsgType;
import lombok.Data;

import java.io.Serializable;

/**
 * 钉钉消息请求
 *
 * @author zhaokechun
 * @date 2018/11/27 16:54
 */
@Data
public class MessageRequest implements Serializable {

    /**
     * 分贝通企业ID
     */
    private String companyId;

    /**
     * 接收员工ID，多个用逗号分隔
     */
    private String employeeIds;

    /**
     * 消息类型 {@link DingtalkMsgType}
     */
    private String msgType;

    /**
     * 消息内容, json格式
     * {@link Text}
     * {@link Link}
     */
    private String msg;

    @Data
    public static class Text {

        private String content;

    }

    @Data
    public static class Link {

        private String title;

        private String text;

        private String messageUrl;

        private String picUrl;

    }
}


