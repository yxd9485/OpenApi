package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: FxkGetCustomDataListReqDTO</p>
 * <p>Description: 纷享销客获取自定义对象数据列表请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 7:44 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxkGetCustomDataListReqDTO {

    /**
     * 企业应用访问公司合法性凭证
     */
    public String corpAccessToken;

    /**
     * 开放平台公司账号
     */
    public String corpId;

    /**
     * 当前操作人的openUserId
     */
    public String currentOpenUserId;

    /**
     * 过滤条件
     */
    public FxkGetCustomDataListCondition data;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FxkGetCustomDataListCondition {

        /**
         * 对象的api_name
         */
        public String dataObjectApiName;

        /**
         * 查询条件列表
         */
        @JsonProperty("search_query_info")
        public SearchQueryInfo searchqueryInfo;

    }

    @Data
    public static class SearchQueryInfo {

        /**
         * 偏移量，从0开始，数值必须为limit的整数倍
         */
        public Integer offset;

        /**
         * 查询数据返回的条数，最大值为100
         */
        public Integer limit;

        /**
         * 过滤条件列表
         */
        public List<FxkGetCustomDataListFilter> filters;

        /**
         * 排序
         */
        public List<FxkGetCustomDataListOrder> orders;

        /**
         * 返回字段列表
         */
        public List<String> fieldProjection;

    }

    @Data
    public static class FxkGetCustomDataListFilter {

        /**
         * 字段名
         */
        @JsonProperty("field_name")
        public String fieldName;

        /**
         * 取值范围
         */
        @JsonProperty("field_values")
        public List<String> fieldValues;

        /**
         * 支持操作
         */
        public String operator;
    }

    @Data
    public static class FxkGetCustomDataListOrder {

        /**
         * 字段名
         */
        public String fieldName;

        /**
         * 如果是ture，按照升序排列，如果是false，则按照倒序排列
         */
        public Boolean isAsc;
    }
}
