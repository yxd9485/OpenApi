package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 详细信息参照https://open.yunzhijia.com/openplatform/resourceCenter/doc#/gitbook-wiki/server-api/pubSend.html
 */
@Data
@Builder
public class YunzhijiaMsgReqDTO {
    //发送方信息，格式为JSON对象
    private From from;
    //接收方信息，格式为包含一至多个接收方信息JSON对象的JSON数组
    private List<To> to;
    //消息类型，格式为整型",(取值 2：单文本,5：文本链接,6：图文链接)
    private int type;
    //发布到讯通的消息内容，格式为JSON对象
    private Text msg;

    /**
     * 发送方数据
     */
    @Data
    @Builder
    public static class From {
        //发送方企业的企业注册号(eid)，格式为字符串
        private String no;
        //发送使用的公共号ID，格式为字符串
        private String pub;
        //发送时间，为'currentTimeMillis()以毫秒为单位的当前时间'的字符串或数字
        private long time;
        //随机数，格式为字符串或数字
        private String nonce;
        //公共号加密串，格式为字符串
        private String pubtoken;
    }

    /**
     * 接收方数据
     */
    @Data
    @Builder
    public static class To {
        //接收方企业的企业注册号(eID)，格式为字符串"
        private String no;
        //接收方的用户ID，格式为包含OPENID的JSON数组
        private List<String> user;
    }

    @Data
    @Builder
    public static class Text {
        //文本消息内容，格式为字符串
        private String text;
        private String url ;       //"文本链接地址，格式为经过URLENCODE编码的字符串",
        private String appid ;     // "如果想让链接地址携带用户身份（ticket），请传入轻应用ID（appid）",
        private int todo ;      //"int，必填,暂时只能为0，表示推送原公共号消息",
        private String sourceid ;  //"备用字段，暂时无用"
    }
}
