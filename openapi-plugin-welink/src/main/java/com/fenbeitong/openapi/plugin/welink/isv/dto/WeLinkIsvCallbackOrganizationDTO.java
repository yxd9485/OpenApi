package com.fenbeitong.openapi.plugin.welink.isv.dto;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.Data;

import java.util.List;

/**
 * 组织机构人员回调dto
 */
@Data
public class WeLinkIsvCallbackOrganizationDTO extends WeLinkIsvCallbackReqDTO {

    private List<CallbackOrganizationInfo> data;

    @Data
    public static class CallbackOrganizationInfo {

        private String deptCode;

        private String tenantId;

    }

}
