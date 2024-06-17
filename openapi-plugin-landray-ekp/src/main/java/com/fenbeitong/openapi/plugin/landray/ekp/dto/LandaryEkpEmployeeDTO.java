package com.fenbeitong.openapi.plugin.landray.ekp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * create on 2021-01-27 19:31:11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LandaryEkpEmployeeDTO {

    private String no;

    private Boolean isAvailable;

    private String parent;

    private String alterTime;

    private String hierarchyId;

    private Map<String, Object> customProps;

    private String sex;

    private String lunid;

    private String mobileNo;

    private String type;

    private List<String> posts;

    private String password;

    private String loginName;

    private String name;

    private String id;

    private Langprops langProps;

    private String email;

    private String keyword;



    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Langprops {

        private String fdNameJP;

        private String fdName;

        private String fdNameCN;

    }
}