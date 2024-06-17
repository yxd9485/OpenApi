package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * <p>Title: BeisenEmployeeV5DTO<p>
 * <p>Description: 北森V5版本员工实体 <p>
 * <p> 文档参照：https://open.italent.cn/#/open-document?menu=document-center&id=d2405033-0bb3-4574-ad82-b959799e8882 <p>
 *
 * @author liuhong
 * @String 2022/9/13 16:46
 */
@Data
public class BeisenEmployeeV5DTO {
    /**
     * 外部ID标识，第三方系统唯一标识ID（非北森系统）
     */
    @JsonProperty("originalId")
    private String originalId;
    /**
     * 查询员工信息数据结果
     */
    @JsonProperty("employeeInfo")
    private EmployeeInfo employeeInfo;
    /**
     * 查询员工的任职记录数据结果
     */
    @JsonProperty("recordInfo")
    private RecordInfo recordInfo;

    @Data
    public static class EmployeeInfo{
        private Integer userID;
        private String name;
        @JsonProperty("_Name")
        private String _Name;
        private Integer gender;
        private String email;
        @JsonProperty("iDType")
        private String iDType;
        @JsonProperty("iDNumber")
        private String iDNumber;
        private String birthday;
        private String workDate;
        private String homeAddress;
        private String mobilePhone;
        private String weiXin;
        @JsonProperty("iDPhoto")
        private String iDPhoto;
        private String personalHomepage;
        private String speciality;
        private String major;
        private String postalCode;
        private String passportNumber;
        private String constellation;
        private String bloodType;
        private String residenceAddress;
        private String joinPartyDate;
        private String domicileType;
        private String emergencyContact;
        private String emergencyContactRelationship;
        private String emergencyContactPhone;
        private String qQ;
        private String aboutMe;
        private String homePhone;
        private String graduateDate;
        private String marryCategory;
        private String politicalStatus;
        private Integer nationality;
        private String nation;
        private String birthplace;
        private String registAddress;
        private String educationLevel;
        private String lastSchool;
        private String engName;
        private String officeTel;
        private String businessAddress;
        private String backupMail;
        private String applicantId;
        private String age;
        private Integer businessModifiedBy;
        private String businessModifiedTime;
        private Integer sourceType;
        private String objectId;
        private Integer createdBy;
        private String createdTime;
        private Integer modifiedBy;
        private String modifiedTime;
        private Boolean stdIsDeleted;
        private Map<String,Object> customProperties;
    }
    @Data
    public static class RecordInfo{
        private Integer userID;
        @JsonProperty("pObjectDataID")
        private String pObjectDataID;
        @JsonProperty("oIdDepartment")
        private Integer oIdDepartment;
        private String startDate;
        private String stopDate;
        private String jobNumber;
        private String entryDate;
        private String lastWorkDate;
        private String regularizationDate;
        private String probation;
        private String order;
        private Integer employType;
        private Integer serviceType;
        private Integer serviceStatus;
        private Integer approvalStatus;
        private String employmentSource;
        private String employmentForm;
        private String isCharge;
        @JsonProperty("oIdJobPost")
        private String oIdJobPost;
        @JsonProperty("oIdJobSequence")
        private String oIdJobSequence;
        @JsonProperty("oIdProfessionalLine")
        private String oIdProfessionalLine;
        @JsonProperty("oIdJobPosition")
        private String oIdJobPosition;
        @JsonProperty("oIdJobLevel")
        private String oIdJobLevel;
        @JsonProperty("oidJobGrade")
        private String oidJobGrade;
        private String place;
        private String employeeStatus;
        private String employmentType;
        private String employmentChangeID;
        private String changedStatus;
        @JsonProperty("pOIdEmpAdmin")
        private Integer pOIdEmpAdmin;
        @JsonProperty("pOIdEmpReserve2")
        private String pOIdEmpReserve2;
        private String businessTypeOID;
        private String changeTypeOID;
        private String entryStatus;
        private Boolean isCurrentRecord;
        private String workYearBefore;
        private String workYearGroupBefore;
        private Integer workYearCompanyBefore;
        private String workYearTotal;
        private String workYearGroupTotal;
        private Integer workYearCompanyTotal;
        @JsonProperty("oIdOrganization")
        private Integer oIdOrganization;
        private String whereabouts;
        private String blackStaffDesc;
        private String blackListAddReason;
        private String transitionTypeOID;
        private String changeReason;
        private String probationResult;
        private String probationActualStopDate;
        private String probationStartDate;
        private String probationStopDate;
        private String isHaveProbation;
        private String remarks;
        private String addOrNotBlackList;
        private Integer businessModifiedBy;
        private String businessModifiedTime;
        private String traineeStartDate;
        private String objectId;
        private Integer createdBy;
        private String createdTime;
        private Integer modifiedBy;
        private String modifiedTime;
        private Boolean stdIsDeleted;
        private Map<String,Object> customProperties;

    }



}
