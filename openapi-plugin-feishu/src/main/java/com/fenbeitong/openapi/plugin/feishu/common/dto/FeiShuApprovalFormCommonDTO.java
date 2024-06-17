package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

/**
 * <p>Title: FeiShuApprovalFormCommonDTO<p>
 * <p>Description: 飞书表单信息DTO,json转换时，已经根据不同的控件类型，进行对象转换<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/8/29 17:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeiShuApprovalFormCommonDTO {
    /**
     * 控件id
     */
    @JsonProperty("id")
    private String id;
    /**
     * 控件名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 控件类型
     */
    @JsonProperty("type")
    private String type;
    /**
     * 控件数据
     */
    @JsonProperty("value")
    private Object value;

    public void setType(String type) {
        this.type = type;
        if (ObjectUtils.isNotEmpty(value)){
            buildValueByType(type,value);
        }
    }

    public void setValue(Object value) {
        this.value = value;
        if (!StringUtils.isBlank(type)){
            buildValueByType(type,value);
        }
    }

    private void buildValueByType(String type, Object value) {
        if (StringUtils.isBlank(type) || ObjectUtils.isEmpty(value)){
            return;
        }
        switch(type){
            case FeiShuConstant.APPROVAL_FORM_TYPE_OUT_GROUP:
                this.value = JsonUtils.toObj(JsonUtils.toJson(value),OutGroupValue.class);
                return;
            case FeiShuConstant.APPROVAL_FORM_TYPE_DATE_INTERVAL:
                this.value = JsonUtils.toObj(JsonUtils.toJson(value),DateIntervalValue.class);
                return;
            case FeiShuConstant.APPROVAL_FORM_TYPE_FIELD_LIST:
                //明细控件里面不会嵌套明细控件，所以只会单次进入该分支逻辑
                this.value = JsonUtils.toObj(JsonUtils.toJson(value), new TypeReference<List<List<FeiShuApprovalFormCommonDTO>>>() {});
                return;
            default:
        }
    }

    /**
     * 外出类型控件
     */
    @Data
    public static class OutGroupValue{
        /**
         * 结束时间
         */
        private String end;
        /**
         * 开始时间
         */
        private String start;
        /**
         * 图片
         */
        private List<String> image;
        /**
         * 外出时长
         */
        private String interval;
        /**
         * 单位
         */
        private String unit;
        /**
         * 外出类型
         */
        private String name;

        /**
         * 外出事由
         */
        private String reason;
    }

    /**
     * 日期区间类型控件
     */
    @Data
    public static class DateIntervalValue{
        /**
         * 开始时间
         */
        private String start;
        /**
         * 结束时间
         */
        private String end;
        /**
         * 时长
         */
        private Integer interval;
    }


}
