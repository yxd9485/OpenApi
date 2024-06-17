package com.fenbeitong.openapi.plugin.welink.isv.dto;

import lombok.Data;

import java.util.List;

/**
 * 组织机构人员回调dto
 */
@Data
public class WeLinkIsvCallbackEmployeeDTO extends WeLinkIsvCallbackReqDTO {

    private List<CallbackEmployeeInfo> data;

    @Data
    public static class CallbackEmployeeInfo {

        private String userId;

        private String tenantId;

    }
}
