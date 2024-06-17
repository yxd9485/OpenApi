package com.fenbeitong.openapi.plugin.landray.ekp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2021-01-27 15:24:40
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LandaryEkpDepartmentInfoDTO {

    private String no;

    private Boolean isAvailable;

    private String parent;

    private String alterTime;

    private String hierarchyId;

    private String lunid;

    private String name;

    private String id;

    private String type;

    private Langprops langProps;

    private String thisLeader;

    private String order;


    @Data
    public static class Langprops {

        private String fdNameJP;

        private String fdName;

        private String fdNameCN;

    }

}