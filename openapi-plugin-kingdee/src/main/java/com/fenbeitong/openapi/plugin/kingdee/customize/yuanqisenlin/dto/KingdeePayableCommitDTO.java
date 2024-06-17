package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ctl
 * @date 2021/7/5
 */
@Data
@NoArgsConstructor
public class KingdeePayableCommitDTO {

    /**
     * 表单id
     */
    @JsonProperty("formid")
    private String formId;

    /**
     * 数据包
     */
    @JsonProperty("data")
    private Resource data;

    @Data
    @NoArgsConstructor
    public static class Resource {
        //数据模块给入默认值
        @JsonProperty("CreateOrgId")
        private String createOrgId;
        //单据编码集合，数组类型，格式：[No1,No2,...]（使用编码时必录）
        @JsonProperty("Numbers")
        private List<String> numbers;
        //单据内码集合，字符串类型，格式："Id1,Id2,..."（使用内码时必录）
        @JsonProperty("Ids")
        private String id;
        //工作流发起员工岗位内码，整型（非必录） 注（员工身兼多岗时不传参默认取第一个岗位）
        @JsonProperty("SelectedPostId")
        private int selectedPostId;
    }
}
