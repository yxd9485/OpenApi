package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName QiqiProjectReqDTO
 * @Description 项目dto
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/25
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiProjectReqDTO {
    /**
     * 项目分类
     */
    @JsonProperty("categoryId")
    private String categoryId;
    /**
     * 项目负责人
     */
    @JsonProperty("ownerUserId")
    private String ownerUserId;
    /**
     * ID
     */
    @JsonProperty("id")
    private String id;
    /**
     * 编码
     */
    @JsonProperty("code")
    private String code;
    /**
     * 名称
     */
    @JsonProperty("name")
    private String name;
    /**
     * 描述
     */
    @JsonProperty("descrtption")
    private String descrtption;
    /**
     * 开始时间(计划开始时间的时间戳)
     */
    @JsonProperty("planStartDate")
    private Long planStartDate;
    /**
     * 结束时间(计划结束日期的时间戳)
     */
    @JsonProperty("planEndDate")
    private Long planEndDate;
    /**
     * 成员信息
     */
    @JsonProperty("membersObject")
    private List<QiqiProjectMemberDTO> membersObject;

    /**
     * 单据状态
     */
    @JsonProperty("billStatus")
    private String billStatus;
}
