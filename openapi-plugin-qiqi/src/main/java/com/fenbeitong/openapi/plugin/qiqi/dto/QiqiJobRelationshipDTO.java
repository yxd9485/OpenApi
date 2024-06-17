package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName JobRelationship
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/12
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiJobRelationshipDTO implements Comparable<QiqiJobRelationshipDTO>{
    /**
     * id
     */
    @JsonProperty("id")
    private String id;
    /**
     * 人员类型
     */
    @JsonProperty("userCategoryId")
    private String userCategoryId;
    /**
     * 是否主职
     */
    @JsonProperty("isMain")
    private boolean isMain;

    /**
     * 任职结束时间
     */
    @JsonProperty("disabledTime")
    private Long disabledTime;

    @Override
    public int compareTo(QiqiJobRelationshipDTO s) {
        int num = this.disabledTime - s.disabledTime > 0 ? 0 : -1;
        return num;
    }
}
