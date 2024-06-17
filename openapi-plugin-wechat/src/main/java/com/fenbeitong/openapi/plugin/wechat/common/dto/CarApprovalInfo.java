package com.fenbeitong.openapi.plugin.wechat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by dave.hansins on 19/12/21.
 */
@Data
public class CarApprovalInfo extends ApprovalInfo {

    private List<CarApplyRule> applyTaxiRuleInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CarApplyRule {

        private String type;

        private Object value;
    }



}
