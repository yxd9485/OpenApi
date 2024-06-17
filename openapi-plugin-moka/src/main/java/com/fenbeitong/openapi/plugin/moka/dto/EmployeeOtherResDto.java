package com.fenbeitong.openapi.plugin.moka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

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
@ApiModel
public class EmployeeOtherResDto {

    @JsonProperty("code")
    public String code;

    @JsonProperty("msg")
    public String msg;

    @JsonProperty("data")
    public DataBean data;

    @Data
    public static class DataBean {
        @JsonProperty("wecomUserId")
        public List<Wecomuserid> wecomUserId;
    }
    
    @Data
    public static class Wecomuserid {

        @JsonProperty("userid")
        public String userid;

        @JsonProperty("corpid")
        public String corpid;

        @JsonProperty("uuid")
        public Integer uuid;

        @JsonProperty("type")
        public Integer type;
    }

}
