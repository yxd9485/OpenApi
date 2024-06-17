package com.fenbeitong.openapi.plugin.kingdee.customize.xindi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 金蝶提交数据结构
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KingdeeCommitDataDTO {

    @JsonProperty("formid")
    private String formId = "ER_ExpReimbursement_Travel";
    @JsonProperty("data")
    private Resource data;

    //模型数据
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Resource {
        //数据模块给入默认值
        @JsonProperty("CreateOrgId")
        private String createOrgId;
        //单据编码集合，数组类型，格式：[No1,No2,...]（使用编码时必录）
        @JsonProperty("Numbers")
        private List<String> numbers = new ArrayList<>();
        //单据内码集合，字符串类型，格式："Id1,Id2,..."（使用内码时必录）
        @JsonProperty("Ids")
        private String id;
        @JsonProperty("SelectedPostId")
        private int selectedPostId = 0;

    }

}
