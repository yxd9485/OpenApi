package com.fenbeitong.openapi.plugin.beisen.common.dto;

import lombok.Data;

/**
 * <p>Title: BeisenResultEntity</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/08/03 4:53 PM
 */
@Data
public class BeiSenCorporation {
    public Fields fields;

    @Data
    public static class Fields {
        public String Alias;
        public String Code;
        public String Name;
        public String OId;
    }
}
