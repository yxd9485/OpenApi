package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName QiqiEmployeeReqDTO
 * @Description 企企人员
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/5/16 下午3:49
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiEmployeeReqDTO {
    @JsonProperty("name")
    private String name;
    @JsonProperty("mobile")
    private String mobile;
    @JsonProperty("department_id")
    private String departmentId;
    @JsonProperty("id")
    private String id;
    @JsonProperty("id_type_id")
    private String idTypeId;
    @JsonProperty("id_number")
    private String idNumber;
    @JsonProperty("gender_id")
    private String genderId;
    @JsonProperty("rank_id")
    private String rankId;
    @JsonProperty("email")
    private String email;
    @JsonProperty("custom_attrs")
    private Map<String, Object> customAttrs;
    @JsonProperty("status_id")
    private String statusId;
    @JsonProperty("code")
    private String code;
    @JsonProperty("birthdate")
    private long birthdate;
    /**
     * 业务停用日期(业务停用日期的时间戳)
     */
    @JsonProperty("disabledTime")
    private Long disabledTime;
    /**
     * 系统停用日期(系统停用日期的时间戳)
     */
    @JsonProperty("systemDisabledTime")
    private Long systemDisabledTime;

    /**
     * 任职记录
     */
    @JsonProperty("jobRelationshipsObject")
    private List<QiqiJobRelationshipDTO> jobRelationshipsObject;

    /**
     * 预算归属人
     */
    @JsonProperty("csYuSuanGuiShuRenCcb5vds0n6f01")
    private String csYuSuanGuiShuRenCcb5vds0n6f01;
}
