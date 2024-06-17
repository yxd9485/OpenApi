package com.fenbeitong.openapi.plugin.welink.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-04-15 10:15:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeLinkIsvTenantsInfoRespDTO {

    private String code;

    private String message;

    private ResponseData data;

    @Data
    public static class ResponseData {

        private String tenantId;

        private String companyNameCn;

        private String companyNameEn;

        private String companyContactName;

        private Integer registeredNumbers;

        private Integer tenantType;

        private String companyDomainName;

        private Integer companyScale;

        private String licenseStartTime;

        private String licenseEndTime;

        private String creationTime;

        private String lastUpdatedTime;

    }
}