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
 * <p>Description: 人员</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-15 14:59
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRespDto {


    @JsonProperty("code")
    public String code;

    @JsonProperty("msg")
    public String msg;

    @JsonProperty("data")
    public DataBean data;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
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
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Labellist {

            @JsonProperty("field_name")
            public String fieldName;

            @JsonProperty("label")
            public String label;

            @JsonProperty("type")
            public String type;
        }


        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ListBean {

            @JsonProperty("path")
            public String path;

            @JsonProperty("update_time")
            public String updateTime;

            @JsonProperty("id_no")
            public String idNo;

            @JsonProperty("create_time")
            public String createTime;

            @JsonProperty("department_id")
            public String departmentId;

            @JsonProperty("telephone_county_code")
            public String telephoneCountyCode;

            @JsonProperty("id_type")
            public String idType;

            @JsonProperty("telephone")
            public String telephone;

            @JsonProperty("id_no_id_type")
            public String idNoIdType;

            @JsonProperty("department")
            public String department;

            @JsonProperty("uuid")
            public String uuid;

            @JsonProperty("realname")
            public String realname;

            @JsonProperty("employee_status_id")
            public String employeeStatusId;

            @JsonProperty("duty_level")
            public String dutyLevel;

            @JsonProperty("duty_level_id")
            public Integer dutyLevelId;
        }

    }
}
