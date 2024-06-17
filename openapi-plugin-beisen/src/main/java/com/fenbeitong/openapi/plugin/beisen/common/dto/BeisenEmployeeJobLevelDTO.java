package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 北森的职级数据结构
 *
 * @author xiaowei
 * @date 2020/08/06
 */
@Data
public class BeisenEmployeeJobLevelDTO {

    @JsonProperty("Total")
    private Integer total;
    @JsonProperty("Data")
    private List<BeisenEmployeeJobLevelListDTO> data;
    @JsonProperty("Code")
    private int code;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Extra")
    private String extra;


}
