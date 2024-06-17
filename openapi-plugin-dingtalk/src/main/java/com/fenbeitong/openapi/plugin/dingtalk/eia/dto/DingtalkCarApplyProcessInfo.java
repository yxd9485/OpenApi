package com.fenbeitong.openapi.plugin.dingtalk.eia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: DingtalkCarApplyProcessInfo</p>
 * <p>Description: 用车审批对象</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/24 5:35 PM
 */
@Data
public class DingtalkCarApplyProcessInfo extends DingtalkTripApplyProcessInfo {

    private List<UseCarApplyRule> applyTaxiRuleInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UseCarApplyRule {

        private String type;

        private Object value;
    }

}
