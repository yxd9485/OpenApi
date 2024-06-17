package com.fenbeitong.openapi.plugin.customize.archive.yihai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: YiHaiConfigDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021-05-17 10:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YiHaiArchiveProjectDTO {

    @JsonProperty("STATUS")
    public String STATUS;

    @JsonProperty("MSG")
    public String MSG;

    @JsonProperty("ITEMS")
    public List<Bean> ITEMS;

    @Data
    public static class Bean {
        // 控制范围  限制为8000
        @JsonProperty("KOKRS")
        public String KOKRS;
        // 成本中心
        @JsonProperty("KOSTL")
        public String KOSTL;
        // 更改日期
        @JsonProperty("UDATE")
        public String UDATE;
        // 更改时间
        @JsonProperty("UTIME")
        public String UTIME;
        // 成本中心名称
        @JsonProperty("KTEXT")
        public String KTEXT;
        // 有效截止日
        @JsonProperty("DATBI")
        public String DATBI;
        // 有效期始日
        @JsonProperty("DATAB")
        public String DATAB;
        // 公司代码
        @JsonProperty("BUKRS")
        public String BUKRS;
        // 成本中心类型
        @JsonProperty("KOSAR")
        public String KOSAR;
        // 负责人
        @JsonProperty("VERAK")
        public String VERAK;
        // 功能范围
        @JsonProperty("FUNCAREA")
        public String FUNCAREA;

        // X 无效
        @JsonProperty("BKZKP")
        public String BKZKP;



    }
}
