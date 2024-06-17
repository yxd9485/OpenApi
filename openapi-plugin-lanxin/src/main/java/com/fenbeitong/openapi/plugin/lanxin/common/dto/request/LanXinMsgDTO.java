package com.fenbeitong.openapi.plugin.lanxin.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: LanXinMsgDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/8 4:24 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LanXinMsgDTO {

    private List<String> userIdList;
    private List<String> departmentIdList;
    private String msgType;
    private Msgdata msgData;
    private String entryId;
    private String accountId;
    private String attach;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Msgdata {

        private Linkcard linkCard;


    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Linkcard {
        private String title;
        private String description;
        private String iconLink;
        private String link;
        private String fromName;
        private String fromIconLink;
    }
}
