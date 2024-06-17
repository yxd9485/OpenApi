package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: BeisenRankV5DTO<p>
 * <p>Description: 北森V5版本职级实体<p>
 * <p>字段描述参照： https://open.italent.cn/#/open-document?menu=document-center&id=e96a007d-7265-4e43-8b96-a051c9208269 <p>
 *
 * @author: liuhong
 * @date: 2022/9/13 00:35
 */
@Data
public class BeisenRankV5DTO {
    private String name;
    @JsonProperty("oId")
    private String oId;
    private String startDate;
    private String stopDate;
    private Integer status;
    @JsonProperty("oIdResourceSet")
    private String oIdResourceSet;
    @JsonProperty("oIdJobGradeLow")
    private String oIdJobGradeLow;
    @JsonProperty("oIdJobGradeHigh")
    private String oIdJobGradeHigh;
    @JsonProperty("oIdJobLevelType")
    private String oIdJobLevelType;
    private Integer level;
    private Integer businessModifiedBy;
    private String businessModifiedTime;
    private String orderCode;
    private String objectId;
    private String customProperties;
    private String translateProperties;
    private Integer createdBy;
    private String createdTime;
    private Integer modifiedBy;
    private String modifiedTime;
    private boolean stdIsDeleted;

}
