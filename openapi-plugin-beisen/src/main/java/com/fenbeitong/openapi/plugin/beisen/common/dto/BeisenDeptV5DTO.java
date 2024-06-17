package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: BeisenDeptV5DTO<p>
 * <p>Description: 北森V5版本部门实体<p>
 * <p>字段描述参照：https://open.italent.cn/#/open-document?menu=document-center&id=dc1e628c-21a6-49cc-ae0c-7fc4a037d1bd <p>
 *
 * @author liuhong
 * @String 2022/9/13 01:46
 */
@Data
public class BeisenDeptV5DTO {
    private String name;
    private String shortName;
    private String code;
    @JsonProperty("oId")
    private Integer oId;
    private String level;
    private Integer status;
    private String establishDate;
    private String startDate;
    private String stopDate;
    private String changeDate;
    @JsonProperty("pOIdOrgAdmin")
    private Integer pOIdOrgAdmin;
    @JsonProperty("pOIdOrgReserve2")
    private Integer pOIdOrgReserve2;
    private Boolean isCurrentRecord;
    private String personInCharge;
    @JsonProperty("hRBP")
    private String hRBP;
    private String shopOwner;
    private String administrativeAssistant;
    private String personInChargeDeputy;
    private Integer businessModifiedBy;
    private String businessModifiedTime;
    private String legalMan;
    private String address;
    private String fax;
    private String postcode;
    private String phone;
    private String url;
    private String description;
    private String number;
    private String broadType;
    private String economicType;
    private String industry;
    private String place;
    private Integer orderAdmin;
    private String orderReserve2;
    private String orderReserve3;
    private String comment;
    @JsonProperty("oIdOrganizationType")
    private String oIdOrganizationType;
    @JsonProperty("pOIdOrgAdmin_TreePath")
    private String pOIdOrgAdmin_TreePath;
    @JsonProperty("pOIdOrgAdmin_TreeLevel")
    private Integer pOIdOrgAdmin_TreeLevel;
    @JsonProperty("pOIdOrgReserve2_TreePath")
    private String pOIdOrgReserve2_TreePath;
    @JsonProperty("pOIdOrgReserve2_TreeLevel")
    private String pOIdOrgReserve2_TreeLevel;
    private Integer firstLevelOrganization;
    private Integer secondLevelOrganization;
    private Integer thirdLevelOrganization;
    private String fourthLevelOrganization;
    private String fifthLevelOrganization;
    private String sixthLevelOrganization;
    private String seventhLevelOrganization;
    private String eighthLevelOrganization;
    @JsonProperty("nIntegerhLevelOrganization")
    private String nIntegerhLevelOrganization;
    private String tenthLevelOrganization;
    private Integer orderCode;
    @JsonProperty("pOIdOrgAdminNameTreePath")
    private String pOIdOrgAdminNameTreePath;
    private String objectId;
    private String customProperties;
    private Integer createdBy;
    private String createdTime;
    private Integer modifiedBy;
    private String modifiedTime;
    private Boolean stdIsDeleted;
}
