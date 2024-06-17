package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: BeisenReqBaseDTO<p>
 * <p>Description: 请求北森接口请求基类<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/9/12 20:37
 */
@Data
public class BeisenReqBaseDTO {
    /**
     * 时间窗查询类型  1修改时间、2业务修改时间 ModifiedTime:修改时间，系统修改同步更新该时间 BusinessModifiedTime:业务修改时间，系统修改不同步更新该时间
     */
    @JsonProperty("timeWindowQueryType")
    private Integer timeWindowQueryType;
    /**
     * 时间范围开始时间，格式：2021-01-01T00:00:00
     */
    @JsonProperty("startTime")
    private String startTime;
    /**
     * 时间范围结束时间，格式：2021-01-02T00:00:00
     */
    @JsonProperty("stopTime")
    private String stopTime;
    /**
     * 每批次查询个数，默认100个
     */
    @JsonProperty("capacity")
    private Integer capacity;
    /**
     * 查询字段列表
     */
    @JsonProperty("columns")
    private String[] columns;
    /**
     * 自定义字段查询条件 多个条件使用and且关系，不支持or或关系。示例：[{"fieldName": "extExtQueryFloat_127666_832132060","queryType": 5,"values": ["1"]}]
     */
    @JsonProperty("extQueries")
    private List<ExtQuery> extQueries;
    /**
     * 是否包括已删除数据，默认否，示例：true/false
     */
    @JsonProperty("isWithDeleted")
    private Boolean isWithDeleted;
    /**
     * 是否开启动态翻译，默认否，示例：true/false
     */
    @JsonProperty("enableTranslate")
    private Boolean enableTranslate;
    /**
     * 本批次的ScrollId，第一次查询为空，后续为上次结果返回的ScrollId
     */
    @JsonProperty("scrollId")
    private String scrollId;

    @Data
    @Builder
    public static class ExtQuery {
        /**
         * 字段名称，示例：extExtQueryFloat_127666_832132060，严格区分大小写
         */
        @JsonProperty("fieldName")
        private String fieldName;
        /**
         * 自定义字段查询类型条：1大于、2大于等于、3小于、4小于等于、5等于、6不等于、7区间。仅日期时间或者数值类型支持大小比较、区间查询 Larger:大于 LargerOrEqual:大于等于 Smaller:小于 SmallerOrEqual:小于等于 Equal:等于 NotEqual:不等于 Between:区间
         */
        @JsonProperty("queryType")
        private int queryType;
        /**
         * 值列表，至少包含一个元素。若条件类型为1大于、2大于等于、3小于、4小于等于时，则只能包含一个值。若条件类型为7区间，则只能包含两个值。若为等于或者不等于，可最大支持300个值，且可使用null或者空字符串进行非空或空判断。
         */
        @JsonProperty("values")
        private String[] values;
    }
}
