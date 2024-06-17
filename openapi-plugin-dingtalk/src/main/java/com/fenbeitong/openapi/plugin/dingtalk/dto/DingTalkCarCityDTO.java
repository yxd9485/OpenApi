package com.fenbeitong.openapi.plugin.dingtalk.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: CarCityDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-11-16 16:35
 */
@Data
public class DingTalkCarCityDTO {

    private List<Bean> rowValue;

    @Data
    public static class Bean {
        private String componentType;
        private String label;
        private String value;
        private String key;


    }
}
