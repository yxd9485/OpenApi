package com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuRespDTO;
import lombok.Data;

import java.util.List;

/**
 * 飞书子部门列表DTO
 * @author zhangpeng
 * @date 2022/4/19 2:07 下午
 */

@Data
public class FeiShuSubDepartmentListRespDTO extends FeiShuRespDTO {

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
            @JsonProperty("name")
            private String name;
            @JsonProperty("i18n_name")
            private I18nNameDTO i18nName;
            @JsonProperty("parent_department_id")
            private String parentDepartmentId;
            @JsonProperty("department_id")
            private String departmentId;
            @JsonProperty("open_department_id")
            private String openDepartmentId;
            @JsonProperty("leader_user_id")
            private String leaderUserId;
            @JsonProperty("chat_id")
            private String chatId;
            @JsonProperty("order")
            private String order;
            @JsonProperty("unit_ids")
            private List<String> unitIds;
            @JsonProperty("member_count")
            private Integer memberCount;
            @JsonProperty("status")
            private StatusDTO status;
            @JsonProperty("create_group_chat")
            private Boolean createGroupChat;

            @Data
            public static class I18nNameDTO {
                @JsonProperty("zh_cn")
                private String zhCn;
                @JsonProperty("ja_jp")
                private String jaJp;
                @JsonProperty("en_us")
                private String enUs;
            }

            @Data
            public static class StatusDTO {
                @JsonProperty("is_deleted")
                private Boolean isDeleted;
            }
        }
    }
}
