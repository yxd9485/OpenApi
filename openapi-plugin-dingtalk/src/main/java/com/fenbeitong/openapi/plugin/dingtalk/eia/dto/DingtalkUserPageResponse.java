package com.fenbeitong.openapi.plugin.dingtalk.eia.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaokechun
 * @date 2019/3/16 10:21
 */
@Data
public class DingtalkUserPageResponse implements Serializable {

    private boolean hasMore;

    private String errmsg;

    private int errcode;

    List<DingtalkUser> userlist;
}
