package com.fenbeitong.openapi.plugin.customize.yuntianlifei.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: projectDto</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/4/26 5:46 下午
 */
@NoArgsConstructor
@Data
public class YunTianProjectDto {


    private Integer code;
    private List<DataBean> data;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        private List<MembersBean> members;
        private String name;
        private String pmId;
        private String pmName;
        private String projectNo;
        private Integer status;

        @NoArgsConstructor
        @Data
        public static class MembersBean {
            private String id;
            private String name;
        }
    }
}
