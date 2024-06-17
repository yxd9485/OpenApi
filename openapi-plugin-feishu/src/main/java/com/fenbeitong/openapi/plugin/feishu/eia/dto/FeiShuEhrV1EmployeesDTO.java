package com.fenbeitong.openapi.plugin.feishu.eia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhiqiang.zhang
 * @date 2022-06-24
 * @description: 飞书人事（标准版） 批量获取员工花名册信息
 * 文档地址： <a href="https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/ehr/ehr-v1/employee/list">...</a>
 */
@NoArgsConstructor
@Data
public class FeiShuEhrV1EmployeesDTO {

    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("items")
        private List<ItemsDTO> items;
        @JsonProperty("page_token")
        private String pageToken;
        @JsonProperty("has_more")
        private Boolean hasMore;

        @NoArgsConstructor
        @Data
        public static class ItemsDTO {
            @JsonProperty("user_id")
            private String userId;
            @JsonProperty("system_fields")
            private SystemFieldsDTO systemFields;
            @JsonProperty("custom_fields")
            private List<CustomFieldsDTO> customFields;

            @NoArgsConstructor
            @Data
            public static class SystemFieldsDTO {
                @JsonProperty("name")
                private String name;
                @JsonProperty("en_name")
                private String enName;
                @JsonProperty("email")
                private String email;
                @JsonProperty("mobile")
                private String mobile;
                @JsonProperty("department_id")
                private String departmentId;
                @JsonProperty("manager")
                private ManagerDTO manager;
                @JsonProperty("job")
                private JobDTO job;
                @JsonProperty("job_level")
                private JobLevelDTO jobLevel;
                @JsonProperty("work_location")
                private WorkLocationDTO workLocation;
                @JsonProperty("gender")
                private Integer gender;
                @JsonProperty("birthday")
                private String birthday;
                @JsonProperty("native_region")
                private NativeRegionDTO nativeRegion;
                @JsonProperty("ethnicity")
                private Integer ethnicity;
                @JsonProperty("marital_status")
                private Integer maritalStatus;
                @JsonProperty("political_status")
                private Integer politicalStatus;
                @JsonProperty("entered_workforce_date")
                private String enteredWorkforceDate;
                @JsonProperty("id_type")
                private Integer idType;
                @JsonProperty("id_number")
                private String idNumber;
                @JsonProperty("hukou_type")
                private Integer hukouType;
                @JsonProperty("hukou_location")
                private String hukouLocation;
                @JsonProperty("bank_account_number")
                private String bankAccountNumber;
                @JsonProperty("bank_name")
                private String bankName;
                @JsonProperty("social_security_account")
                private String socialSecurityAccount;
                @JsonProperty("provident_fund_account")
                private String providentFundAccount;
                @JsonProperty("employee_no")
                private String employeeNo;
                @JsonProperty("employee_type")
                private Integer employeeType;
                @JsonProperty("status")
                private Integer status;
                @JsonProperty("hire_date")
                private String hireDate;
                @JsonProperty("probation_months")
                private Integer probationMonths;
                @JsonProperty("conversion_date")
                private String conversionDate;
                @JsonProperty("application")
                private Integer application;
                @JsonProperty("application_status")
                private Integer applicationStatus;
                @JsonProperty("last_day")
                private String lastDay;
                @JsonProperty("departure_type")
                private Integer departureType;
                @JsonProperty("departure_reason")
                private Integer departureReason;
                @JsonProperty("departure_notes")
                private String departureNotes;
                @JsonProperty("contract_company")
                private ContractCompanyDTO contractCompany;
                @JsonProperty("contract_type")
                private Integer contractType;
                @JsonProperty("contract_start_date")
                private String contractStartDate;
                @JsonProperty("contract_expiration_date")
                private String contractExpirationDate;
                @JsonProperty("contract_sign_times")
                private Integer contractSignTimes;
                @JsonProperty("personal_email")
                private String personalEmail;
                @JsonProperty("family_address")
                private String familyAddress;
                @JsonProperty("primary_emergency_contact")
                private PrimaryEmergencyContactDTO primaryEmergencyContact;
                @JsonProperty("emergency_contact")
                private List<EmergencyContactDTO> emergencyContact;
                @JsonProperty("highest_level_of_edu")
                private HighestLevelOfEduDTO highestLevelOfEdu;
                @JsonProperty("education")
                private List<EducationDTO> education;
                @JsonProperty("former_work_exp")
                private FormerWorkExpDTO formerWorkExp;
                @JsonProperty("work_exp")
                private List<WorkExpDTO> workExp;
                @JsonProperty("id_photo_po_side")
                private List<IdPhotoPoSideDTO> idPhotoPoSide;
                @JsonProperty("id_photo_em_side")
                private List<IdPhotoEmSideDTO> idPhotoEmSide;
                @JsonProperty("id_photo")
                private List<IdPhotoDTO> idPhoto;
                @JsonProperty("diploma_photo")
                private List<DiplomaPhotoDTO> diplomaPhoto;
                @JsonProperty("graduation_cert")
                private List<GraduationCertDTO> graduationCert;
                @JsonProperty("cert_of_merit")
                private List<CertOfMeritDTO> certOfMerit;
                @JsonProperty("offboarding_file")
                private List<OffboardingFileDTO> offboardingFile;
                @JsonProperty("cancel_onboarding_reason")
                private Integer cancelOnboardingReason;
                @JsonProperty("cancel_onboarding_notes")
                private String cancelOnboardingNotes;
                @JsonProperty("employee_form_status")
                private Integer employeeFormStatus;
                @JsonProperty("create_time")
                private Long createTime;
                @JsonProperty("update_time")
                private Long updateTime;

