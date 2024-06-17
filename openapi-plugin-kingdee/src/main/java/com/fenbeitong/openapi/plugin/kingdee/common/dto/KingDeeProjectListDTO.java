package com.fenbeitong.openapi.plugin.kingdee.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: ProjectListDTo</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-08-26 14:56
 */
@Data
public class KingDeeProjectListDTO {

    @JsonProperty("code")
    public Integer code;

    @JsonProperty("message")
    public String message;

    @JsonProperty("data")
    public List<Iteam> data;

    @JsonProperty("meaning")
    public String meaning;


    @Data
    public static class Iteam{
        @JsonProperty("projectNo")
        public String projectNo;

        @JsonProperty("name")
        public String name;
    }

}
