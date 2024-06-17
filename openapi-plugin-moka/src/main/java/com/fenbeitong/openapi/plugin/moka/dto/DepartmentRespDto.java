package com.fenbeitong.openapi.plugin.moka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: DepartmentRespDto</p>
 * <p>Description: 部门</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-15 14:59
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentRespDto {


    @JsonProperty("code")
    public String code;

    @JsonProperty("msg")
    public String msg;

    @JsonProperty("data")
    public DataBean data;

    @Data
    public static class DataBean {

        @JsonProperty("list")
        public List<ListBean> list;

        @JsonProperty("total")
        public String total;

        @JsonProperty("label_list")
        public List<Labellist> labelList;

        @JsonProperty("rank_categories")
        public String rankCategories;

        @JsonProperty("groups")
        public String groups;

        @JsonProperty("position_class")
        public String positionClass;

        @JsonProperty("sub_class")
        public String subClass;

        @Data
        public static class Labellist {

            @JsonProperty("field_name")
            public String fieldName;

            @JsonProperty("label")
            public String label;

            @JsonProperty("type")
            public String type;
            
        }

        @Data
        public static class ListBean {

            @JsonProperty("update_time")
            public String updateTime;

            @JsonProperty("create_time")
            public String createTime;

            @JsonProperty("node_uid")
            public String nodeUid;

            @JsonProperty("node_code")
            public String nodeCode;

            @JsonProperty("dept_sample_name")
            public String deptSampleName;

            @JsonProperty("dept_name")
            public String deptName;

            @JsonProperty("superior_dept")
            public String superiorDept;

            @JsonProperty("have_used")
            public String haveUsed;

            @JsonProperty("director_id")
            public Integer directorId;

            @JsonProperty("director")
            public String director;

        }

    }


    @Data
    public static class SuperiorDept {
        @JsonProperty("name")
        public String name;

        @JsonProperty("id")
        public String id;
    }

}
