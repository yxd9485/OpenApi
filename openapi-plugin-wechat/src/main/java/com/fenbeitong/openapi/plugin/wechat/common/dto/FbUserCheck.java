package com.fenbeitong.openapi.plugin.wechat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by dave.hansins on 19/12/25.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FbUserCheck {

    private String fbUserId;
    private String fbUserPhone;
    private String thirdUserId;
    private String fbUserName;
    private boolean isFbUser;


}