                @NoArgsConstructor
                @Data
                public static class ManagerDTO {
                    @JsonProperty("user_id")
                    private String userId;
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("en_name")
                    private String enName;
                }

                @NoArgsConstructor
                @Data
                public static class JobDTO {
                    @JsonProperty("id")
                    private Long id;
                    @JsonProperty("name")
                    private String name;
                }

                @NoArgsConstructor
                @Data
                public static class JobLevelDTO {
                    @JsonProperty("id")
                    private Long id;
                    @JsonProperty("name")
                    private String name;
                }

                @NoArgsConstructor
                @Data
                public static class WorkLocationDTO {
                    @JsonProperty("id")
                    private Long id;
                    @JsonProperty("name")
                    private String name;
                }

                @NoArgsConstructor
                @Data
                public static class NativeRegionDTO {
                    @JsonProperty("iso_code")
                    private String isoCode;
                    @JsonProperty("name")
                    private String name;
                }

                @NoArgsConstructor
                @Data
                public static class ContractCompanyDTO {
                    @JsonProperty("id")
                    private Long id;
                    @JsonProperty("name")
                    private String name;
                }

                @NoArgsConstructor
                @Data
                public static class PrimaryEmergencyContactDTO {
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("relationship")
                    private Integer relationship;
                    @JsonProperty("mobile")
                    private String mobile;
                }

                @NoArgsConstructor
                @Data
                public static class HighestLevelOfEduDTO {
                    @JsonProperty("level")
                    private Integer level;
                    @JsonProperty("school")
                    private String school;
                    @JsonProperty("major")
                    private String major;
                    @JsonProperty("degree")
                    private Integer degree;
                    @JsonProperty("start")
                    private String start;
                    @JsonProperty("end")
                    private String end;
                }

                @NoArgsConstructor
                @Data
                public static class FormerWorkExpDTO {
                    @JsonProperty("company")
                    private String company;
                    @JsonProperty("department")
                    private String department;
                    @JsonProperty("job")
                    private String job;
                    @JsonProperty("start")
                    private String start;
                    @JsonProperty("end")
                    private String end;
                    @JsonProperty("description")
                    private String description;
                }

                @NoArgsConstructor
                @Data
                public static class EmergencyContactDTO {
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("relationship")
                    private Integer relationship;
                    @JsonProperty("mobile")
                    private String mobile;
                }

                @NoArgsConstructor
                @Data
                public static class EducationDTO {
                    @JsonProperty("level")
                    private Integer level;
                    @JsonProperty("school")
                    private String school;
                    @JsonProperty("major")
                    private String major;
                    @JsonProperty("degree")
                    private Integer degree;
                    @JsonProperty("start")
                    private String start;
                    @JsonProperty("end")
                    private String end;
                }

                @NoArgsConstructor
                @Data
                public static class WorkExpDTO {
                    @JsonProperty("company")
                    private String company;
                    @JsonProperty("department")
                    private String department;
                    @JsonProperty("job")
                    private String job;
                    @JsonProperty("start")
                    private String start;
                    @JsonProperty("end")
                    private String end;
                    @JsonProperty("description")
                    private String description;
                }

                @NoArgsConstructor
                @Data
                public static class IdPhotoPoSideDTO {
                    @JsonProperty("id")
                    private String id;
                    @JsonProperty("mime_type")
                    private String mimeType;
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("size")
                    private Integer size;
                }

                @NoArgsConstructor
                @Data
                public static class IdPhotoEmSideDTO {
                    @JsonProperty("id")
                    private String id;
                    @JsonProperty("mime_type")
                    private String mimeType;
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("size")
                    private Integer size;
                }

                @NoArgsConstructor
                @Data
                public static class IdPhotoDTO {
                    @JsonProperty("id")
                    private String id;
                    @JsonProperty("mime_type")
                    private String mimeType;
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("size")
                    private Integer size;
                }

                @NoArgsConstructor
                @Data
                public static class DiplomaPhotoDTO {
                    @JsonProperty("id")
                    private String id;
                    @JsonProperty("mime_type")
                    private String mimeType;
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("size")
                    private Integer size;
                }

                @NoArgsConstructor
                @Data
                public static class GraduationCertDTO {
                    @JsonProperty("id")
                    private String id;
                    @JsonProperty("mime_type")
                    private String mimeType;
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("size")
                    private Integer size;
                }

                @NoArgsConstructor
                @Data
                public static class CertOfMeritDTO {
                    @JsonProperty("id")
                    private String id;
                    @JsonProperty("mime_type")
                    private String mimeType;
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("size")
                    private Integer size;
                }

                @NoArgsConstructor
                @Data
                public static class OffboardingFileDTO {
                    @JsonProperty("id")
                    private String id;
                    @JsonProperty("mime_type")
                    private String mimeType;
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("size")
                    private Integer size;
                }
            }

            @NoArgsConstructor
            @Data
            public static class CustomFieldsDTO {
                @JsonProperty("key")
                private String key;
                @JsonProperty("label")
                private String label;
                @JsonProperty("type")
                private String type;
                @JsonProperty("value")
                private String value;
            }
        }
    }
}
