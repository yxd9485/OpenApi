package com.fenbeitong.openapi.plugin.func.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: FuncThirdInfoExpressDTO</p>
 * <p>Description: 三方信息表达式</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/6 7:28 PM
 */
@Data
public class FuncThirdInfoExpressDTO {

    private List<ThirdCommonExpress> userExpressList;

    private List<ThirdCommonExpress> deptExpressList;

    private List<ThirdCostExpress> costExpressList;

    private List<ThirdCommonExpress> applyExpressList;

    private List<ThirdUserPhoneExpress> userPhoneExpressList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThirdCommonExpress {

        private String express;

        private String tgtField;

        private String group;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThirdCostExpress {

        private String id;

        private String tgtField;

        /**
         * 费用归属类型 1:部门 2:项目
         */
        private int costCategory;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThirdUserPhoneExpress {

        private String phone;

        private String tgtDept;

        private String tgtUser;
    }
}
