package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QiqiProjectMemberDTO
 * @Description 企企项目团队dto
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/6/14
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiProjectMemberDTO {
    /**
     * ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 人员id
     */
    @JsonProperty("userId")
    private String userId;
}
