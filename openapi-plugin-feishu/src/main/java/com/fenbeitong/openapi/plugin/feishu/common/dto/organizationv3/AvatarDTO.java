package com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 飞书新版人员头像信息
 * @author zhangpeng
 * @date 2022/4/20 1:44 下午
 */
@Data
public class AvatarDTO {

    @JsonProperty("avatar_72")
    private String avatar72;
    @JsonProperty("avatar_240")
    private String avatar240;
    @JsonProperty("avatar_640")
    private String avatar640;
    @JsonProperty("avatar_origin")
    private String avatarOrigin;

}
