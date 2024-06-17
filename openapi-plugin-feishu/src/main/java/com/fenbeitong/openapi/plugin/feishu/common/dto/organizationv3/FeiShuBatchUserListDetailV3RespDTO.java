package com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuRespDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 飞书批量人员详情DTO
 * @author zhangpeng
 * @date 2022/4/19 11:53 上午
 */
@Data
public class FeiShuBatchUserListDetailV3RespDTO extends FeiShuRespDTO {

    @JsonProperty("data")
    private DataDTO data;

    @Data
    public static class DataDTO {
        @JsonProperty("has_more")
        private Boolean hasMore;
        @JsonProperty("page_token")
        private String pageToken;
        @JsonProperty("items")
        private List<ItemsDTO> items;

        @Data
        public static class ItemsDTO {
            @JsonProperty("union_id")
            private String unionId;
            @JsonProperty("user_id")
            private String userId;
            @JsonProperty("open_id")
            private String openId;
            @JsonProperty("name")
            private String name;
            @JsonProperty("en_name")
            private String enName;
            @JsonProperty("nickname")
            private String nickname;
            @JsonProperty("email")
            private String email;
            @JsonProperty("mobile")
            private String mobile;
            @JsonProperty("mobile_visible")
            private Boolean mobileVisible;
            @JsonProperty("gender")
            private Integer gender;
            @JsonProperty("avatar_key")
            private String avatarKey;
            @JsonProperty("avatar")
            private AvatarDTO avatar;
            @JsonProperty("status")
            private StatusDTO status;
            @JsonProperty("department_ids")
            private List<String> departmentIds;
            @JsonProperty("leader_user_id")
            private String leaderUserId;
            @JsonProperty("city")
            private String city;
            @JsonProperty("country")
            private String country;
            @JsonProperty("work_station")
            private String workStation;
            @JsonProperty("join_time")
            private Integer joinTime;
            @JsonProperty("is_tenant_manager")
            private Boolean isTenantManager;
            @JsonProperty("employee_no")
            private String employeeNo;
            @JsonProperty("employee_type")
            private Integer employeeType;
            @JsonProperty("orders")
            private List<OrdersDTO> orders;
            @JsonProperty("custom_attrs")
            private List<CustomAttrsDTO> customAttrs;
            @JsonProperty("enterprise_email")
            private String enterpriseEmail;
            @JsonProperty("job_title")
            private String jobTitle;
            @JsonProperty("is_frozen")
            private Boolean isFrozen;

            @JsonProperty("custom_attr_infos")
            private Map<String, Object> customAttrInfos;

//            @Data
//            public static class AvatarDTO {
//                @JsonProperty("avatar_72")
//                private String avatar72;
//                @JsonProperty("avatar_240")
//                private String avatar240;
//                @JsonProperty("avatar_640")
//                private String avatar640;
//                @JsonProperty("avatar_origin")
//                private String avatarOrigin;
//            }

            @Data
            public static class OrdersDTO {
                @JsonProperty("department_id")
                private String departmentId;
                @JsonProperty("user_order")
                private Integer userOrder;
                @JsonProperty("department_order")
                private Integer departmentOrder;
            }

//            @Data
//            public static class CustomAttrsDTO {
//                @JsonProperty("type")
//                private String type;
//                @JsonProperty("id")
//                private String id;
//                @JsonProperty("value")
//                private ValueDTO value;
//
//                @Data
//                public static class ValueDTO {
//                    @JsonProperty("text")
//                    private String text;
//                    @JsonProperty("url")
//                    private String url;
//                    @JsonProperty("pc_url")
//                    private String pcUrl;
//                    @JsonProperty("option_id")
//                    private String optionId;
//                    @JsonProperty("option_value")
//                    private String optionValue;
//                    @JsonProperty("name")
//                    private String name;
//                    @JsonProperty("picture_url")
//                    private String pictureUrl;
//                    @JsonProperty("generic_user")
//                    private GenericUserDTO genericUser;
//
//                    @Data
//                    public static class GenericUserDTO {
//                        @JsonProperty("id")
//                        private String id;
//                        @JsonProperty("type")
//                        private Integer type;
//                    }
//                }
//            }
        }
    }
}
