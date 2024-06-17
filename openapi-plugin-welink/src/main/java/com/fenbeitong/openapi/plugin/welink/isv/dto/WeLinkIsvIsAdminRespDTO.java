package com.fenbeitong.openapi.plugin.welink.isv.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * create on 2020-04-15 17:47:10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeLinkIsvIsAdminRespDTO {

    private String code;

    private String message;

    @JsonSetter("isAdmin")
    private boolean isAdmin;

    private List<String> roles;


}