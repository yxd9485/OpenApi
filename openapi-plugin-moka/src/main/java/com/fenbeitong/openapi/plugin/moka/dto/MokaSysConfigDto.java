package com.fenbeitong.openapi.plugin.moka.dto;

import lombok.Data;


@Data
public class MokaSysConfigDto {

    public EmlpoyeeBean emlpoyee;
    public DepartmentBean department;
    public EmlpoyeeOtherBean emlpoyeeOtherBean;
    public String userName;
    public String authorization;

    @Data
    public static class EmlpoyeeBean {
        public String entCode;
        public String apiCode;
        public String url;

    }

    @Data
    public static class DepartmentBean {
        public String entCode;
        public String apiCode;
        public String url;
    }

    @Data
    public static class EmlpoyeeOtherBean {
        public String entCode;
        public String apiCode;
        public String url;
    }
}
