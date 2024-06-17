package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:钉钉用车表单城市数据
 * @Author: xiaohai
 * @Date: 2021/4/17 下午6:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DingtalkIsvCityDTO {

    @JsonProperty("province")
    private City province;

    @JsonProperty("city")
    private City city;

    @JsonProperty("district")
    private City district;

    @Data
    public static class City{

        //城市编码
        @JsonProperty("id")
        private String id;

        //城市名称
        @JsonProperty("name")
        private String name;
    }

}
