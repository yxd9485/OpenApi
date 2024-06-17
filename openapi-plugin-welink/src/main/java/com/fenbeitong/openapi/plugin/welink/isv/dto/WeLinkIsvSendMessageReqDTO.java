package com.fenbeitong.openapi.plugin.welink.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * create on 2020-04-20 20:41:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeLinkIsvSendMessageReqDTO {

    private Integer msgRange;

    private List<String> toUserList;

    private String msgTitle;

    private String msgContent;

    private String urlType;

    private String urlPath;

    private String msgOwner;

    private String createTime;

}