package com.fenbeitong.openapi.plugin.ecology.v8.kailin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 开林行程信息
 *
 * @author lizhen
 */
@Data
public class KaiLinTripApplyDetailDTO {

    /**
     * 交通工具
     */
    private String jtgjx;

    /**
     * 单程往返;0单程;1往返
     */
    private String dcwfnew;

    /**
     * 出发城市
     */
    private String cfcs;

    /**
     * 出发城市_show_value
     */
    @JsonProperty("cfcs_show_value")
    private String cfcsShowValue;

    /**
     * 目的城市
     */
    private String mdcs;

    /**
     * 目的城市_show_value
     */
    @JsonProperty("mdcs_show_value")
    private String mdcsShowValue;

    /**
     * 开始时间
     */
    private String ksrq;

    /**
     * 结束时间
     */
    private String jsrq;

    public String getCfcsShowValue() {
        String substring = cfcsShowValue.substring(0, cfcsShowValue.indexOf("<"));
        if (substring.contains("市")) {
            substring = substring.substring(0, substring.indexOf("市"));
        }
        return substring;
    }

    public String getMdcsShowValue() {
        String substring = mdcsShowValue.substring(0, mdcsShowValue.indexOf("<"));
        if (substring.contains("市")) {
            substring = substring.substring(0, substring.indexOf("市"));
        }
        return substring;
    }

}
