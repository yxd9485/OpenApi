package com.fenbeitong.openapi.plugin.qiqi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName QiqiMessageBodyDTO
 * @Description 企企消息实体
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/26
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiMessageBodyDTO {
    private String tenantId;
    private String objectName;
    private String objectId;
    private String operation;
    private String eventId;
    private String entrySrcSystemId;
    private String createdOrgId;
    private String flowTypeId;
    private Variables variables;

    @Data
    public static class Variables {
        private Map<String,String> lastUserId;
    }
}
